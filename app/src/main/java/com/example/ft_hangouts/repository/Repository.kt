package com.example.ft_hangouts.repository

import com.example.ft_hangouts.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.sms_database.DatabaseDAO

abstract class Repository {
    abstract val databaseDAO: DatabaseDAO
}

class ContactRepository: Repository() {
    override val databaseDAO: ContactDatabaseDAO
        get() = ContactDatabaseDAO()
}