package ru.netology.nmedia.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
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
import ru.netology.nmedia.entity.PostEntity
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.SECONDS)
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
    fun getAll(): Call<List<PostEntity>>

    @POST("posts")
    fun save(@Body post: PostEntity): Call<PostEntity>

    @DELETE("posts/{id}")
    fun deleteById(@Path("id") id: Long): Call<Unit>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long):Call<PostEntity>

    @DELETE("posts/{id}/likes")
    fun unLikeById(@Path("id") id: Long):Call<PostEntity>
}

object ApiService {
    val service by lazy {
        retrofit.create<PostApi>()
    }
}