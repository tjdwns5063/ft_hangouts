package com.example.ft_hangouts.ui.edit

import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.data.contact_database.Profile
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.util.compressBitmapToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactEditViewModel(
    private val contactDatabaseDAO: ContactDatabaseDAO,
    id: Long,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
    ) {

    private val _contact = MutableStateFlow<ContactDomainModel>(
        ContactDomainModel(-1, "", "", "", "", "")
    )
    val contact: StateFlow<ContactDomainModel> = _contact.asStateFlow()

    private val _updatedProfile = MutableStateFlow<Profile>(Profile(null))
    val updatedProfile: StateFlow<Profile> = _updatedProfile.asStateFlow()

    init {
        lifecycleScope.launch {
            getContactById(id)
        }
    }

    private fun createContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ): Contact  {
        val profileBitmapDrawable= updatedProfile.value.bitmapDrawable

        return Contact(
            id = 0,
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            gender = gender,
            relation = relation,
            profile = compressBitmapToByteArray(profileBitmapDrawable?.bitmap)
        )
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            _contact.value = contactToContactDomainModel(contactDatabaseDAO.getItemById(id))
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler().apply { this.updateTerminated(true) })
        }
    }

    private suspend fun updateContactById(rowId: Long, newContact: Contact) = withContext(Dispatchers.IO) {
        try {
            contactDatabaseDAO.updateById(rowId, newContact)
            baseViewModel.submitHandler(DatabaseSuccessHandler().apply { this.updateTerminated(true) })
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseUpdateErrorHandler())
        }
    }

    private suspend fun updateProfileImageLogic(uriString: String) = withContext(Dispatchers.IO) {
        try {
            _updatedProfile.value = imageDatabaseDAO.getImageFromUri(uriString)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun updateProfileImage(uriString: String) {
        lifecycleScope.launch {
            updateProfileImageLogic(uriString)
        }
    }

    fun updateContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ) {
        val newContact = createContact(name, phoneNumber, email, gender, relation)

        lifecycleScope.launch {
            updateContactById(contact.value.id, newContact)
        }
    }

    fun clearProfileImage() {
        lifecycleScope.launch {
            _updatedProfile.value = Profile(null)
        }
    }
}