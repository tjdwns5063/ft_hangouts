package com.example.ft_hangouts.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.error.CallSystemErrorHandler
import com.example.ft_hangouts.error.DatabaseCreateErrorHandler
import com.example.ft_hangouts.system.CallSystemHelper
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
    private lateinit var viewModel: MainViewModel
    private lateinit var callSystemHelper: CallSystemHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initCallSystemHelper()
        createViewModel()
        binding.viewModel = viewModel
        setContentView(binding.root)
        callSystemHelper.registerCallPermissionLauncher()
        callSystemHelper.requestCallPermission()
        setButton()
        setAppBarColor()
        setRecyclerView()
        observeAndUpdateContactList()
    }

    private fun initCallSystemHelper() {
        try {
            callSystemHelper = CallSystemHelper(this)
        } catch (err: Exception) {
            baseViewModel.submitHandler(CallSystemErrorHandler())
        }
    }

    private fun createViewModel() {
        try {
            viewModel = MainViewModel(
                sharedPreferenceUtils,
                ContactDatabaseDAO(ContactHelper.createDatabase(applicationContext)),
                lifecycleScope,
                super.baseViewModel
            )
        } catch (err: Exception) {
            baseViewModel.submitHandler(DatabaseCreateErrorHandler())
        }
    }

    private fun setButton() {
        binding.toolbarAddButton.setOnClickListener { goToActivity(ContactAddActivity::class.java) }
        binding.toolbarSettingButton.setOnClickListener { setPopUpMenu(it) }
        binding.toolbarSearchButton.setOnClickListener { goToActivity(ContactSearchActivity::class.java) }
    }

    private fun setPopUpMenu(view: View) {
        val popupMenu = PopupMenu(this, view)

        popupMenu.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.main_header_color_change_menu -> {
                    goToActivity(AppBarSettingActivity::class.java)
                    true
                }
                R.id.main_language_select_menu -> {
                    goToActivity(LanguageSettingActivity::class.java)
                    true
                }
                else -> false
            }
        }

        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun setRecyclerView() {
        val itemTouchHelperCallback = ContactTouchHelperCallback()

        val helper = ItemTouchHelper(itemTouchHelperCallback)
        helper.attachToRecyclerView(binding.contactRecyclerView)

        val adapter = ContactRecyclerAdapter { contactRecyclerItemOnClick(it) }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        itemTouchHelperCallback.setSwipeListener { position, direction ->
            adapter.redraw(position)
            when (direction) {
                ItemTouchHelper.RIGHT -> {
                    callSystemHelper.requestCallPermission()
                    callSystemHelper.call(adapter.currentList[position].phoneNumber)
                }
                ItemTouchHelper.LEFT -> {
                    goToActivity(
                        ContactSmsActivity::class.java, ContactActivityContract.CONTACT_ID, adapter.getItemId(position)
                    )
                }
            }
        }

        binding.contactRecyclerView.adapter = adapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.contactRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onResume() {
        super.onResume()

        viewModel.initRecyclerList()
        viewModel.updateAppbarColor()
    }

    private fun contactRecyclerItemOnClick(view: View) {
        val adapter = binding.contactRecyclerView.adapter as ContactRecyclerAdapter
        val position = binding.contactRecyclerView.getChildLayoutPosition(view)
        val id = adapter.getItemId(position)

        goToActivity(ContactDetailActivity::class.java, ContactActivityContract.CONTACT_ID, id)
    }

    private fun setAppBarColor() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appBarColor.collect {
                    binding.appBar.backgroundTintList = ColorStateList.valueOf(it)
                }
            }
        }
    }

    private fun observeAndUpdateContactList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.contactList.collect {
                    (binding.contactRecyclerView.adapter as ContactRecyclerAdapter).submitList(it)
                }
            }
        }
    }

}