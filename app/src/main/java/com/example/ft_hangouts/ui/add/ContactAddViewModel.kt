package com.example.ft_hangouts.ui.add

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseCreateErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.*

class ContactAddViewModel(
    contactDatabaseDAO: ContactDatabaseDAO,
    private val lifecycleScope: CoroutineScope,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
) {
    private val contactDAO = contactDatabaseDAO
    val profileImage: LiveData<Drawable>
        get() = _profileImage
    private val _profileImage = MutableLiveData<Drawable>()

    private fun createContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ): Contact {
        val profileBitmap = (profileImage.value as? BitmapDrawable)?.bitmap
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

    private suspend fun addContactToDatabase(contact: Contact) = withContext(Dispatchers.IO) {
        try {
            contactDAO.addItem(contact)
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseCreateErrorHandler())
        }
    }

    fun addContact(name: String,
                   phoneNumber: String,
                   email: String,
                   gender: String,
                   relation: String
    ): Job {
        val contact = createContact(name, phoneNumber, email, gender, relation)

        return lifecycleScope.launch {
            addContactToDatabase(contact)
        }
    }

    private suspend fun updateProfileImage(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val bitmapDrawable = imageDatabaseDAO.getImageFromUri(uri)
            _profileImage.postValue(bitmapDrawable)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun updateProfile(uri: Uri) {
        lifecycleScope.launch {
            updateProfileImage(uri)
        }
    }
}