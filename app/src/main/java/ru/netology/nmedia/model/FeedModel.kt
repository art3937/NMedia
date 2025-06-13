package ru.netology.nmedia.model

import android.content.Context
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import java.io.IOException

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val error: Throwable? = null,
    val loading: Boolean = false,
    val empty: Boolean = false
){
   val isError: Boolean = error != null

    fun errorToString(context: Context): String = when (error){
       is IOException -> context.getString(R.string.network_error)
        else -> context.getString(R.string.unknown_error)
    }
}