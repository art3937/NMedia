package ru.netology.nmedia.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.fromDtoToEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    private var newPosts: List<Post> = emptyList()
    override val data = dao.getAll().map { it.map { it.toDto() } }

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

    override suspend fun likeById(id: Long, like: Boolean) {
        dao.likeById(id)
        try {
            if (!like) {
                ApiService.service.likeById(id).toDto()
            } else {
                ApiService.service.unLikeById(id).toDto()
            }
        } catch (e: Exception) {
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

    override suspend fun saveById(post: Post, photo: File?): Post {

        val media = photo?.let { saveMedia(it) }
        val postWithAttachment = media?.let {
            post.copy(
                attachment = Attachment(it.id, AttachmentType.IMAGE)
            )
        } ?: post

        val response = ApiService.service.save(PostEntity.fromDto(postWithAttachment))
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }

        val body = response.body() ?: throw ApiError(response.code(), response.message())

        dao.save(PostEntity.fromDto(postWithAttachment))
        return body
    }

    private suspend fun saveMedia(file: File): Media =
        ApiService.service.uploadFile(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody()
            )
        )


    override fun getNewer(id: Long): Flow<Int> = flow {

        while (true) {
            delay(1_000_00)
            val response = ApiService.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            newPosts = response.body() ?: throw ApiError(response.code(), response.message())
            emit(newPosts.size)
        }
    }.catch { e ->
        throw AppError.from(e)
    }

    override suspend fun show() {
        dao.insert(newPosts.fromDtoToEntity())
        newPosts = emptyList()
    }
}