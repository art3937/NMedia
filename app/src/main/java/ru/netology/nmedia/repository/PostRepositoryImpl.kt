package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostEntity2
import ru.netology.nmedia.entity.fromDtoToEntity


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override  val data: LiveData<List<Post>> = dao.getAll().map {it.map (PostEntity::toDto) }


    override suspend fun getAllAsync() {
val posts: List<Post> = ApiService.service.getAll()
        dao.insert( posts.fromDtoToEntity())
    }

    override suspend fun likeById(id: Long, like: Boolean){
        dao.likeById(id)
        try {
            if (!like) {
                ApiService.service.likeById(id).toDto()
            } else {
                ApiService.service.unLikeById(id).toDto()
            }
        }
        catch (e: Exception){
            e.printStackTrace()
           // dao.likeById(id)
        }
    }

    override suspend fun shareById(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        ApiService.service.deleteById(id)

    }

    override suspend fun saveById(post: Post): Post {
val response = ApiService.service.save(PostEntity2.fromDto(post)).toDto()
        dao.save(PostEntity.fromDto(post))
        return response
    }

}