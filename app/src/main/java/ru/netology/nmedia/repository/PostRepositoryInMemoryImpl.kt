package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 0L
    private var posts = listOf(
        Post(
            id = nextId++,
        author = "Нетология. Университет интернет-проффесий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
            likedByMe = false
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false
        ), Post(
            id = nextId++,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false
        )
    )

    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data


    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countLikes = if (!it.likedByMe) it.countLikes + 1 else it.countLikes - 1
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map { if (it.id == id) it.copy(countRepost = it.countRepost + 1) else it }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun saveById(post: Post) {
        posts = if (post.id == 0L) {
            listOf(post.copy(id = nextId++, author = "Mi")) + posts
        } else {
            posts.map { if (it.id != post.id) it else it.copy(content = post.content) }
        }
        data.value = posts
    }
}