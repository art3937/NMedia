package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private var posts = listOf(
        Post(
        id = 1,
        author = "Нетология. Университет интернет-проффесий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likedByMe = false,
        countLikes = 999,
        countRepost = 999
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false,
            countLikes = 999,
            countRepost = 999
        ), Post(
            id = 3,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false,
            countLikes = 999,
            countRepost = 999
        ),
        Post(
            id = 4,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false,
            countLikes = 999,
            countRepost = 999
        ),
        Post(
            id = 5,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет Это новая нетология",
            published = "29 мая в 15:36",
            likedByMe = false,
            countLikes = 999,
            countRepost = 999
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
}