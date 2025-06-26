package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
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
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Int) {
                _data.value = FeedModel(error = e)
            }

            override fun onErrorServer(e: String) {
                _data.value =( FeedModel(errorServer = e))
            }

        })
    }

    fun like(id: Long, like: Boolean) {
        thread {
            val old = _data.value?.posts.orEmpty()
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
            edited.value?.let { post ->
                if (post.content != text || post.video != url) {
                    repository.saveById(
                        post.copy(
                            content = text,
                            video = url,
                           // authorAvatar = "none"
                           // attachment = attachment
                            // published = Date()
                            )
                            ,object : PostRepository.GetAllCallBack{
                            override fun onSuccess(posts: List<Post>) {
                             //  _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
                            }

                            override fun onError(e: Int) {
                                _data.value = FeedModel(error = e)
                            }

                            override fun onErrorServer(e: String) {
                                _data.value =( FeedModel(errorServer = e))
                            }
                            }
                    )
                    _postCreated.postValue(Unit)
                }
            }
            edited.postValue(empty)
    }
}