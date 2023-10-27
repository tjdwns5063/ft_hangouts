package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.ui.setting.abb_bar_setting.ColorSettingViewModel
import com.example.ft_hangouts.ui.setting.abb_bar_setting.ColorSettingViewModelFactory
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorSettingViewModelTest {
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var viewModel: ColorSettingViewModel
    private lateinit var viewModelFactory: ColorSettingViewModelFactory
    private lateinit var testScope: TestScope
    private lateinit var context: Context

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        testScope = TestScope(StandardTestDispatcher())
        sharedPreferenceUtils = SharedPreferenceUtils(context)
        viewModelFactory = ColorSettingViewModelFactory(sharedPreferenceUtils)
        viewModel = viewModelFactory.create(ColorSettingViewModel::class.java)
    }

    @Test
    fun `색상 변경 테스트`() {
        //given
        val color = 123456

        //when
        viewModel.changeColor(color)

        //then
        Assert.assertEquals(color, viewModel.selectedColor)
    }

    @Test
    fun `색상 저장 테스트`() {
        //given
        val color = 123456

        //when
        viewModel.changeColor(color)
        viewModel.save()

        //then
        Assert.assertEquals(sharedPreferenceUtils.getAppbarColor(), viewModel.selectedColor)
    }
}