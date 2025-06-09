package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
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

    fun like(id: Long,like:Boolean) {
        thread {
            val old = _data.value?.posts.orEmpty()
//            _data.value?.posts?.forEach { if(it.id == id) like = it.likedByMe }
            try {
                val post = repository.likeById(id, !like)
                _postCreated.postValue(Unit)
                    val res = _data.value?.posts?.let {
                        data.value?.posts?.map {
                            if (it.id != post.id) it else it.copy(
                                likedByMe = post.likedByMe,
                                countLikes = post.countLikes
                            )
                        }
                    }?.let {
                        FeedModel(posts = it)
                    }

                    _data.postValue(res)

            }catch (e: IOException){
                _data.postValue(_data.value?.copy(posts = old))
            }

        }
    }

    fun sharePost(post: Post) = repository.shareById(post)
    fun removeById(id: Long){
        thread {
            val old = _data.value?.posts.orEmpty()
            try {
                repository.removeById(id)

            }catch (e: IOException){
                _data.postValue(_data.value?.copy(posts = old))
            }
           // _postCreated.postValue(Unit)
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }
//                )
//            )
        }
    }
    //= repository.removeById(id)
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
//            edited.value = empty
            edited.postValue(empty)
        }
    }
}