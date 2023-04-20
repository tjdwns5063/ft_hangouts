package com.example.ft_hangouts

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.ui.BaseViewModel
import com.example.ft_hangouts.ui.add.ContactAddViewModel
import com.example.ft_hangouts.ui.main.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
internal class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var context: Context
    private lateinit var dao: ContactDatabaseDAO
    private lateinit var dbHelper: ContactHelper
    private lateinit var addViewModel: ContactAddViewModel
    private lateinit var baseViewModel: BaseViewModel
    @Before
    @ExperimentalCoroutinesApi
    fun setupViewModel() = runTest {
        context = InstrumentationRegistry.getInstrumentation().context
        dbHelper = ContactHelper(context)
        dao = ContactDatabaseDAO(ContactHelper(context))

        baseViewModel = BaseViewModel()
        mainViewModel = MainViewModel(dao, TestScope(StandardTestDispatcher()), baseViewModel)
        addViewModel = ContactAddViewModel(dao, TestScope(StandardTestDispatcher()), baseViewModel, ImageDatabaseDAO(context))

    }
    @Test
    @ExperimentalCoroutinesApi
    fun getContactList() = runTest {
        // given
        dao.addItem(Contact(1,"John Doe", "01012345678", "john@example.com", "", ""))
        dao.addItem(Contact(2,"Jane Doe", "01056478923", "jane@example.com", "", ""))


        // when
        mainViewModel.getContactList()

        // then
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            mainViewModel.contactList.collect {
                assertEquals(it.size, 2)
                assertEquals(it[0].name, "John Doe")
                assertEquals(it[0].phoneNumber, "01012345678")
                assertEquals(it[0].email, "john@example.com")
                assertEquals(it[1].name, "Jane Doe")
                assertEquals(it[1].phoneNumber, "01056478923")
                assertEquals(it[1].email, "jane@example.com")
            }
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun getAppbarColor() {

    }

    @After
    fun closeDb() {
        dbHelper.close()
    }
}
/*


     */
