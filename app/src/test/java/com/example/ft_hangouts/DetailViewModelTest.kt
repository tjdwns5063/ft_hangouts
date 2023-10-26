package com.example.ft_hangouts

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.detail.ContactDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
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

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
        contactDAO = contactDatabase.contactDao()
        detailViewModel = ContactDetailViewModel(testScope, 1, baseViewModel, contactDAO)
        runTest {
            CoroutineScope(Dispatchers.IO).launch {
                contactDAO.add(Contact(0, "a", "0000000", "abc@def.com", "", ""))
            }.join()
        }
    }

    @Test
    fun `연락처 정보 초기화 테스트`() = runTest {
        //given
        val detailViewModel = ContactDetailViewModel(testScope, 1, baseViewModel, contactDAO)

        //then
        detailViewModel.initContact().join()

        //expect
        Assert.assertEquals(
            ContactDomainModel(1, "a", "0000000", "abc@def.com", "", ""),
            detailViewModel.contact.value
        )
    }

    @Test
    fun `연락처 삭제 테스트`() = runTest {
        //given
        CoroutineScope(Dispatchers.IO).launch {
            contactDAO.add(Contact(0, "a", "0000000", "abc@def.com", "", ""))
        }

        //then
        val detailViewModel = ContactDetailViewModel(testScope, 1, baseViewModel, contactDAO)
        detailViewModel.initContact().join()
        detailViewModel.deleteContact().join()

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