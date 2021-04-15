package com.udacity.capstone.poetry.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.capstone.poetry.database.PoemsDisplayFilter
import com.udacity.capstone.poetry.database.getDatabase
import com.udacity.capstone.poetry.domain.Poem
import com.udacity.capstone.poetry.repository.PoetryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PoetryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val poemsRepository = PoetryRepository(database)
    private val _navigateToSelectedPoem = MutableLiveData<Poem>()

    var poems = poemsRepository.getPoems()
    val showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showNoPoems: MutableLiveData<Boolean> = MutableLiveData()
    var poemToUpdate = MutableLiveData<Poem?>()

    private val _poemOfDayList =  MutableLiveData<List<Poem>>()
    val poemOfDayList: LiveData<List<Poem>>
        get() = _poemOfDayList

    val navigateToSelectedPoem: LiveData<Poem>
        get() = _navigateToSelectedPoem

    fun displayPoem(poem: Poem?) {
        _navigateToSelectedPoem.value = poem
    }

    fun displayPoemComplete() {
        _navigateToSelectedPoem.value = null
    }

    private suspend fun updatePoem(selectedPoem: Poem) {
        withContext(Dispatchers.IO) {
            poemsRepository.upsertPoem(selectedPoem)
        }
    }

    fun addToFavorites() {
        viewModelScope.launch {
            val selectedPoem = poemToUpdate.value ?: return@launch
            selectedPoem.isFavorite = true
            //sync favorite indicator on Poem of Day as well
            if (!_poemOfDayList.value.isNullOrEmpty() && selectedPoem.author.equals(_poemOfDayList.value?.get(0)?.author)
                && selectedPoem.title.equals(_poemOfDayList.value?.get(0)?.title)) {
                _poemOfDayList.value?.get(0)?.isFavorite = true
            }
            updatePoem(selectedPoem)

        }
    }

    fun removeFromFavorites() {
        viewModelScope.launch {
            val selectedPoem = poemToUpdate.value ?: return@launch
            selectedPoem.isFavorite = false
            //sync favorite indicator on Poem of Day as well
            if (!_poemOfDayList.value.isNullOrEmpty() && selectedPoem.author.equals(
                    _poemOfDayList.value?.get(0)?.author) && selectedPoem.title.equals(_poemOfDayList.value?.get(0)?.title)) {
                _poemOfDayList.value?.get(0)?.isFavorite = false
            }
            updatePoem(selectedPoem)
        }
    }

    init {
        viewModelScope.launch {
            _poemOfDayList.value = poemsRepository.getPoemOfTheDay()
            poemsRepository.refreshPoems()
        }
    }

    fun updateFilter(filter: PoemsDisplayFilter): LiveData<List<Poem>> {
        when (filter) {
            PoemsDisplayFilter.BROWSE_ALL_POEMS -> poems = poemsRepository.getPoems()
            PoemsDisplayFilter.BROWSE_FAVORITES -> poems = poemsRepository.getFavoritePoems()
            PoemsDisplayFilter.POEM_OF_THE_DAY -> poems = _poemOfDayList
        }
        return poems
    }

}