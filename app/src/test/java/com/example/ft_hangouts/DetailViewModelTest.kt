package com.example.ft_hangouts

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.detail.ContactDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var contactHelper: ContactHelper
    private lateinit var contactDatabaseDAO: ContactDatabaseDAO

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        contactHelper = ContactHelper(context)
        contactDatabaseDAO = ContactDatabaseDAO(contactHelper)
        detailViewModel = ContactDetailViewModel(testScope, 1, baseViewModel, contactDatabaseDAO)
    }

    @Test
    fun `연락처 정보 초기화 테스트`() = runTest {
        //given
        contactDatabaseDAO.addItem(Contact(1, "a", "0000000", "abc@def.com", "", ""))

        //then
        val detailViewModel = ContactDetailViewModel(testScope, 1, baseViewModel, contactDatabaseDAO)
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
        contactDatabaseDAO.addItem(Contact(1, "a", "0000000", "abc@def.com", "", ""))

        //then
        val detailViewModel = ContactDetailViewModel(testScope, 1, baseViewModel, contactDatabaseDAO)
        detailViewModel.initContact().join()
        detailViewModel.deleteContact().join()

        //expect
        Assert.assertThrows(
            IllegalStateException::class.java,
        ) {
            contactDatabaseDAO.getItemById(1)
        }
    }

    @After
    fun after() {
        contactHelper.close()
    }
}