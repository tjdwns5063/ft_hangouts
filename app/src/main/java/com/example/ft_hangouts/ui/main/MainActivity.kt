package com.example.ft_hangouts.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.App
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.ui.BaseActivity
import com.example.ft_hangouts.ui.setting.abb_bar_setting.AppBarSettingActivity
import com.example.ft_hangouts.ui.add.ContactAddActivity
import com.example.ft_hangouts.ui.detail.ContactDetailActivity
import com.example.ft_hangouts.ui.setting.language_setting.LanguageSettingActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy { MainViewModel(sharedPreferenceUtils, ContactDatabaseDAO(ContactHelper(applicationContext)), lifecycleScope, super.baseViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
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
                R.id.main_language_select_menu -> {
                    goToLanguageSettingActivity()
                    true
                }
                else -> false
            }
        }

        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun goToLanguageSettingActivity() {
        val intent = Intent(this, LanguageSettingActivity::class.java)

        startActivity(intent)
    }

    private fun setRecyclerView() {
        val adapter = ContactRecyclerAdapter { contactRecyclerItemOnClick(it) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contactList.collect {
                    adapter.submitList(it)
                }
            }
        }
        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appBarColor.collect {
                    binding.mainLayout.backgroundTintList = ColorStateList.valueOf(it)
                }
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

        viewModel.updateAppbarColor()
        viewModel.initRecyclerList()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeDatabase()
    }
}