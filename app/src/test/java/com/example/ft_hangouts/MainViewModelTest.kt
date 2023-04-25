package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.main.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner



@RunWith(RobolectricTestRunner::class)
internal class MainViewModelTest {
    @get:Rule
    @ExperimentalCoroutinesApi
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var context: Context
    private lateinit var dao: ContactDatabaseDAO
    private lateinit var dbHelper: ContactHelper
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var testScope: TestScope

    @Before
    @ExperimentalCoroutinesApi
    fun setupViewModel() = runTest {
        context = InstrumentationRegistry.getInstrumentation().context
        dbHelper = ContactHelper(context)
        dao = ContactDatabaseDAO(ContactHelper(context))
        sharedPreferenceUtils = SharedPreferenceUtils(context)
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        mainViewModel = MainViewModel(sharedPreferenceUtils, dao, testScope, baseViewModel)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `given database have two item when viewModel create then check initial condition`() = runTest {
        // given
        val first = ContactDomainModel(1, "a", "00000000", "abc", "abc", "abc")
        val second = ContactDomainModel(2, "b", "11111111", "bcd", "bcd", "bcd")
        val defaultColor = 16119285

        dao.addItem(Contact(1, "a", "00000000", "abc", "abc", "abc"))
        dao.addItem(Contact(2, "b", "11111111", "bcd", "bcd", "bcd"))

        val initMain = MainViewModel(sharedPreferenceUtils, dao, TestScope(mainDispatcherRule.testDispatcher), baseViewModel)

        initMain.initRecyclerList().join()

        assertEquals(2, initMain.contactList.value.size)
        assertEquals(first, initMain.contactList.value[0])
        assertEquals(second, initMain.contactList.value[1])
        assertEquals(defaultColor, initMain.appBarColor.value)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `given database have two item when viewModel check items then get all item list`() = runTest {
        val first = Contact(1, "a", "b", "c", "d", "e")
        val second = Contact(2, "b", "c", "d", "e", "f")

        dao.addItem(first)
        dao.addItem(second)

        mainViewModel.initRecyclerList().join()

        assertEquals(2, mainViewModel.contactList.value.size)
        assertEquals(
            ContactDomainModel(1, "a", "b", "c", "d", "e"),
            mainViewModel.contactList.value[0]
        )
        assertEquals(
            ContactDomainModel(2, "b", "c", "d", "e", "f"),
            mainViewModel.contactList.value[1]
        )
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `given set appbar color when update appbar color expect color is set`() = runTest {
        sharedPreferenceUtils.setAppbarColor(1111111)

        mainViewModel.updateAppbarColor().join()

        assertEquals(1111111, mainViewModel.appBarColor.value)
    }

    @After
    fun closeDb() {
        mainViewModel.closeDatabase()
    }
}
