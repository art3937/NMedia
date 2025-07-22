package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    // fun getAll(): List<Post>
    val data: LiveData<List<Post>>
    suspend fun likeById(id: Long, like: Boolean)
    suspend fun shareById(post: Post)
    suspend fun removeById(id: Long)
    suspend fun saveById(post: Post): Post

    suspend fun getAllAsync()
}