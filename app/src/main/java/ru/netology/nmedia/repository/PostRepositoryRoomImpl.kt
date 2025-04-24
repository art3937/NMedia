package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositoryRoomImpl(
    private val dao: PostDao
) : PostRepository {


//    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list-> list.map{it.toDto()} }
override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list-> list.map(PostEntity::toDto) }

    override fun saveById(post: Post) = dao.save(PostEntity.fromDto(post))

    override fun likeById(id: Long)= dao.likeById(id)

    override fun shareById(post: Post) = dao.oneShare(post.id)

    override fun removeById(id: Long) = dao.removeById(id)
}