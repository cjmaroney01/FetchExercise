package com.colinmaroney.fetchexercise.network

import com.colinmaroney.fetchexercise.service.HiringService
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class HiringRepoTest {
    private lateinit var repo: HiringRepo
    private val mockRetrofit: Retrofit = mockk()
    private val mockRetrofitBuilder: Retrofit.Builder = mockk()
    private val mockService: HiringService = mockk()

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = UnconfinedTestDispatcher(scheduler)

    @BeforeEach
    fun beforeEach() {
        clearMocks(mockRetrofit, mockRetrofitBuilder, mockService)

        mockkConstructor(Retrofit.Builder::class)
        every { anyConstructed<Retrofit.Builder>().baseUrl(any<String>()) } returns mockRetrofitBuilder
        every { mockRetrofitBuilder.addConverterFactory(any()) } returns mockRetrofitBuilder
        every { mockRetrofitBuilder.build() } returns mockRetrofit

        every { mockRetrofit.create(HiringService::class.java) } returns mockService

        coEvery { mockService.getHiringData() } returns listOf()

        repo = spyk(HiringRepo(), recordPrivateCalls = true)
    }

    @Test
    fun `should call service and get hiring data`() = runTest(dispatcher) {
        assertNotNull(repo.getHiringData())
        coVerify(exactly = 1) { mockService.getHiringData() }
    }

    @Test
    fun `should get retrofit and service on init`() {
        verify(exactly = 1) { anyConstructed<Retrofit.Builder>().baseUrl(any<String>()) }
        verify(exactly = 1) { anyConstructed<Retrofit.Builder>().baseUrl(HiringRepo.baseUrl) }
        verify(exactly = 1) { mockRetrofitBuilder.addConverterFactory(any()) }
        verify(exactly = 1) { mockRetrofitBuilder.build() }

        verify(exactly = 1) { mockRetrofit.create(HiringService::class.java) }
    }
}