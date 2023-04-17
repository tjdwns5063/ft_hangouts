package com.example.ft_hangouts.ui.main

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
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
    private val handler by lazy {if (Build.VERSION.SDK_INT >= 28) Handler.createAsync(mainLooper) else Handler(mainLooper)}
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAppBarColor()
        setRecyclerView()
        binding.button.setOnClickListener { goToAddActivity() }
        binding.settingButton.setOnClickListener { setPopUpMenu(it) }
    }

    private fun goToAddActivity() {
        startActivity(Intent(this, ContactAddActivity::class.java))
    }

    private fun setPopUpMenu(view: View) {
        val popupMenu = PopupMenu(this, view)

        popupMenu.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.main_header_color_change_menu -> {
                    goToAppBarChangeActivity()
                    true
                }
                else -> false
            }
        }

        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun setRecyclerView() {
        val adapter = ContactRecyclerAdapter { contactRecyclerItemOnClick(it) }

        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        updateRecyclerView(adapter)
    }

    private fun contactRecyclerItemOnClick(view: View) {
        val adapter = binding.contactRecyclerView.adapter as ContactRecyclerAdapter
        val position = binding.contactRecyclerView.getChildLayoutPosition(view)

        goToDetailActivity(adapter.getIdByPosition(position))
    }

    private fun goToAppBarChangeActivity() {
        val intent = Intent(this, AppBarSettingActivity::class.java)

        startActivity(intent)
    }

    private fun setAppBarColor() {
        binding.mainLayout.backgroundTintList = ColorStateList.valueOf(viewModel.getAppbarColor())
    }

    private fun updateRecyclerView(adapter: ContactRecyclerAdapter) {
        BackgroundHelper.execute {
            try {
                val list = viewModel.getAllContact()
                handler.post { adapter.addItem(list) }
            } catch (err: Exception) {
                Toast.makeText(this, "연락처를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToDetailActivity(id: Long) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra("id", id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val adapter = binding.contactRecyclerView.adapter as ContactRecyclerAdapter

        setAppBarColor()
        updateRecyclerView(adapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeDatabase()
    }
}