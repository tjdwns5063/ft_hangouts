package com.example.ft_hangouts.ui.search

import android.app.SearchManager
import android.app.SearchableInfo
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.databinding.ActivityContactSearchBinding
import com.example.ft_hangouts.ui.base.BaseActivity
import com.example.ft_hangouts.ui.base.ContactActivityContract
import com.example.ft_hangouts.ui.detail.ContactDetailActivity
import kotlinx.coroutines.launch

class ContactSearchActivity : BaseActivity() {
    private lateinit var binding: ActivityContactSearchBinding
    private val viewModel by lazy {
        ContactSearchViewModel(
            ContactDatabaseDAO(ContactHelper.createDatabase(this)),
            lifecycleScope,
            baseViewModel
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButton()
        setRecyclerView()
        setSearchView()
        updateSearchResult()
    }

    private fun setButton() {
        binding.searchBackButton.setOnClickListener { finish() }
    }

    private fun setRecyclerView() {
        val adapter = SearchRecyclerViewAdapter {
            onClickRecyclerItem(it)
        }
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun onClickRecyclerItem(view: View) {
        val position = binding.searchRecyclerView.getChildLayoutPosition(view)
        val id = (binding.searchRecyclerView.adapter as SearchRecyclerViewAdapter).getIdByPosition(position)
        val intent = Intent(this, ContactDetailActivity::class.java)

        intent.putExtra(ContactActivityContract.CONTACT_ID, id)
        startActivity(intent)
    }

    private fun setSearchView() {
        binding.searchView.onActionViewExpanded()
        binding.searchView.queryHint = getString(R.string.search_hint)
        binding.searchView.setOnQueryTextListener(ContactQueryTextListener())
    }

    private fun updateSearchResult() {
        val adapter = (binding.searchRecyclerView.adapter) as SearchRecyclerViewAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.update()
                viewModel.searchedList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    inner class ContactQueryTextListener: OnQueryTextListener {
        override fun onQueryTextSubmit(text: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(text: String?): Boolean {
            text ?: return false
            if (text == "") return false
            viewModel.search(text)
            return true
        }
    }
}