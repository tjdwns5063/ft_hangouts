package com.example.ft_hangouts.data.contact_database

import android.graphics.Bitmap
import java.io.Serializable

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val relation: String,
    val gender:String,
    val profile: ByteArray? = null
    ): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (!profile.contentEquals(other.profile)) return false
        if (name != other.name) return false
        if (phoneNumber != other.phoneNumber) return false
        if (email != other.email) return false
        if (relation != other.relation) return false
        if (gender != other.gender) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + profile.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + relation.hashCode()
        result = 31 * result + gender.hashCode()
        return result
    }
}

data class ContactDomainModel(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val relation: String,
    val gender:String,
    val profile: Bitmap? = null,
) {
    @Override
    override fun equals(other: Any?): Boolean {
        if (other !is ContactDomainModel) return false

        val sameProfile = if (other.profile == null && this.profile == null) true else {
            other.profile?.let {
                other.profile.sameAs(this.profile)
            } ?: false
        }

        return other.id == this.id && other.name == this.name && other.phoneNumber == this.phoneNumber &&
                other.email == this.email && other.relation == this.relation && other.gender == this.gender &&
                sameProfile
    }
}

fun contactToContactDomainModel(contact: Contact): ContactDomainModel {
    return ContactDomainModel(
        id = contact.id,
        name = contact.name,
        phoneNumber = contact.phoneNumber,
        email = contact.email,
        relation = contact.relation,
        gender = contact.gender,
        profile = contact.profile?.let { ContactDatabaseDAO.decodeByteArrayToBitmap(it) }
    )
}

fun contactDomainModelToContact(contactDomainModel: ContactDomainModel): Contact {
    return Contact(
        id = contactDomainModel.id,
        name = contactDomainModel.name,
        phoneNumber = contactDomainModel.phoneNumber,
        email = contactDomainModel.email,
        relation = contactDomainModel.relation,
        gender = contactDomainModel.gender,
        profile = contactDomainModel.profile?.let { ContactDatabaseDAO.compressBitmapToByteArray(it) }
    )
}