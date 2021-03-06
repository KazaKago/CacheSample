package com.kazakago.storeflowable.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kazakago.storeflowable.example.model.GithubMeta
import com.kazakago.storeflowable.example.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GithubMetaViewModel(application: Application) : AndroidViewModel(application) {

    private val _githubMeta = MutableStateFlow<GithubMeta?>(null)
    val githubMeta: StateFlow<GithubMeta?> get() = _githubMeta
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _error = MutableStateFlow<Exception?>(null)
    val error: StateFlow<Exception?> get() = _error
    private val githubRepository = GithubRepository()

    init {
        subscribe()
    }

    fun refresh() = viewModelScope.launch {
        githubRepository.refreshMeta()
    }

    fun retry() = viewModelScope.launch {
        githubRepository.refreshMeta()
    }

    private fun subscribe() = viewModelScope.launch {
        githubRepository.followMeta().collect {
            it.doAction(
                onFixed = {
                    it.content.doAction(
                        onExist = { githubMeta ->
                            _githubMeta.value = githubMeta
                            _isLoading.value = false
                            _error.value = null
                        },
                        onNotExist = {
                            _githubMeta.value = null
                            _isLoading.value = false
                            _error.value = null
                        }
                    )
                },
                onLoading = {
                    it.content.doAction(
                        onExist = { githubMeta ->
                            _githubMeta.value = githubMeta
                            _isLoading.value = true
                            _error.value = null
                        },
                        onNotExist = {
                            _githubMeta.value = null
                            _isLoading.value = true
                            _error.value = null
                        }
                    )
                },
                onError = { exception ->
                    it.content.doAction(
                        onExist = { githubMeta ->
                            _githubMeta.value = githubMeta
                            _isLoading.value = false
                            _error.value = null
                        },
                        onNotExist = {
                            _githubMeta.value = null
                            _isLoading.value = false
                            _error.value = exception
                        }
                    )
                }
            )
        }
    }
}
