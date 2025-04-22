package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFilesImpl
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,

)
class PostViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao)
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun like(id: Long) = repository.likeById(id)
    fun sharePost(post: Post) = repository.shareById(post)
    fun removeById(id: Long) = repository.removeById(id)
    fun edit(post: Post) {
        edited.value = post
    }
    fun cancel(){
        edited.value = empty
    }

    fun changeContentAndSave(text: String,url: String) {
        edited.value?.let {
            if (it.content != text || it.video != url ) {
                repository.saveById(it.copy(content = text, video = url))
            }
        }
        edited.value = empty
    }
}