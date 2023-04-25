package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.search.ContactSearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class SearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var context: Context
    private lateinit var searchViewModel: ContactSearchViewModel
    private lateinit var contactDatabaseDAO: ContactDatabaseDAO
    private lateinit var contactHelper: ContactHelper
    private lateinit var testScope: TestScope
    private lateinit var baseViewModel: BaseViewModel

    @Before
    fun setUpViewModel() {
        context = InstrumentationRegistry.getInstrumentation().context
        contactHelper = ContactHelper(context)
        contactDatabaseDAO = ContactDatabaseDAO(contactHelper)
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        searchViewModel = ContactSearchViewModel(contactDatabaseDAO, testScope, baseViewModel)
    }

    @Test
    fun `given match two contact when search partial match then check result`() = runTest {
        //given
        contactDatabaseDAO.addItem(Contact(0, "seongjki", "000000", "", "", ""))
        contactDatabaseDAO.addItem(Contact(1, "seo", "111111", "", "", ""))

        //when
        searchViewModel.search("se").join()

        //then
        Assert.assertEquals(2, searchViewModel.searchedList.value.size)
        Assert.assertEquals(
            ContactDomainModel(1, "seongjki", "000000", "", "", ""),
            searchViewModel.searchedList.value[0]
        )
        Assert.assertEquals(
            ContactDomainModel(2, "seo", "111111", "", "", ""),
            searchViewModel.searchedList.value[1]
        )
    }
}