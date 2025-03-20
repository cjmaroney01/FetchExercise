package com.colinmaroney.fetchexercise.viewmodel

import com.colinmaroney.fetchexercise.data.HiringEntry
import com.colinmaroney.fetchexercise.network.HiringRepo
import com.colinmaroney.fetchexercise.utils.InstantTaskExecutorRuleForJUnit5
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorRuleForJUnit5::class)
class HiringViewModelTest {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    private val mockRepo: HiringRepo = mockk()

    private val viewModel = spyk(HiringViewModel(mockRepo, testDispatcher), recordPrivateCalls = true)

    val mockData = listOf(
        HiringEntry(1, 1, "foo 12"),
        HiringEntry(2, 1, "bar 2"),
        HiringEntry(3, 1, null),
        HiringEntry(4, 1, ""),
        HiringEntry(5, 1, "baz 1"),
        HiringEntry(6, 2, "xxx 9"),
        HiringEntry(7, 2, null),
        HiringEntry(8, 2, "yyy 12")
    )

    val mockMap = mutableMapOf<Int, String>()

    @BeforeEach
    fun beforeEach() {
        clearMocks(mockRepo)
        mockMap.clear()
        mockData.filter { it.name.isNullOrEmpty().not() }.sortedWith(
            compareBy<HiringEntry> { it.listId }.thenBy { getNum(it.name!!) }
        ).forEach { item ->
            mockMap[item.listId]?.let {
                val newStr = "$it, ${item.name}"
                mockMap[item.listId] = newStr
            } ?: run {
                mockMap[item.listId] = item.name!!
            }
        }
    }

    fun getNum(name: String): Int {
        val regex = Regex("\\d+")
        return try {
            val match = regex.findAll(name).first().value
            match.toInt()
        } catch (e: NoSuchElementException) {
            0
        }
    }

    @Nested
    @DisplayName("fetchHiringData")
    inner class FetchHiringDataTests {
        @BeforeEach
        fun beforeEach() {
            coEvery { mockRepo.getHiringData() } returns mockData
        }

        @Test
        fun `should call repo for hiring data`() = runTest {
            viewModel.fetchHiringData()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(mockMap, viewModel.hiringData.value)
            assertFalse(viewModel.hiringError.value ?: false)

            coVerify(exactly = 1) { mockRepo.getHiringData() }
        }

        @Test
        fun `should call buildMapFromEntries`() = runTest {
            every { viewModel.buildMapFromEntries(any()) } returns mockMap
            viewModel.fetchHiringData()
            testScheduler.advanceUntilIdle()
            verify(exactly = 1) { viewModel.buildMapFromEntries(any()) }
        }

        @Test
        fun `should catch HttpException and set error`() = runTest {
            coEvery { mockRepo.getHiringData() } throws HttpException(Response.error<List<HiringEntry>>(500, mockk() {
                every { contentType() } returns mockk()
                every { contentLength() } returns 100
            }))

            viewModel.fetchHiringData()
            testScheduler.advanceUntilIdle()

            assertTrue(viewModel.hiringError.value ?: false)
        }
    }

    @Nested
    @DisplayName("buildMapFromEntries")
    inner class BuildMapFromEntriesTests {

        @Test
        fun `should return a proper map from emtries`() {
            assertEquals(mockMap, viewModel.buildMapFromEntries(mockData.filter { it.name.isNullOrEmpty().not() }.sortedWith(
                compareBy<HiringEntry> { it.listId }.thenBy { getNum(it.name!!) }
            )))
        }
    }

    @Nested
    @DisplayName("itemNumber")
    inner class ItemNumberTests {
        @Test
        fun `should return the number portion of the input string`() {
            assertEquals(123, viewModel.itemNumber("foo 123"))
        }

        @Test
        fun `should return the first number in the input string`() {
            assertEquals(321, viewModel.itemNumber("foobar 321 456"))
        }

        @Test
        fun `should return 0 if there is no number in the input string`() {
            assertEquals(0, viewModel.itemNumber("foo bar"))
        }
    }
}