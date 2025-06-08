package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import java.lang.reflect.Type
import java.util.Date
import java.util.concurrent.TimeUnit

class PostRepositoryImpl() : PostRepository {
private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30,TimeUnit.SECONDS)
    .build()
    private val gson = Gson()

    private companion object{
        const val BASE_URL ="http://10.0.2.2:9999"
        val jsonType = "application/json".toMediaType()
        val postsType: Type = object : TypeToken<List<Post>>() {}.type
    }

//    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list-> list.map{it.toDto()} }
override fun getAll():List<Post> {
val call = okHttpClient.newCall(
    Request.Builder()
        .url("$BASE_URL/api/slow/posts")
        .build()
)
    val response = call.execute()

    val responseText = response.body?.string() ?: error("Response body is null")

    return gson.fromJson(responseText, postsType)
}

    override fun saveById(post: Post): Post{
        val call = okHttpClient.newCall(
            Request.Builder()
                .url("$BASE_URL/api/slow/posts")
                .post(gson.toJson(post).toRequestBody(jsonType))
                .build()
        )
        val response = call.execute()

        val responseText = response.body?.string() ?: error("Response body is null")
        return gson.fromJson(responseText, Post::class.java)
    }
    override fun likeById(id: Long) {
        TODO()
    }

    override fun shareById(post: Post) {
        TODO()
    }

    override fun removeById(id: Long) {
        TODO()
    }
}