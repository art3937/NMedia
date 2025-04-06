package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false
)
class PostViewModel: ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun like(id: Long) = repository.likeById(id)
    fun sharePost(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun edit(post: Post) {
        edited.value = post
    }
    fun cancel(){
        edited.value = empty
    }

    fun changeContentAndSave(text: String) {
        edited.value?.let {
            if (it.content != text) {
                repository.saveById(it.copy(content = text))
            }
        }
        edited.value = empty
    }
}