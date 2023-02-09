package com.example.ft_hangouts.database

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val relation: String,
    val gender:String
    )