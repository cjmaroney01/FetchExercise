package com.colinmaroney.fetchexercise.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.colinmaroney.fetchexercise.R
import com.colinmaroney.fetchexercise.viewmodel.HiringViewModel
import io.mockk.CapturingSlot
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MainFragmentTest {
    private val fragment = spyk(MainFragment(), recordPrivateCalls = true)
    private val mockViewModel: HiringViewModel = mockk()
    private val mockRecycler: RecyclerView = mockk()
    private val mockLiveData1: LiveData<Map<Int, String>?> = mockk()
    private val mockLiveData2: LiveData<Boolean> = mockk()
    private val slot1 = CapturingSlot<Observer<Map<Int, String>?>>()
    private val slot2 = CapturingSlot<Observer<Boolean>>()
    private val mockContext: Context = mockk()

    @BeforeEach
    fun beforeEach() {
        clearMocks(mockViewModel, mockRecycler,
            mockLiveData1, mockLiveData2, mockContext)

        every { fragment.requireContext() } returns mockContext
        every { fragment.viewLifecycleOwner } returns mockk()

        every { fragment.viewModel } returns mockViewModel
        every { mockRecycler.adapter = any() } just runs

        every { mockViewModel.hiringData } returns mockLiveData1
        every { mockViewModel.hiringError } returns mockLiveData2

        every { mockLiveData1.observe(any(), capture(slot1)) } just runs
        every { mockLiveData2.observe(any(), capture(slot2)) } just runs

        every { fragment.getString(any()) } answers {
            args[0].toString()
        }
    }

    @Nested
    @DisplayName("addObservers")
    inner class AddObserversTest {
        private val mocKBuilder: AlertDialog.Builder = mockk()
        private val buttonSlot: CapturingSlot<DialogInterface.OnClickListener> = CapturingSlot()
        private val map = mutableMapOf(1 to "item 1, item 2, item 3", 2 to "item 4, item 5")

        @BeforeEach
        fun beforeEach() {
            mockkConstructor(AlertDialog.Builder::class)
            every { anyConstructed<AlertDialog.Builder>().setMessage(any<String>()) } returns mocKBuilder
            every { mocKBuilder.setPositiveButton(any<String>(), capture(buttonSlot)) } returns mocKBuilder
            every { mocKBuilder.show() } returns mockk()
        }

        @Test
        fun `should observe hiringData`() {
            fragment.addObservers(mockRecycler)
            verify(exactly = 1) { mockLiveData1.observe(any(), any()) }
        }

        @Test
        fun `should observe hiringError`() {
            fragment.addObservers(mockRecycler)
            verify(exactly = 1) { mockLiveData2.observe(any(), any()) }
        }

        @Test
        fun `should set the recyclers adapter with the hiring data`() {
            fragment.addObservers(mockRecycler)
            slot1.captured.onChanged(map)
            verify(exactly = 1) { mockRecycler.adapter = any() }
        }

        @Test
        fun `should not set the recyclers adapter with the hiring data if it is null`() {
            fragment.addObservers(mockRecycler)
            slot1.captured.onChanged(null)
            verify(exactly = 0) { mockRecycler.adapter = any() }
        }

        @Test
        fun `should pop an alert dialog if error`() {
            fragment.addObservers(mockRecycler)
            slot2.captured.onChanged(true)

            verify(exactly = 1) { anyConstructed<AlertDialog.Builder>().setMessage(any<String>()) }
            verify(exactly = 1) { anyConstructed<AlertDialog.Builder>().setMessage(R.string.an_error_occurred.toString()) }

            verify(exactly = 1) { mocKBuilder.setPositiveButton(any<String>(), any()) }
            verify(exactly = 1) { mocKBuilder.setPositiveButton(R.string.ok.toString(), any()) }

            verify(exactly = 1){ mocKBuilder.show() }
        }

        @Test
        fun `should dismiss the dialog on positive click`() {
            val mockInterface: DialogInterface = mockk() {
                every { dismiss() } just runs
            }
            fragment.addObservers(mockRecycler)
            slot2.captured.onChanged(true)

            buttonSlot.captured.onClick(mockInterface, 1)
            verify(exactly = 1) { mockInterface.dismiss() }
        }

        @Test
        fun `should not pop a dialog if error is false`() {
            fragment.addObservers(mockRecycler)
            slot2.captured.onChanged(false)
            verify(exactly = 0) { mocKBuilder.show() }
        }
    }
}