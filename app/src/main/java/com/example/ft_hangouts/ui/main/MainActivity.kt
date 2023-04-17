package com.example.ft_hangouts.ui.main

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.BackgroundHelper
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.ui.BaseActivity
import com.example.ft_hangouts.ui.abb_bar_setting.AppBarSettingActivity
import com.example.ft_hangouts.ui.add.ContactAddActivity
import com.example.ft_hangouts.ui.detail.ContactDetailActivity

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val contactDAO = ContactDatabaseDAO()
    private val appbarSettingActivityLauncher = registerChangeAppBarResult()
    private val handler by lazy {if (Build.VERSION.SDK_INT >= 28) Handler.createAsync(mainLooper) else Handler(mainLooper)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val adapter = ContactRecyclerAdapter {
            val adapter = binding.contactRecyclerView.adapter as ContactRecyclerAdapter
            val position = binding.contactRecyclerView.getChildLayoutPosition(it)
            goToDetailActivity(adapter.getIdByPosition(position))
        }
        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        getAllContactAndUpdateRecyclerView(adapter)

        binding.button.setOnClickListener {
            startActivity(Intent(this, ContactAddActivity::class.java))
        }

        binding.settingButton.setOnClickListener {
            val popupMenu = PopupMenu(this, it)

            popupMenu.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.main_header_color_change_menu -> {
                        val intent = Intent(this, AppBarSettingActivity::class.java)
                        appbarSettingActivityLauncher.launch(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
            popupMenu.show()
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = binding.contactRecyclerView.adapter as ContactRecyclerAdapter

        getAllContactAndUpdateRecyclerView(adapter)
    }

    private fun getAllContactAndUpdateRecyclerView(adapter: ContactRecyclerAdapter) {
        BackgroundHelper.execute {
            try {
                val list = contactDAO.getAllItems()
                handler.post { adapter.addItem(list) }
            } catch (err: Exception) {
                Toast.makeText(this, "연락처를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerChangeAppBarResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = it.data ?: return@registerForActivityResult
                val color = intent.getIntExtra("color", 0)
                println(color)
                binding.mainLayout.backgroundTintList = ColorStateList.valueOf(color)
            }
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
        contactDAO.closeDatabase()
    }
}