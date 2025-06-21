package ru.netology.nmedia.repository

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.activity.AppActivity
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import kotlin.coroutines.coroutineContext

class PostRepositoryImpl() : PostRepository {


    fun getAll(): List<Post> {
        return ApiService.service.getAll().execute().let { posts ->
            posts.body()?.map { it.toDto() } ?: throw RuntimeException("body is null")
        }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallBack) {
        println("1  ${Thread.currentThread().name}")
        ApiService.service.getAll().enqueue(object : Callback<List<PostEntity>> {
            override fun onResponse(
                call: Call<List<PostEntity>>, response: Response<List<PostEntity>>
            ) {
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("body is null"))
                    return
                }
                callback.onSuccess(body.map { it.toDto() })
            }

            override fun onFailure(
                call: Call<List<PostEntity>>, throwable: Throwable
            ) {
                callback.onError(throwable)
            }
        })
    }

    override fun saveById(post: Post) {
       ApiService.service.save(PostEntity.fromDto(post)).execute()
    }

    override fun likeById(id: Long, like: Boolean): Post {
        TODO("Not yet implemented")
    }


//    override fun likeById(id: Long, like: Boolean): Post {
//        val call = if (!like) {
//            okHttpClient.newCall(
//                Request.Builder().url("$BASE_URL/api/posts/${id}/likes")
//                    .delete(gson.toJson(id).toRequestBody(jsonType)).build()
//            )
//        } else {
//            okHttpClient.newCall(
//                Request.Builder().url("$BASE_URL/api/posts/${id}/likes")
//                    .post(gson.toJson(id).toRequestBody(jsonType)).build()
//            )
//        }
//        val response = call.execute()
//        val responseText = response.body?.string() ?: error("Response body is null")
//
//        return gson.fromJson(responseText, PostEntity::class.java).toDto()
//    }

    override fun shareById(post: Post) {
        TODO()
    }

    override fun removeById(id: Long) {
        ApiService.service.deleteById(id).execute()
    }
}