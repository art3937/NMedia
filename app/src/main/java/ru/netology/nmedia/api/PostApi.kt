package ru.netology.nmedia.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostEntity2
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
        if(BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    )
    .build()

private val retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface PostApi {
    @GET("posts")
   suspend fun getAll():Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: PostEntity2): PostEntity2

    @DELETE("posts/{id}")
    suspend fun deleteById(@Path("id") id: Long)

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): PostEntity2

    @DELETE("posts/{id}/likes")
    suspend fun unLikeById(@Path("id") id: Long): PostEntity2

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long):Response<List<Post>>
}

object ApiService {
    val service by lazy {
        retrofit.create<PostApi>()
    }
}