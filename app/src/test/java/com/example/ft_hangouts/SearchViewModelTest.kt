package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.search.ContactSearchViewModel
import com.example.ft_hangouts.ui.search.SearchViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.*
import org.junit.After
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
    private lateinit var contactDAO: ContactDAO
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var testScope: TestScope
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var viewModelFactory: SearchViewModelFactory

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        contactDatabase = createContactDatabase(context)
        contactDAO = contactDatabase.contactDao()
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        viewModelFactory = SearchViewModelFactory(contactDatabase, baseViewModel)
        searchViewModel = viewModelFactory.create(ContactSearchViewModel::class.java)
    }

    @Test
    fun `given match two contact when search partial match then check result`() = runTest {
        //given
        CoroutineScope(Dispatchers.IO).launch {
            contactDAO.add(Contact(0, "seongjki", "000000", "", "", ""), Contact(0, "seo", "111111", "", "", ""))
        }.join()

        //when
        searchViewModel.search("se")

        //then
        Assert.assertEquals(2, searchViewModel.searchedList.value.size)
        Assert.assertEquals(
            Contact(1, "seongjki", "000000", "", "", ""),
            searchViewModel.searchedList.value[0]
        )
        Assert.assertEquals(
            Contact(2, "seo", "111111", "", "", ""),
            searchViewModel.searchedList.value[1]
        )
    }

    @After
    fun after() {
        contactDatabase.close()
    }
}