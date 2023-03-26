package com.example.ft_hangouts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ft_hangouts.database.createDatabase
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val dbHelper = createDatabase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", -1)
        Toast.makeText(this, "$id", Toast.LENGTH_SHORT).show()
    }
}