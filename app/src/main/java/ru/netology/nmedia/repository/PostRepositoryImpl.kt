package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity


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
                    callback.onError(response.code())
                    println("Вот она ошибочка!!!!!  ${response.code()}")
                    return
                }
                callback.onSuccess(body.map { it.toDto() })
                println("Вот она удачная загрузка!!!!!  ${response.code()}")
            }

            override fun onFailure(
                call: Call<List<PostEntity>>, throwable: Throwable
            ) {
                callback.onErrorServer("Нет связи с сервером $throwable")
            }
        })
    }

    override fun saveById(post: Post, callback: PostRepository.GetLikeCallBack) {

        ApiService.service.save(PostEntity.fromDto(post)).enqueue(object : Callback<PostEntity> {
            override fun onResponse(call: Call<PostEntity>, response: Response<PostEntity>) {
                val body = response.body() ?: run {
                    callback.onErrorServer("Пост не сохранился код : ${response.code()}")
                    println("Вот она ошибочка!!!!!  ${response.code()}")
                    return
                }
                callback.onSuccess(body.toDto())
                println("Вот она удачная загрузка!!!!!  ${response.code()}")
            }

            override fun onFailure(call: Call<PostEntity>, throwable: Throwable) {
                callback.onErrorServer("Нет связи с сервером $throwable")
            }


        })
    }

    override fun likeById(id: Long, like: Boolean, callback: PostRepository.GetLikeCallBack) {
        var resultLike = ApiService.service.likeById(id)
        if (!like) {
            resultLike = ApiService.service.unLikeById(id)
        }
        resultLike.enqueue(object : Callback<PostEntity> {
            override fun onResponse(call: Call<PostEntity>, response: Response<PostEntity>) {
                val body = response.body() ?: run {
                    callback.onErrorServer("Пост не лайкнут : ${response.code()}")
                    println("Вот она ошибочка!!!!!  ${response.code()}")
                    return
                }
                callback.onSuccess(body.toDto())
                println("Вот она удачная загрузка!!!!!  ${response.code()}")
            }

            override fun onFailure(call: Call<PostEntity>, throwable: Throwable) {
                callback.onErrorServer("Нет связи с сервером $throwable")
            }


        })
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