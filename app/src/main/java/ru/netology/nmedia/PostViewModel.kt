package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "Artemy",
    content = "",
    published = 0L,
    likedByMe = false,
    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    val obj = Object()

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
      //  load()
        loadPosts()
    }

    fun load() {
//        thread {
//            _data.postValue(FeedModel(loading = true))
//            val result = try {
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: Exception) {
//                FeedModel(error = e)
//            }
//            _data.postValue(result)
//        }
    }

    fun loadPosts(){
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.GetAllCallBack{
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue( FeedModel(error = e))
            }

        })
    }

    fun like(id: Long, like: Boolean) {
        thread {
            val old = _data.value?.posts.orEmpty()
//            _data.value?.posts?.forEach { if(it.id == id) like = it.likedByMe }
            try {
                val post = repository.likeById(id, !like)
                val res = _data.value?.posts?.let {
                    data.value?.posts?.map {
                        if (it.id != post.id) it else it.copy(
                            likedByMe = post.likedByMe,
                            likes = post.likes
                        )
                    }
                }?.let {
                    FeedModel(posts = it)
                }

                _data.postValue(res)

            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }

        }
    }

    fun sharePost(post: Post) = repository.shareById(post)
    fun removeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
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
        thread {
            edited.value?.let {
                if (it.content != text || it.video != url) {
                    repository.saveById(
                        it.copy(
                            content = text,
                            video = url,
                            // published = Date()
                        )
                    )
                    _postCreated.postValue(Unit)

                }
//            edited.value = empty
                edited.postValue(empty)
            }
        }
    }
}