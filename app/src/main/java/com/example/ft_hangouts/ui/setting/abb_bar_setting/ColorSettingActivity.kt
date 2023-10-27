package com.example.ft_hangouts.ui.setting.abb_bar_setting

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.ActivityAppBarSettingBinding
import com.example.ft_hangouts.ui.base.BaseActivity

class ColorSettingActivity : BaseActivity() {
    private val binding by lazy { ActivityAppBarSettingBinding.inflate(layoutInflater) }
    private val viewModel: ColorSettingViewModel by viewModels { ColorSettingViewModelFactory(sharedPreferenceUtils) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.appbarSettingColorPicker.setOnColorListener { color ->
            viewModel.changeColor(color)
            binding.appbarSettingSelectedColorImage.imageTintList = ColorStateList.valueOf(color)
        }

        binding.appbarSettingOkButton.setOnClickListener {
            viewModel.save()
            finish()
        }

        binding.appbarSettingDefaultButton.setOnClickListener {
            val defaultColor = getColor(R.color.main_background)

            viewModel.changeColor(defaultColor)
            viewModel.save()
            finish()
        }

        binding.appbarSettingCancelButton.setOnClickListener {
            finish()
        }
    }
}