package com.colinmaroney.fetchexercise.ui.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.colinmaroney.fetchexercise.data.HiringEntry
import com.colinmaroney.fetchexercise.repository.HiringRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HiringViewModel(private val repo: HiringRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val _hiringData: MutableLiveData<Map<Int, String>?> = MutableLiveData(null)
    val hiringData: LiveData<Map<Int,String>?>
        get() = _hiringData

    private val _hiringError: MutableLiveData<Boolean> = MutableLiveData(false)
    val hiringError: LiveData<Boolean>
        get() = _hiringError

    class Factory(private val repo: HiringRepo, private val dispatcher: CoroutineDispatcher? = null): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return dispatcher?.let {
                HiringViewModel(repo, it) as T
            } ?: HiringViewModel(repo) as T
        }
    }

    fun fetchHiringData() {
        viewModelScope.launch(dispatcher) {
            try {
                val entries = repo.getHiringData()
                val filteredEntries = entries.filter { it.name.isNullOrEmpty().not() }
                    .sortedWith (
                        // we have filtered out the null names from
                        // this list, so the force-unrwap is ok
                        compareBy<HiringEntry> { it.listId }.thenBy { itemNumber(it.name!!) }
                    )
                val entryMap = buildMapFromEntries(filteredEntries)
                _hiringData.postValue(entryMap)
            } catch (e: HttpException) {
                _hiringError.postValue(true)
            }
        }
    }

    @VisibleForTesting
    internal fun buildMapFromEntries(entries: List<HiringEntry>): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        entries.forEach { item ->
            map[item.listId]?.let {
                val newStr = "$it, ${item.name}"
                map[item.listId] = newStr
            } ?: run {
                // we have filtered out the null names from
                // this list, so the force-unrwap is ok
                map[item.listId] = item.name!!
            }
        }

        return map.toMap()
    }

    @VisibleForTesting
    fun itemNumber(name: String): Int {
        val regex = Regex("\\d+")
        return try {
            val match = regex.findAll(name).first().value
            match.toInt()
        } catch (e: NoSuchElementException) {
            0
        }
    }
}