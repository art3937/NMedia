package ru.netology.nmedia

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel: ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun like(id: Long) = repository.likeById(id)
    fun sharePost(id: Long) = repository.shareById(id)
}