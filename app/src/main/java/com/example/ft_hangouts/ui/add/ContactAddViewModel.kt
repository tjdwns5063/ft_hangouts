package com.example.ft_hangouts.ui.add

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseDeleteErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.ui.BaseViewModel
import java.io.ByteArrayOutputStream

class ContactAddViewModel(
    private val handler: Handler,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
) {
    private val contactDAO = ContactDatabaseDAO()
    val profileImage: LiveData<Drawable>
        get() = _profileImage
    private val _profileImage = MutableLiveData<Drawable>()

    fun createContact(
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

    fun addContact(contact: Contact) {
        BackgroundHelper.execute {
            try {
                contactDAO.addItem(contact)
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseDeleteErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(DatabaseSuccessHandler()) }
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        BackgroundHelper.execute {
            try {
                val bitmapDrawable = imageDatabaseDAO.getImageFromUri(uri)
                handler.post { _profileImage.value = bitmapDrawable }
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseReadErrorHandler()) }
            }
        }
    }
}