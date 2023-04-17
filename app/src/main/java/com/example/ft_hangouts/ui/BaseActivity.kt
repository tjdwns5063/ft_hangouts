package com.example.ft_hangouts.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity: AppCompatActivity() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA)
    private lateinit var dateTime: String
    protected val baseViewModel by lazy { BaseViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseViewModel.errorHandler.observe(this) {
            it ?: return@observe

            if (it.isSuccess()) {
                finish()
            } else {
                it.handle(this)
            }
        }
    }
    override fun onPause() {
        super.onPause()

        dateTime = dateFormat.format(System.currentTimeMillis())
    }

    override fun onResume() {
        super.onResume()

        if (this::dateTime.isInitialized)
            Toast.makeText(this, dateTime, Toast.LENGTH_SHORT).show()
    }
}