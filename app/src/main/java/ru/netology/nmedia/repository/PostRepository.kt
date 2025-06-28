package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
   // fun getAll(): List<Post>
    fun likeById(id: Long,like:Boolean,callback: GetLikeCallBack)
    fun shareById(post: Post)
    fun removeById(id: Long)
    fun saveById(post: Post,callback: GetLikeCallBack)

    fun getAllAsync(callback: GetAllCallBack)

    interface  GetAllCallBack{
        fun onSuccess(posts: List<Post>)
        fun onError(e: Int)
        fun onErrorServer(e: String)
    }

    interface GetLikeCallBack{
        fun onSuccess(post:Post)
        fun onError(e: Int)
        fun onErrorServer(e: String)
    }
}