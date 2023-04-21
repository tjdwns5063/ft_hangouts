package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.SharedPreferenceUtils
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.main.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var context: Context
    private lateinit var dao: ContactDatabaseDAO
    private lateinit var dbHelper: ContactHelper
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var testScope: CoroutineScope
    @Before
    @ExperimentalCoroutinesApi
    fun setupViewModel() = runTest {
        context = InstrumentationRegistry.getInstrumentation().context
        dbHelper = ContactHelper(context)
        dao = ContactDatabaseDAO(ContactHelper(context))
        sharedPreferenceUtils = SharedPreferenceUtils(context)
        testScope = TestScope(StandardTestDispatcher())
        baseViewModel = BaseViewModel(testScope)
        mainViewModel = MainViewModel(sharedPreferenceUtils, dao, testScope, baseViewModel)
    }

    @Test
    @OptIn(InternalCoroutinesApi::class)
    @ExperimentalCoroutinesApi
    fun `given database have two item when viewModel create then check initial condition`() = runTest {
        // given
        val first = Contact(0, "a", "00000000", "abc", "abc", "abc")
        val second = Contact(1, "b", "11111111", "bcd", "bcd", "bcd")
        val defaultColor = 16119285

        dao.addItem(Contact(0, "a", "00000000", "abc", "abc", "abc"))
        dao.addItem(Contact(1, "b", "11111111", "bcd", "bcd", "bcd"))

        backgroundScope.launch {
            // when
            val initMain = backgroundScope.async {
                MainViewModel(sharedPreferenceUtils, dao, TestScope(StandardTestDispatcher()), baseViewModel)
            }.await()

            // then
            initMain.contactList.collect(FlowCollector {
                assertEquals(it.size, 2)
                assertEquals(it[0], first)
                assertEquals(it[1], second)
            })

            initMain.appBarColor.collect(FlowCollector {
                assertEquals(it, defaultColor)
            })
        }
    }
    @OptIn(InternalCoroutinesApi::class)
    @Test
    @ExperimentalCoroutinesApi
    fun `given database have two item when viewModel check items then get all item list`() = runTest {
        // given
        dao.addItem(Contact(1, "John Doe", "01012345678", "john@example.com", "", ""))
        dao.addItem(Contact(2, "Jane Doe", "01056478923", "jane@example.com", "", ""))


        // when
        backgroundScope.launch {
            mainViewModel.initRecyclerList()
        }

        // then
        backgroundScope.launch {
            mainViewModel.contactList.collect(FlowCollector {
                assertEquals(it.size, 2)
                assertEquals(it[0].name, "John Doe")
                assertEquals(it[0].phoneNumber, "01012345678")
                assertEquals(it[0].email, "john@example.com")
                assertEquals(it[1].name, "Jane Doe")
                assertEquals(it[1].phoneNumber, "01056478923")
                assertEquals(it[1].email, "jane@example.com")
            })
        }
    }

    @Test
    @OptIn(InternalCoroutinesApi::class)
    @ExperimentalCoroutinesApi
    fun `given set appbar color when update appbar color expect color is set`() = runTest {
        //given
        sharedPreferenceUtils.setAppbarColor(111111)

        // when
        backgroundScope.launch {
            mainViewModel.updateAppbarColor()
        }

        //then
        backgroundScope.launch {
            mainViewModel.appBarColor.collect(FlowCollector {
                assertEquals(it, 111111)
            })
        }
    }

    @After
    fun closeDb() {
        mainViewModel.closeDatabase()
    }
}
