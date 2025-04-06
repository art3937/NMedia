package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.NumberFormatting
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.CardPostBinding

interface OneInteractionListener {
    fun oneLike(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
}


class PostsAdapter(
    private val oneInteractionListener: OneInteractionListener,
    private val bindingActivity: ActivityMainBinding
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, bindingActivity, oneInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val bindingActivity: ActivityMainBinding,
    private val oneInteractionListener: OneInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private val numberFormatting = NumberFormatting()

    @RequiresApi(Build.VERSION_CODES.Q)
    fun bind(post: Post) = with(binding) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likes.text = numberFormatting.formatting(post.countLikes)
            repostButton.text = numberFormatting.formatting(post.countRepost)
            countView.text = numberFormatting.formatting(post.countViews)
            likes.isChecked = post.likedByMe
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
                            bindingActivity.apply {
                                group.visibility = View.VISIBLE
                                authorName.text = author.text
                            }
                            oneInteractionListener.onEdit(post)
                            true
                        }

                        else -> false
                    }
                }
            }.show()
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