package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    // fun getAll(): List<Post>
    val data: Flow<List<Post>>
    fun getNewer(id: Long): Flow<Int>
    suspend fun likeById(id: Long, like: Boolean)
    suspend fun shareById(post: Post)
    suspend fun removeById(id: Long)
    suspend fun saveById(post: Post): Post

    suspend fun getAllAsync()
}