package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class PostRepositoryImpl() : PostRepository {

    private val okHttpClient = OkHttpClient.Builder()

        .connectTimeout(30, TimeUnit.SECONDS).build()
    private val gson = Gson()

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        val jsonType = "application/json".toMediaType()
        val postsType: Type = object : TypeToken<List<PostEntity>>() {}.type
    }


//    override fun getAll(): List<Post> {
//        val call = okHttpClient.newCall(
//            Request.Builder()
//                .url("$BASE_URL/api/slow/posts")
//                .build()
//        )
//        val response = call.execute()
//
//        val responseText = response.body?.string() ?: error("Response body is null")
//
//        return gson.fromJson<List<PostEntity>?>(responseText, postsType).map { it.toDto() }
//    }

    override fun getAllAsync(callback: PostRepository.GetAllCallBack) {
        println("1  ${Thread.currentThread().name}")
        val call = okHttpClient.newCall(
            Request.Builder()
                .url("$BASE_URL/api/slow/posts")
                .build()
        )
       call.enqueue(object : Callback{

            override fun onResponse(call: Call, response: Response) {
                try {
                    println("2  ${Thread.currentThread().name}")
                    val posts = response.body?.string() ?: error("Response body is null")
                    callback.onSuccess(
                        gson.fromJson<List<PostEntity>?>(posts, postsType).map { it.toDto() })
                }catch (e: Exception){
                    callback.onError(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
        println("3  ${Thread.currentThread().name}")
    }

    override fun saveById(post: Post): Post {
        val call = okHttpClient.newCall(
            Request.Builder().url("$BASE_URL/api/slow/posts")
                .post(gson.toJson(PostEntity.fromDto(post)).toRequestBody(jsonType)).build()
        )
        val response = call.execute()

        val responseText = response.body?.string() ?: error("Response body is null")
        return gson.fromJson(responseText, Post::class.java)
    }


    override fun likeById(id: Long, like: Boolean): Post {
        val call = if (!like) {
            okHttpClient.newCall(
                Request.Builder().url("$BASE_URL/api/posts/${id}/likes")
                    .delete(gson.toJson(id).toRequestBody(jsonType)).build()
            )
        } else {
            okHttpClient.newCall(
                Request.Builder().url("$BASE_URL/api/posts/${id}/likes")
                    .post(gson.toJson(id).toRequestBody(jsonType)).build()
            )
        }
        val response = call.execute()
        val responseText = response.body?.string() ?: error("Response body is null")

        return gson.fromJson(responseText, PostEntity::class.java).toDto()
    }

    override fun shareById(post: Post) {
        TODO()
    }

    override fun removeById(id: Long): String {

        val call = okHttpClient.newCall(
            Request.Builder().url("$BASE_URL/api/slow/posts/${id}")
                .delete(gson.toJson(id).toRequestBody(jsonType)).build()
        )

        val response = call.execute()

        val responseText = response.body?.string() ?: error("Response body is null")
        return responseText
    }
}