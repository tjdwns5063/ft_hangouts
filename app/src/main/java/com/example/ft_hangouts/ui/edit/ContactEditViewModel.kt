package com.example.ft_hangouts.ui.edit

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ft_hangouts.data.contact_database.*
import com.example.ft_hangouts.error.DatabaseReadErrorHandler
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import com.example.ft_hangouts.data.ImageDAO
import com.example.ft_hangouts.util.compressBitmapToByteArray

class ContactEditViewModel(
    private val contactDAO: ContactDAO,
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val imageDAO: ImageDAO
    ): ViewModel() {

    private val _contact = MutableStateFlow<Contact>(
        Contact(-1, "", "", "", "", "")
    )
    val contact: StateFlow<Contact> = _contact.asStateFlow()

    init {
        viewModelScope.launch {
            initViewModel()
        }
    }
    suspend fun initViewModel() {
        getContactById(id)
    }

    private suspend fun getContactById(id: Long) = withContext(Dispatchers.IO) {
        try {
            _contact.value = contactDAO.getItemById(id)
            baseViewModel.submitHandler(DatabaseSuccessHandler())
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseReadErrorHandler().apply { this.updateTerminated(true) })
        }
    }

    private suspend fun updateContact(newContact: Contact) = withContext(Dispatchers.IO) {
        try {
            contactDAO.update(newContact)
            baseViewModel.submitHandler(DatabaseSuccessHandler().apply { this.updateTerminated(true) })
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseUpdateErrorHandler())
        }
    }

    suspend fun updateProfileImage(url: String) {
        val bitmap = imageDAO.loadBitmap(url)

        _contact.value = _contact.value.copy(profile = compressBitmapToByteArray(bitmap, imageDAO.density))
    }

    fun updateContact(
        name: String,
        phoneNumber: String,
        email: String,
        gender: String,
        relation: String
    ) {

        viewModelScope.launch {
            val newContact = Contact(id, name, phoneNumber, email, relation, gender, _contact.value.profile)

            updateContact(newContact)
        }
    }

    fun clearProfileImage() {
        _contact.value = _contact.value.copy(profile = null)
    }
}

class EditViewModelFactory(
    private val id: Long,
    private val baseViewModel: BaseViewModel,
    private val database: ContactDatabase,
    private val imageDAO: ImageDAO
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
    ): T {
        return ContactEditViewModel(
                database.contactDao(),
                id,
                baseViewModel,
                imageDAO,
            ) as T
    }
}