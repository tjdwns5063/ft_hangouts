package com.example.ft_hangouts.ui.abb_bar_setting

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.ColorInt
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivityAppBarSettingBinding
import com.example.ft_hangouts.ui.main.MainActivity

class AppBarSettingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAppBarSettingBinding.inflate(layoutInflater) }
    @ColorInt private var selectedColor: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.appbarSettingColorPicker.setOnColorListener { color ->
            selectedColor = color
            binding.appbarSettingSelectedColorImage.imageTintList = ColorStateList.valueOf(color)
        }

        binding.appbarSettingOkButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            intent.putExtra("color", selectedColor)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.appbarSettingDefaultButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val defaultColor = getColor(R.color.main_background)

            intent.putExtra("color", defaultColor)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.appbarSettingCancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}