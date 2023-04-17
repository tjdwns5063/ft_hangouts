package com.example.ft_hangouts.ui.edit

import android.os.Handler
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.error.DatabaseUpdateErrorHandler
import com.example.ft_hangouts.ui.BaseViewModel

class ContactEditViewModel(private val handler: Handler, private val baseViewModel: BaseViewModel) {
    private val databaseDAO = ContactDatabaseDAO()

    fun updateContactById(rowId: Long, newContact: Contact) {
        BackgroundHelper.execute {
            try {
                databaseDAO.updateById(rowId, newContact)
            } catch (err: Exception) {
                handler.post { baseViewModel.submitHandler(DatabaseUpdateErrorHandler()) }
            } finally {
                handler.post { baseViewModel.submitHandler(DatabaseSuccessHandler()) }
            }
        }
    }
}