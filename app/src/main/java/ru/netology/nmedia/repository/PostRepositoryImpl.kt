package ru.netology.nmedia.repository

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostEntity2
import ru.netology.nmedia.entity.fromDtoToEntity
import ru.netology.nmedia.entity.fromDtoToEntity2
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
private var newPosts: List<Post> = emptyList()
    override val data = dao.getAll().map { it.map { it.copy(showPost = true).toDto() } }

    override suspend fun getAllAsync() {
        try {
            val response = ApiService.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.fromDtoToEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
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
        dao.sharedById(post.id)
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

    override fun getNewer(id: Long): Flow<Int> = flow {

        while (true) {
            delay(10_000)
            val response = ApiService.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
          body.map { it.showPost = false }
            newPosts = body
            emit(body.size)
        }
    }.catch {
        e -> throw  AppError.from(e)
    }

    override suspend fun show(){
    newPosts.map { it.showPost = true }
    dao.insert(newPosts.fromDtoToEntity())
        newPosts = emptyList()
}
}