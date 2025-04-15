package ru.netology.nmedia.repository

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Post

class PostRepositoryFilesImpl(private val context: Context) : PostRepository {


    private var nextId = 1L
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
            data.value = posts
        }
    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data

    init {
        val file = context.filesDir.resolve(FILE_NAME)
        if (file.exists()) {
            context.openFileInput(FILE_NAME).bufferedReader().use { it ->
                posts = gson.fromJson(it, type)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1

            }
        }
    }



    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countLikes = if (!it.likedByMe) it.countLikes + 1 else it.countLikes - 1
            )
        }
    }

    override fun shareById(id: Long) {
        posts = posts.map { if (it.id == id) it.copy(countRepost = it.countRepost + 1) else it }
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
    }

    override fun saveById(post: Post) {
        posts = if (post.id == 0L) {
            listOf(post.copy(id = nextId++, author = "Mi")) + posts
        } else {
            posts.map { if (it.id != post.id) it else it.copy(content = post.content) }
        }
    }

    private fun sync() {
        context.openFileOutput(FILE_NAME,Context.MODE_PRIVATE).bufferedWriter().use {
          it.write(gson.toJson(posts))
        }
    }

    companion object {
        private const val FILE_NAME = "posts.json"
        private val gson = Gson()
        private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    }
}