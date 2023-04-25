package com.example.ft_hangouts.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.databinding.ActivityContactSearchBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import kotlinx.coroutines.launch

class ContactSearchActivity : BaseActivity() {
    private lateinit var binding: ActivityContactSearchBinding
    private val viewModel by lazy {
        ContactSearchViewModel(ContactDatabaseDAO(ContactHelper.createDatabase(this)), lifecycleScope, baseViewModel)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = SearchRecyclerViewAdapter {

        }
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchedList.collect {
                    Log.i("search", "list: $it")
                    adapter.submitList(it)
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object: OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                text ?: return false
                viewModel.search(text)
                return true
            }
        })

    }

    private fun search(query: String) {

    }
}