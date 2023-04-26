package com.example.ft_hangouts.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.system.CallSystemHelper
import com.example.ft_hangouts.system.registerRequestCallPermissionResult
import com.example.ft_hangouts.system.requestCallPermission
import com.example.ft_hangouts.system.requestCallToCallSystemHelper
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.setting.abb_bar_setting.AppBarSettingActivity
import com.example.ft_hangouts.ui.add.ContactAddActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract
import com.example.ft_hangouts.ui.detail.ContactDetailActivity
import com.example.ft_hangouts.ui.search.ContactSearchActivity
import com.example.ft_hangouts.ui.setting.language_setting.LanguageSettingActivity
import com.example.ft_hangouts.ui.sms.ContactSmsActivity
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy { MainViewModel(sharedPreferenceUtils, ContactDatabaseDAO(ContactHelper.createDatabase(this)), lifecycleScope, super.baseViewModel) }
    private val callPermissionLauncher = registerRequestCallPermissionResult()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        setContentView(binding.root)
        setButton()
        setAppBarColor()
        setRecyclerView()
    }

    private fun setButton() {
        binding.toolbarAddButton.setOnClickListener { goToAddActivity() }
        binding.toolbarSettingButton.setOnClickListener { setPopUpMenu(it) }
        binding.toolbarSearchButton.setOnClickListener {
            val intent = Intent(this, ContactSearchActivity::class.java)
            startActivity(intent)
        }
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
        val adapter = ContactRecyclerAdapter { contactRecyclerItemOnClick(it) }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val notifyItemChanged = { pos: Int ->
            adapter.notifyItemChanged(pos)
        }
        val itemTouchHelperCallback = ContactTouchHelperCallback(notifyItemChanged) { position, direction ->
            when (direction) {
                ItemTouchHelper.RIGHT -> {
                    requestCallPermission(callPermissionLauncher)
                    requestCallToCallSystemHelper(adapter.currentList[position].phoneNumber)
                    Toast.makeText(this, "오른쪽 스와이프", Toast.LENGTH_SHORT).show()
                }
                ItemTouchHelper.LEFT -> {
                    val intent = Intent(this, ContactSmsActivity::class.java)
                    intent.putExtra(ContactActivityContract.CONTACT_ID, adapter.getIdByPosition(position))
                    startActivity(intent)
                    Toast.makeText(this, "왼쪽 스와이프", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val helper = ItemTouchHelper(itemTouchHelperCallback)
        helper.attachToRecyclerView(binding.contactRecyclerView)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.initRecyclerList()

                viewModel.contactList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }
    private fun contactRecyclerItemOnClick(view: View) {
        val adapter = binding.contactRecyclerView.adapter as ContactRecyclerAdapter
        val position = binding.contactRecyclerView.getChildLayoutPosition(view)
        val id = adapter.getIdByPosition(position)

        goToDetailActivity(id)
    }

    private fun goToAppBarChangeActivity() {
        val intent = Intent(this, AppBarSettingActivity::class.java)

        startActivity(intent)
    }

    private fun setAppBarColor() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateAppbarColor()
                viewModel.appBarColor.collect {
                    binding.appBar.backgroundTintList = ColorStateList.valueOf(it)
                }
            }
        }
    }

    private fun goToDetailActivity(id: Long) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra(ContactActivityContract.CONTACT_ID, id)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeDatabase()
    }
}