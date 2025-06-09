package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long,like:Boolean):Post
    fun shareById(post: Post)
    fun removeById(id: Long):String
    fun saveById(post: Post): Post
}