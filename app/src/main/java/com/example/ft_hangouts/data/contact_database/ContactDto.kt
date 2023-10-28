package com.example.ft_hangouts.data.contact_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ft_hangouts.util.decodeByteArrayToBitmap
import java.io.Serializable

@Entity(tableName = "contact")
data class ContactDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo("phone_number")
    val phoneNumber: String,
    val email: String,
    val relation: String,
    val gender:String,
    val profile: ByteArray? = null
    ): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactDto

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

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val relation: String,
    val gender:String,
    val profile: Profile = Profile(null),
) {
    @Override
    override fun equals(other: Any?): Boolean {
        if (other !is Contact) return false

        return other.id == this.id && other.name == this.name && other.phoneNumber == this.phoneNumber &&
                other.email == this.email && other.relation == this.relation && other.gender == this.gender &&
                other.profile.bitmap?.byteCount == this.profile.bitmap?.byteCount
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + profile.bitmap.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + relation.hashCode()
        result = 31 * result + gender.hashCode()
        return result
    }

    companion object {
        fun from(contactDto: ContactDto): Contact {
            return Contact(
                id = contactDto.id,
                name = contactDto.name,
                phoneNumber = contactDto.phoneNumber,
                email = contactDto.email,
                relation = contactDto.relation,
                gender = contactDto.gender,
                profile = Profile(contactDto.profile?.let { decodeByteArrayToBitmap(it) })
            )
        }
    }
}
