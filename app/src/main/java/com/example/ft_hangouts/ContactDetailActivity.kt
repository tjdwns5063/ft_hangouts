package com.example.ft_hangouts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ft_hangouts.database.ContactDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {
    private val binding: ActivityContactDetailBinding by lazy { ActivityContactDetailBinding.inflate(layoutInflater) }
    private val contactDAO = ContactDatabaseDAO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", -1)

        contactDAO.getItemById(id)?.let {
            binding.detailNameValueText.text = it.name
            binding.detailPhoneNumberValueText.text = it.phoneNumber
            binding.detailEmailValueText.text = it.email
            binding.detailGenderValueText.text = it.gender
            binding.detailRelationValueText.text = it.relation
        }
    }
}