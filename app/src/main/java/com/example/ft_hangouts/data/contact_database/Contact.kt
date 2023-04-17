package com.example.ft_hangouts.data.contact_database

import java.io.Serializable

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val relation: String,
    val gender:String
    ): Serializable