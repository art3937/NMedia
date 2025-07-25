package ru.netology.nmedia.adapter

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.NumberFormatting
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.imageLoad.load
import ru.netology.nmedia.util.StringArg
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext


interface OneInteractionListener {
    fun oneLike(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun startActivity(url: String)
    fun startActivityPostRead(post: Post)
    fun load()
}


class PostsAdapter(
    private val oneInteractionListener: OneInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, oneInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val oneInteractionListener: OneInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private val numberFormatting = NumberFormatting()


    fun bind(post: Post) = with(binding) {
        binding.content.maxLines = 4
        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            likes.text = numberFormatting.formatting(post.likes)
            repostButton.text = numberFormatting.formatting(post.countRepost)
            countView.text = numberFormatting.formatting(post.countViews)
            likes.isChecked = post.likedByMe
            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            val urlImages = "http://10.0.2.2:9999/images/${post.attachment?.url}"
            video.load(urlImages,false)
            avatar.load(url,true)
        }

        likes.setOnClickListener {
            oneInteractionListener.oneLike(post)
        }
        repostButton.setOnClickListener {
            oneInteractionListener.onShare(post)
        }

        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_actions)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            oneInteractionListener.onRemove(post)
                            true
                        }

                        R.id.edit -> {
                            oneInteractionListener.onEdit(post)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
            }.show()
        }


        binding.video.setOnClickListener {
            if (post.video.isBlank()) {
                oneInteractionListener.onEdit(post)
            } else {
                oneInteractionListener.startActivity(post.video)
            }
        }
        binding.play.setOnClickListener {
            if (post.video.isBlank()) {
                oneInteractionListener.onEdit(post)
            } else {
                oneInteractionListener.startActivity(post.video)
            }
        }

        binding.cardPostGroup.setOnClickListener {

                binding.cardPostGroup.isClickable = false

            oneInteractionListener.startActivityPostRead(post)
        }

        binding.content.setOnClickListener() {
            binding.content.maxLines = Int.MAX_VALUE
        }

    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}