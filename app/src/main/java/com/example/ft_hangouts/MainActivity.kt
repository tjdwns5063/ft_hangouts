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
import com.example.ft_hangouts.database.*
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dbHelper = createDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val adapter = ContactRecyclerAdapter {
            val position = binding.contactRecyclerView.getChildLayoutPosition(it)

            goToDetailActivity(position.toLong() + 1)
        }
        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        getAllItems()?.let {
            adapter.addItem(it)
        }

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
                val rowId = result?.data?.getLongExtra("data", -1L)
                rowId?.let {
                    if (it < 0L)
                        return@let
                    getItemById(rowId)?.let { item -> adapter.addItem(listOf(item)) }
                }
            } else {
                Toast.makeText(this, "연락처 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}