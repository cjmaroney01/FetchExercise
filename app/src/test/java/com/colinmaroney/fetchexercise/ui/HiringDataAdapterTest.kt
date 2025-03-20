package com.colinmaroney.fetchexercise.ui

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HiringDataAdapterTest {
    private val map = mutableMapOf(1 to "foo 1, bar 2, baz 3",
                2 to "grr 99 brr 101 ack 234")

    private val adapter = spyk(HiringDataAdapter(map), recordPrivateCalls = true)

    private val mockView: View = mockk()
    private val mockTextView1: TextView = mockk()
    private val mockTextView2: TextView = mockk()
    private val mockTextView3: TextView = mockk()
    private val mockTextView4: TextView = mockk()

    private lateinit var viewHolder: HiringDataAdapter.ViewHolder

    @BeforeEach
    fun beforeEach() {
        clearMocks(mockView, mockTextView1, mockTextView2, mockTextView3, mockTextView4)

        every { mockView.findViewById<TextView>(any()) } returnsMany  listOf(mockTextView1, mockTextView2, mockTextView3, mockTextView4)
        every { mockTextView1.text = any() } just runs
        every { mockTextView2.text = any() } just runs
        every { mockTextView3.paintFlags } returns Paint.LINEAR_TEXT_FLAG
        every { mockTextView3.paintFlags = any() } just runs
        every { mockTextView4.paintFlags } returns Paint.LINEAR_TEXT_FLAG
        every { mockTextView4.paintFlags = any() } just runs

        viewHolder = spyk(HiringDataAdapter.ViewHolder(mockView), recordPrivateCalls = true)
    }

    @Nested
    @DisplayName("ViewHolder")
    inner class ViewHolderTests {
        @Test
        fun `should assign the views`() {
            verify(exactly = 4) { mockView.findViewById<TextView>(any()) }
            assertEquals(mockTextView1, viewHolder.namesText)
            assertEquals(mockTextView3, viewHolder.namesHeaderText)
            assertEquals(mockTextView2, viewHolder.listIdText)
            assertEquals(mockTextView4, viewHolder.listIdHeaderText)
        }
    }

    @Nested
    @DisplayName("getItemCount")
    inner class GetItemCountTests {
        @Test
        fun `should return a count of keys in the dataset map`() {
            assertEquals(2, adapter.itemCount)
        }
    }

    @Nested
    @DisplayName("onBindViewHolder")
    inner class OnBindViewHolderTests {
        @Test
        fun `should assign the listId and names text`() {
            adapter.onBindViewHolder(viewHolder, 0)

            verify(exactly = 1) { mockTextView1.text = any() }
            verify(exactly = 1) { mockTextView1.text = map[1] }

            verify(exactly = 1) { mockTextView2.text = any() }
            verify(exactly = 1) { mockTextView2.text = "1" }
        }

        @Test
        fun `should underline the headers`() {
            adapter.onBindViewHolder(viewHolder, 1)

            verify(exactly = 1) { mockTextView3.paintFlags }
            verify(exactly = 1) { mockTextView3.paintFlags = any() }
            verify(exactly = 1) { mockTextView3.paintFlags = Paint.LINEAR_TEXT_FLAG or Paint.UNDERLINE_TEXT_FLAG }

            verify(exactly = 1) { mockTextView4.paintFlags }
            verify(exactly = 1) { mockTextView4.paintFlags = any() }
            verify(exactly = 1) { mockTextView4.paintFlags = Paint.LINEAR_TEXT_FLAG or Paint.UNDERLINE_TEXT_FLAG }
        }
    }

}