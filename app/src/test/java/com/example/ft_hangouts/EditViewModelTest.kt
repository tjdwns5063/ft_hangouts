package com.example.ft_hangouts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.data.contact_database.Profile
import com.example.ft_hangouts.data.contact_database.contactToContactDomainModel
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.edit.ContactEditViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
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

@RunWith(RobolectricTestRunner::class)
class EditViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var context: Context
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var contactDAO: ContactDAO
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var testScope: TestScope
    @Mock
    private lateinit var imageDatabaseDAO: ImageDatabaseDAO
    private lateinit var viewModel: ContactEditViewModel

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
        contactDAO = contactDatabase.contactDao()
        testScope = TestScope(StandardTestDispatcher())
        baseViewModel = BaseViewModel(testScope)
        MockitoAnnotations.openMocks(this)
//        imageDatabaseDAO = ImageDatabaseDAO(context)
    }

    @Test
    fun `연락처 업데이트 테스트`() = runTest {
        //given
        val newContact = Contact(1, "seongjki", "01012345678", "abc@def.ghi", "", "")
        CoroutineScope(Dispatchers.IO).launch {
            contactDAO.add(newContact)
        }.join()

        viewModel = ContactEditViewModel(contactDAO, 1, testScope, baseViewModel, imageDatabaseDAO)
        viewModel.init().join()

        //when
        viewModel.updateContact("mkang", "01012345678", "abc@def.ghi", "", "").join()
        val result = CoroutineScope(Dispatchers.IO).async {
            contactToContactDomainModel(contactDAO.getItemById(1))
        }.await()

        //then
        Assert.assertEquals(
            ContactDomainModel(1, "mkang", "01012345678", "abc@def.ghi", "", "")
            , result
        )

    }

    @Test
    fun `프로필 업데이트 테스트`() = runTest {
        //given
        val newContact = Contact(1, "seongjki", "01012345678", "abc@def.ghi", "", "")
        CoroutineScope(Dispatchers.IO).launch {
            contactDAO.add(newContact)
        }.join()

        viewModel = ContactEditViewModel(contactDAO, 1, testScope, baseViewModel, imageDatabaseDAO)
        viewModel.init().join()
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        Mockito.`when`(
            imageDatabaseDAO.getImageFromUri("123"))
            .thenReturn(Profile(BitmapDrawable(null, bitmap))
        )
        //when
        viewModel.updateProfileImage("123").join()

        //then
        Assert.assertEquals(bitmap.byteCount, viewModel.updatedProfile.value.bitmapDrawable?.bitmap?.byteCount)
    }

    @After
    fun after() {
        contactDatabase.close()
    }

}