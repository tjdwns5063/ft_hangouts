package com.example.ft_hangouts.sms_database

import java.io.Serializable

data class Sms(
    val id: Long,
    val to: String,
    val from: String,
    val content: String,
    val date: Long
): Serializable