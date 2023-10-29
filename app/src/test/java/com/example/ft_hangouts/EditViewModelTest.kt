package com.example.ft_hangouts

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.ImageDAO
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.edit.ContactEditViewModel
import com.example.ft_hangouts.ui.edit.EditViewModelFactory
import com.example.ft_hangouts.util.compressBitmapToByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
class EditViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var context: Context
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var contactDAO: ContactDAO
    @Mock
    private lateinit var imageDAO: ImageDAO
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var testScope: TestScope
    private lateinit var viewModel: ContactEditViewModel
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().context
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
        contactDAO = contactDatabase.contactDao()
        testScope = TestScope(StandardTestDispatcher())
        baseViewModel = BaseViewModel(testScope)
        App.INSTANCE = App()
        viewModelFactory = EditViewModelFactory(1, baseViewModel,contactDatabase, imageDAO)
        viewModel = viewModelFactory.create(ContactEditViewModel::class.java)
    }

    @Test
    fun `연락처 업데이트 테스트`() = runTest {
        //given
        val newContact = Contact(1, "seongjki", "01012345678", "abc@def.ghi", "", "")
        withContext(Dispatchers.IO) {
            contactDAO.add(newContact)
        }
        viewModel.initViewModel()


        //when
        viewModel.updateContact("mkang", "01012345678", "abc@def.ghi", "", "")
        val result = withContext(Dispatchers.IO) {
            contactDAO.getItemById(1)
        }

        //then
        Assert.assertEquals(
            Contact(1, "mkang", "01012345678", "abc@def.ghi", "", "")
            , result
        )

    }

    @Test
    fun `프로필 업데이트 테스트`() = runTest {
        //given
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val newContact = Contact(1, "seongjki", "01012345678", "abc@def.ghi", "", "", compressBitmapToByteArray(bitmap, 1f))

        withContext(Dispatchers.IO) {
            contactDAO.add(newContact)
        }
        viewModel.initViewModel()

        //when
        Mockito.`when`(imageDAO.loadBitmap("123")).thenAnswer { bitmap }
        Mockito.`when`(imageDAO.density).thenReturn(1f)
        viewModel.updateProfileImage("123")

        val byteArray = viewModel.contact.value.profile
        val result = compressBitmapToByteArray(bitmap, imageDAO.density).contentEquals(byteArray)

        //then
        Assert.assertEquals(
            true,
            result
        )
    }

    @After
    fun after() {
        contactDatabase.close()
    }

}