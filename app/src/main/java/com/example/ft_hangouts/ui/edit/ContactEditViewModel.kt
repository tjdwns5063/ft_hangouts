package com.example.ft_hangouts.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.data.contact_database.Profile
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.util.compressBitmapToByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope

class ContactEditViewModel(
    private val contactDAO: ContactDAO,
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
    ): ViewModel() {

    private val _contact = MutableStateFlow<Contact>(
        Contact(-1, "", "", "", "", "")
    )
    val contact: StateFlow<Contact> = _contact.asStateFlow()

    private val _updatedProfile = MutableStateFlow<Profile>(Profile(null))
    val updatedProfile: StateFlow<Profile> = _updatedProfile.asStateFlow()

    init {
        init()
    }

    fun init() {
        viewModelScope.launch {
            getContactById(id)
            initProfile()
        }
    }

    private fun initProfile() {
        _updatedProfile.value = _contact.value.profile
    }

    private fun createContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ): ContactDto  {
        return ContactDto(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            gender = gender,
            relation = relation,
            profile = compressBitmapToByteArray(_updatedProfile.value.bitmap)
        )
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            _contact.value = Contact.from(contactDAO.getItemById(id))
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler().apply { this.updateTerminated(true) })
        }
    }

    private suspend fun updateContact(newContactDto: ContactDto) = withContext(Dispatchers.IO) {
        try {
            contactDAO.update(newContactDto)
            baseViewModel.submitHandler(DatabaseSuccessHandler().apply { this.updateTerminated(true) })
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseUpdateErrorHandler())
        }
    }

    private suspend fun updateProfileImageLogic(uriString: String) = withContext(Dispatchers.IO) {
        try {
            _updatedProfile.value = imageDatabaseDAO.loadImageIntoProfile(uriString)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    suspend fun updateProfileImage(uriString: String) {
        updateProfileImageLogic(uriString)
    }

    suspend fun updateContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ) {
        val newContact = createContact(name, phoneNumber, email, gender, relation)

        updateContact(newContact)
    }

    fun clearProfileImage() {
        _updatedProfile.value = Profile(null)
    }
}

class EditViewModelFactory(
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO,
    private val database: ContactDatabase
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
    ): T {
        return ContactEditViewModel(
                database.contactDao(),
                id,
                baseViewModel,
                imageDatabaseDAO
            ) as T
    }
}