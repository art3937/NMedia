package ru.netology.nmedia.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "Artemy",
    content = "",
    published = 0L,
    likedByMe = false,
    authorAvatar = "netology.jpg"
)

@HiltViewModel

class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

    val data: Flow<PagingData<Post>> = appAuth.state.flatMapLatest { token ->
        repository.data.map { posts ->
            posts.map { post -> post.copy(ownerByMe = token?.id == post.authorId) }
        }
    }.flowOn(Dispatchers.Default)


    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

//    val newerCount = data.switchMap {
//        repository.getNewer(it.posts.firstOrNull()?.id ?: 0)
//            .catch { _state.postValue(FeedModelState(error = true)) }
//            .asLiveData()
//    }
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
        _postCreated.value = Unit
    }


    fun changeContentAndSave(text: String) {
        _postCreated.value = Unit
        _state.postValue(FeedModelState(loading = true))
        viewModelScope.launch {
            try {
                _postCreated.value =  edited.value?.let { it ->
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
    }// делаем через пагинг3

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