package ru.netology.nmedia.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.auth.AppAuth

import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    val isAuthorized: LiveData<Boolean> = appAuth
        .state
        .map { it != null }
        .asLiveData(Dispatchers.Default)


}