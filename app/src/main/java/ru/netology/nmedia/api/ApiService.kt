package ru.netology.nmedia.api


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Token

interface ApiService {
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

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken)
}

