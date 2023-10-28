package com.example.ft_hangouts.data.contact_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDAO {
    @Query("SELECT * FROM contact")
    fun getAllItems(): List<ContactDto>

    @Query("SELECT * FROM contact WHERE id = :id")
    fun getItemById(id: Long): ContactDto

    @Query("DELETE FROM contact WHERE id = :id")
    fun deleteById(id: Long)

    @Insert
    fun add(vararg contactDto: ContactDto)

    @Update
    fun update(vararg contactDto: ContactDto): Int

    @Query("SELECT * FROM contact WHERE name LIKE :name")
    fun search(name: String): List<ContactDto>
}




