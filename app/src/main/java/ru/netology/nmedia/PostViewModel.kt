package ru.netology.nmedia

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "Artemy",
    content = "",
    published = 0L,
    likedByMe = false,
    authorAvatar = "netology.jpg"
)

class PostViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
    val data: LiveData<FeedModel> = AppAuth.getInstance().state.flatMapLatest { token ->
        repository.data.map { posts ->
            FeedModel(
                posts = posts.map { post -> post.copy(ownerByMe = token?.id == post.authorId) },
                empty = posts.isEmpty()
            )
        }
    }
        .catch { it.stackTraceToString() }
        .asLiveData()

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    val newerCount = data.switchMap {
        repository.getNewer(it.posts.firstOrNull()?.id ?: 0)
            .catch { _state.postValue(FeedModelState(error = true)) }
            .asLiveData()
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
            runCatching {
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
            }.onFailure {
                _state.value = FeedModelState(error = true, errorServer = it.stackTraceToString())
            }
        }
    }

    fun sharePost(post: Post) = viewModelScope.launch { repository.shareById(post) }


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


    fun changeContentAndSave(text: String) {
        _state.postValue(FeedModelState(loading = true))
        viewModelScope.launch {
            try {
                edited.value?.let { it ->
                    if (it.content != text) {
                        repository.saveById(
                            it.copy(content = text),
                            _photo.value?.file
                        )
                    }
                }
            } catch (e: Exception) {
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
            runCatching {
                repository.getAllAsync()
                _state.value = FeedModelState()
            }.onFailure {
                _state.value = FeedModelState(error = true, errorServer = it.stackTraceToString())
            }
        }
    }

    fun loadDaoNewPost() {
        viewModelScope.launch {
            repository.show()
        }
    }

    fun savePhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }

    fun removePhoto() {
        _photo.value = null
    }
}