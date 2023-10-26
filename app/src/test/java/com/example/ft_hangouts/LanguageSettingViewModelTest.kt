package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.ui.setting.language_setting.LanguageSettingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
internal class LanguageSettingViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var languageSettingViewModel: LanguageSettingViewModel
    private lateinit var context: Context
    private lateinit var testScope: TestScope
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils

    @Before
    fun setUpViewModel() {
        context = InstrumentationRegistry.getInstrumentation().context
        testScope = TestScope(StandardTestDispatcher())
        sharedPreferenceUtils = SharedPreferenceUtils(context)
        languageSettingViewModel = LanguageSettingViewModel(testScope, sharedPreferenceUtils)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `언어 변경 테스트`() = runTest {
        // given
        sharedPreferenceUtils.setLanguage(null)

        // when
        languageSettingViewModel.updateLanguage("en").join()


        Assert.assertEquals("en", languageSettingViewModel.selectedLanguage.value)
    }
}