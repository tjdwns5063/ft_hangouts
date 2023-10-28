package com.example.ft_hangouts.ui.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.Profile
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseCreateErrorHandler
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.util.compressBitmapToByteArray
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactAddViewModel(
    private val contactDAO: ContactDAO,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
): ViewModel() {
    private val _profileImage = MutableStateFlow<Profile>(Profile(null))
    val profileImage: StateFlow<Profile> = _profileImage.asStateFlow()

    private suspend fun addContactToDatabase(contact: Contact) = withContext(Dispatchers.IO) {
        try {
            contactDAO.add(contact)
            baseViewModel.submitHandler(DatabaseSuccessHandler().apply { this.updateTerminated(true) })
        } catch (err: Exception) {
            println(err.message)
            baseViewModel.submitHandler(DatabaseCreateErrorHandler())
        }
    }

    fun addContact(name: String,
                   phoneNumber: String,
                   email: String,
                   gender: String,
                   relation: String
    ) {
        val contact = Contact(
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            gender = gender,
            relation = relation,
            profile = compressBitmapToByteArray(profileImage.value.bitmap)
        )
        viewModelScope.launch {
            addContactToDatabase(contact)
        }
    }

    private suspend fun updateProfileImage(uriString: String) = withContext(Dispatchers.IO) {
        try {
            _profileImage.value = imageDatabaseDAO.loadImageIntoProfile(uriString)
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler())
        }
    }

    fun updateProfile(uriString: String) {
        viewModelScope.launch {
            updateProfileImage(uriString)
        }
    }

    fun clearProfileImage() {
        _profileImage.value = Profile(null)
    }
}

class AddViewModelFactory(
    private val database: ContactDatabase,
    private val baseViewModel: BaseViewModel,
    private val imageDatabaseDAO: ImageDatabaseDAO
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactAddViewModel(database.contactDao(), baseViewModel, imageDatabaseDAO) as T
    }
}