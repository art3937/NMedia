package ru.netology.nmedia

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import java.time.OffsetDateTime
import kotlin.concurrent.thread

private val empty = Post(
    id = 0, author = "Artemy", content = "", published = 0L, likedByMe = false, video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
     val data: LiveData<FeedModel> = repository.data.map {
         FeedModel(
             posts = it,
             empty = it.isEmpty()
         )
     }

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _state.value = FeedModelState(loading = true)
        viewModelScope.launch {
            runCatching{
                repository.getAllAsync()
                _state.value = FeedModelState()
            }.onFailure {
                _state.value = FeedModelState(error = true, errorServer = it.stackTraceToString())
            }
        }
    }

    fun like(id: Long, like: Boolean) {
       viewModelScope.launch {
           runCatching {
               repository.likeById(id, like)
           }.onFailure{
               _state.value = FeedModelState(error = true, errorServer = it.stackTraceToString())
           }
       }
    }

    fun sharePost(post: Post) = viewModelScope.launch { repository.shareById(post)}


    fun removeById(id: Long) {
     viewModelScope.launch {
         repository.removeById(id)
     }
    }

    //= repository.removeById(id)
    fun edit(post: Post) {
        edited.value = post
    }

    fun cancel() {
        edited.value = empty
    }



    fun changeContentAndSave(text: String, url: String) {
       _state.postValue(FeedModelState(loading = true))
        viewModelScope.launch {
            try {
                edited.value?.let { it ->
                    if (it.content != text || it.video != url) {
                        repository.saveById(
                            it.copy(
                                content = text,
                                video = url,
                                // authorAvatar = "none"
                                // attachment = attachment
                                // published = Date()
                            )
                        )
                    }
                }
            }
            catch (e: Exception){
                println("нет связи")
                return@launch
            }
        }
        _postCreated.value = Unit
        edited.postValue(empty)
    }

    fun refresh() {
        _state.value = FeedModelState(refreshing = true)
        viewModelScope.launch {
            runCatching{
                repository.getAllAsync()
                _state.value = FeedModelState()
            }.onFailure {
                _state.value = FeedModelState(error = true, errorServer = it.stackTraceToString())
            }
        }
    }
}