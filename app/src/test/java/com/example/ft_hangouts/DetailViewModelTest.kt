package com.example.ft_hangouts

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.detail.ContactDetailViewModel
import com.example.ft_hangouts.ui.detail.DetailViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var context: Context
    private lateinit var testScope: TestScope
    private lateinit var detailViewModel: ContactDetailViewModel
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var contactDAO: ContactDAO
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
        contactDAO = contactDatabase.contactDao()
        viewModelFactory = DetailViewModelFactory(1, baseViewModel, contactDatabase)
        detailViewModel = viewModelFactory.create(ContactDetailViewModel::class.java)
    }

    @Test
    fun `연락처 정보 초기화 테스트`() = runTest {
        //given
        withContext(Dispatchers.IO) {
            contactDAO.add(Contact(1, "a", "0000000", "abc@def.com", "", ""))
        }

        //then
        detailViewModel.initContact()

        //expect
        Assert.assertEquals(
            Contact(1, "a", "0000000", "abc@def.com", "", ""),
            detailViewModel.contact.value
        )
    }

    @Test
    fun `연락처 삭제 테스트`() = runTest {
        //given
        withContext(Dispatchers.IO) {
            contactDAO.add(Contact(0, "a", "0000000", "abc@def.com", "", ""))
        }

        //then
        detailViewModel.initContact()
        detailViewModel.deleteContact()

        //expect
        Assert.assertThrows(
            IllegalStateException::class.java,
        ) {
            contactDAO.getItemById(1)
        }
    }

    @After
    fun after() {
        contactDatabase.close()
    }
}