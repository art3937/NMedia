package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val likesCount = NumberFormatting()
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate((layoutInflater))
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                countLikes.text = likesCount.formatting(post.countLikes)
                countReposts.text = likesCount.formatting(post.countRepost)

                likes.setImageResource(R.drawable.baseline_favorite_border_24)
                likes.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.baseline_favorite_24
                    } else {
                        R.drawable.baseline_favorite_border_24
                    }
                )
            }

            binding.likes.setOnClickListener {
                viewModel.like()
            }

            binding.repostButton.setOnClickListener {
                viewModel.share()
            }
        }
    }
}