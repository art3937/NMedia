package ru.netology.nmedia.model

import android.content.Context
import android.widget.Toast
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)
//{
//    val isError: Boolean = error != 0 || errorServer.isNotEmpty()
//
//    fun errorToString(context: Context): String {
//        var result = ""
//        if (errorServer.isNotEmpty()) {
//            Toast.makeText(context, errorServer, Toast.LENGTH_LONG).show()
//        }
//        when (error) {
//            400 -> result = context.getString(R.string.network_error)
//            500 -> {
//                result = "Ошибка Сервера Код 500"
//                Toast.makeText(context, "Ошибка Сервера код $error", Toast.LENGTH_LONG).show()
//            }
//            else -> result = context.getString(R.string.unknown_error)
//        }
//        return result
//    }
//}

data class FeedModelState(
    val errorServer: String = "",
    val error: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false
)