package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate((layoutInflater))
        setContentView(binding.root)
        val likesCount = NumberFormatting()
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-проффесий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            countLikes.text = likesCount.formatting(post.countLikes)
            countReposts.text = likesCount.formatting(post.countRepost)
            if (post.likedByMe) {
                likes.setImageResource(R.drawable.baseline_favorite_24)
            }
            likes.setOnClickListener {
                post.likedByMe = !post.likedByMe
                post.countLikes = if (post.likedByMe) post.countLikes + 1 else post.countLikes - 1
                countLikes.text = likesCount.formatting(post.countLikes)
                likes.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.baseline_favorite_24

                    } else {
                        R.drawable.baseline_favorite_border_24
                    }
                )
            }

            repostButton.setOnClickListener {
                post.countRepost++
                countReposts.text = likesCount.formatting(post.countRepost)
            }
        }

    }
}