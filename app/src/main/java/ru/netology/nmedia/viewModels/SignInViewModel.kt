package ru.netology.nmedia.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AppAuth.AppAuthEntryPoint
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.State
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext


@HiltViewModel
class SignInViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appAuth: AppAuth
) : ViewModel() {

    private val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
    private val _state = MutableLiveData(State())
    val state: LiveData<State>
        get() = _state

    fun signIn(login: String, password: String){

        viewModelScope.launch {
            runCatching {
                val response = entryPoint.getApiService().updateUser(login,password)
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                appAuth.saveAuth(
                    body.id, body.token
                )
                _state.value = State()
            }.onFailure {
                _state.value = State(error = true, errorServer = it.stackTraceToString())
            }
        }
    }
}