package ru.netology.nmedia.api


import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Token
import java.util.concurrent.TimeUnit

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        val request = AppAuth.getInstance().state.value?.token?.let { token ->
            chain.request().newBuilder()
                .header("Authorization", token)
                .build()
        } ?: chain.request()

        chain.proceed(request)
    }
    .addInterceptor(logging)
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
    suspend fun save(@Body post: PostEntity): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deleteById(@Path("id") id: Long)

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): PostEntity

    @DELETE("posts/{id}/likes")
    suspend fun unLikeById(@Path("id") id: Long): PostEntity

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long):Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun uploadFile(@Part file: MultipartBody.Part ): Media

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>
}

object ApiService {
    val service by lazy {
        retrofit.create<PostApi>()
    }
}