package com.example.ft_hangouts.ui.edit

import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO

class ContactEditViewModel {
    private val databaseDAO = ContactDatabaseDAO()

    fun updateById(rowId: Long, newContact: Contact) {
        databaseDAO.updateById(rowId, newContact)
    }
}