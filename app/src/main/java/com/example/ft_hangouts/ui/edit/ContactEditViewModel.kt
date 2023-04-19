package com.example.ft_hangouts.ui.edit

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel

class ContactEditViewModel(
    id: Long,
    private val handler: Handler,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
    ) {
    private val contactDatabaseDAO = ContactDatabaseDAO()

    val contact: LiveData<ContactDomainModel>
        get() = _contact
    private val _contact = MutableLiveData<ContactDomainModel>()

    val updatedProfile: LiveData<Drawable>
        get() = _updatedProfile
    private val _updatedProfile = MutableLiveData<Drawable>()

    init {
        getContactById(id)
    }

    private fun createContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ): Contact {
        val profileBitmap = (updatedProfile.value as? BitmapDrawable)?.bitmap
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

    private fun getContactById(id: Long) {
        BackgroundHelper.execute {
            try {
                val contact = contactToContactDomainModel(contactDatabaseDAO.getItemById(id))

                handler.post { _contact.value = contact }
            } catch (err: Exception) {
                baseViewModel.submitHandler(DatabaseReadErrorHandler())
            } finally {
                baseViewModel.submitHandler(null)
            }
        }
    }

    private fun updateContactById(rowId: Long, newContact: Contact) {
        BackgroundHelper.execute {
            try {
                contactDatabaseDAO.updateById(rowId, newContact)
                baseViewModel.submitHandler(DatabaseSuccessHandler())
            } catch (err: Exception) {
                baseViewModel.submitHandler(DatabaseUpdateErrorHandler())
            } finally {
                baseViewModel.submitHandler(null)
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        BackgroundHelper.execute {
            try {
                val bitmapDrawable = imageDatabaseDAO.getImageFromUri(uri)
                _updatedProfile.postValue(bitmapDrawable)
            } catch (err: Exception) {
                baseViewModel.submitHandler(DatabaseReadErrorHandler())
            }
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
        println("newContact: $newContact")
        updateContactById(contact.value!!.id, newContact)
    }
}