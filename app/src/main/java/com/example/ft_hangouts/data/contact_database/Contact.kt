package com.example.ft_hangouts.data.contact_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "contact")
data class Contact(
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

        other as Contact

        if (id != other.id) return false
        if (name != other.name) return false
        if (phoneNumber != other.phoneNumber) return false
        if (email != other.email) return false
        if (relation != other.relation) return false
        if (gender != other.gender) return false
        if (profile != null) {
            if (other.profile == null) return false
            if (!profile.contentEquals(other.profile)) return false
        } else if (other.profile != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + relation.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (profile?.contentHashCode() ?: 0)
        return result
    }
}