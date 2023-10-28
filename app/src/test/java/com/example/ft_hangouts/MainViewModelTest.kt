package com.example.ft_hangouts

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.ContactDto
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.main.MainViewModel
import com.example.ft_hangouts.ui.main.MainViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner



@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
internal class MainViewModelTest {
    @get:Rule
    @ExperimentalCoroutinesApi
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var context: Context
    private lateinit var contactDAO: ContactDAO
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var testScope: TestScope
    private lateinit var viewModelFactory: MainViewModelFactory

    @Before
    @ExperimentalCoroutinesApi
    fun setupViewModel() = runTest {
        context = InstrumentationRegistry.getInstrumentation().context
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
        contactDAO = contactDatabase.contactDao()
        sharedPreferenceUtils = SharedPreferenceUtils(context)
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        viewModelFactory = MainViewModelFactory(sharedPreferenceUtils, baseViewModel, contactDatabase)
        mainViewModel = viewModelFactory.create(MainViewModel::class.java)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `뷰모델 초기화 테스트`() = runTest {
        // given
        val first = Contact(1, "a", "00000000", "abc", "abc", "abc")
        val second = Contact(2, "b", "11111111", "bcd", "bcd", "bcd")
        val defaultColor = -657931

        CoroutineScope(Dispatchers.IO).launch {
            contactDAO.add(ContactDto(1, "a", "00000000", "abc", "abc", "abc"))
            contactDAO.add(ContactDto(2, "b", "11111111", "bcd", "bcd", "bcd"))
        }.join()


        mainViewModel.initRecyclerList()

        assertEquals(2, mainViewModel.contactList.value.size)
        assertEquals(first, mainViewModel.contactList.value[0])
        assertEquals(second, mainViewModel.contactList.value[1])
        assertEquals(defaultColor, mainViewModel.appBarColor.value)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `연락처 목록 테스트`() = runTest {
        val first = ContactDto(1, "a", "b", "c", "d", "e")
        val second = ContactDto(2, "b", "c", "d", "e", "f")

        CoroutineScope(Dispatchers.IO).launch {
            contactDAO.add(first)
            contactDAO.add(second)
        }.join()

        mainViewModel.initRecyclerList()

        assertEquals(2, mainViewModel.contactList.value.size)
        assertEquals(
            Contact(1, "a", "b", "c", "d", "e"),
            mainViewModel.contactList.value[0]
        )
        assertEquals(
            Contact(2, "b", "c", "d", "e", "f"),
            mainViewModel.contactList.value[1]
        )
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `앱바 색상 변경 테스트`() = runTest {
        sharedPreferenceUtils.setAppbarColor(1111111)

        mainViewModel.updateAppbarColor()

        assertEquals(1111111, mainViewModel.appBarColor.value)
    }

    @After
    fun closeDb() {
        contactDatabase.close()
    }
}
