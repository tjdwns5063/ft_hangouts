package com.example.ft_hangouts

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.database.ContactContract
import com.example.ft_hangouts.database.createDatabase
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dbHelper = createDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val adapter = ContactRecyclerAdapter(dbHelper.getAllItems().toMutableList()) {
            // goto detail activity.
            val position = binding.contactRecyclerView.getChildLayoutPosition(it)
            goToDetailActivity(position.toLong() + 1)
        }

        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val addContactActivityResultLauncher = makeAddContactActivityResultLauncher(adapter)

        binding.button.setOnClickListener {
            addContactActivityResultLauncher.launch(Intent(this, ContactAddActivity::class.java))
        }
    }

    private fun goToDetailActivity(id: Long) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra("id", id)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    private fun makeAddContactActivityResultLauncher(adapter: ContactRecyclerAdapter): ActivityResultLauncher<Intent> {
        return registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i("data", "SUCCESS")
                val rowId = result?.data?.getLongExtra("data", -1L)
                rowId?.let {
                    if (it < 0L)
                        return@let
                    val contact = dbHelper.getItemById(rowId)
                    contact?.let { item -> adapter.addItem(item) }
                }
            } else {
                Log.i("data", "FAIL")
            }
        }
    }
}