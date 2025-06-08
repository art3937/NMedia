package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.util.Date
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "Artemy",
    content = "",
    published = "",
    likedByMe = false,

)
class PostViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        load()
    }

 fun load(){
    thread {
        _data.postValue(FeedModel(loading = true))
        val result = try {
            val posts = repository.getAll()
            FeedModel(posts = posts, empty = posts.isEmpty())
        } catch (e: Exception){
            FeedModel(error = e)
        }
        _data.postValue(result)
    }
}
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
        thread {
            edited.value?.let {
                if (it.content != text || it.video != url) {
                    repository.saveById(
                        it.copy(
                            content = text,
                            video = url,
                            published = Date().toString()
                        )
                    )
                    _postCreated.postValue(Unit)
                }
            }
            edited.postValue(empty)
        }
    }
}