package ru.netology.nmedia.model

import android.content.Context
import android.widget.Toast
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)


data class FeedModelState(
    val errorServer: String = "",
    val error: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false
)