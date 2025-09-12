package ru.netology.nmedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.State
import java.io.IOException

class SignInViewModel : ViewModel() {

    private val _state = MutableLiveData(State())
    val state: LiveData<State>
        get() = _state

    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            runCatching {
                val response = ApiService.service.updateUser(login, password)
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                AppAuth.getInstance().saveAuth(
                    body.id, body.token
                )
                _state.value = State()

            }.onFailure {
                _state.value = State(error = true, errorServer = it.stackTraceToString())
            }
        }
    }
}