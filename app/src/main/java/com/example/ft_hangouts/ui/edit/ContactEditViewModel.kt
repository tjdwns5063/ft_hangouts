package com.example.ft_hangouts.ui.edit

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactEditViewModel(
    context: Context,
    id: Long,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
    ) {
    private val contactDatabaseDAO = ContactDatabaseDAO(ContactHelper.createDatabase(context))

    val contact: LiveData<ContactDomainModel>
        get() = _contact
    private val _contact = MutableLiveData<ContactDomainModel>()

    val updatedProfile: LiveData<Drawable>
        get() = _updatedProfile
    private val _updatedProfile = MutableLiveData<Drawable>()

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
        val profileBitmap = (updatedProfile.value as? BitmapDrawable)?.bitmap ?: contact.value?.profile

        return Contact(
            id = 0,
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            gender = gender,
            relation = relation,
            profile = ContactDatabaseDAO.compressBitmapToByteArray(profileBitmap)
        )
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            val contact = contactToContactDomainModel(contactDatabaseDAO.getItemById(id))

            _contact.postValue(contact)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    private suspend fun updateContactById(rowId: Long, newContact: Contact) = withContext(Dispatchers.IO) {
        try {
            contactDatabaseDAO.updateById(rowId, newContact)
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseUpdateErrorHandler())
        } finally {
            baseViewModel.submitHandler(null)
        }
    }

    private suspend fun updateProfileImageLogic(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val bitmapDrawable = imageDatabaseDAO.getImageFromUri(uri)
            _updatedProfile.postValue(bitmapDrawable)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun updateProfileImage(uri: Uri) {
        lifecycleScope.launch {
            updateProfileImageLogic(uri)
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
            updateContactById(contact.value!!.id, newContact)
        }
    }
}