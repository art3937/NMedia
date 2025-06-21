package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

interface PostRepository {
   // fun getAll(): List<Post>
    fun likeById(id: Long,like:Boolean):Post
    fun shareById(post: Post)
    fun removeById(id: Long)
    fun saveById(post: Post)

    fun getAllAsync(callback: GetAllCallBack)

    interface  GetAllCallBack{
        fun onSuccess(posts: List<Post>)
        fun onError(e: Throwable)
    }
}