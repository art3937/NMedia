package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.model.Token


class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow<Token?>(null)
    val state = _state.asStateFlow()

    init {
        val id = prefs.getLong(ID_KEY, 0L)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id == 0L || token == null) {
            prefs.edit {
                clear()
            }
        } else {
            _state.value = Token(id = id, token = token)
        }
    }


    @Synchronized
    fun saveAuth(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        _state.value = Token(id = id, token = token)
    }

    @Synchronized
    fun removeAuth() {
        prefs.edit { clear() }
        _state.value = null
    }

    companion object {
        private const val ID_KEY = "ID-KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
        private var INSTANCE: AppAuth? = null

        fun getInstance(): AppAuth = requireNotNull(INSTANCE) {
            "required initAuth() first"
        }

        fun initAuth(context: Context): AppAuth {
            INSTANCE?.let { return it }

            synchronized(this) {
                INSTANCE?.let { return it }
                val appAuth = AppAuth(context.applicationContext)
                INSTANCE = appAuth
                return appAuth
            }
        }
    }
}