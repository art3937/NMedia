package ru.netology.nmedia.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val video: String = "",
    val countLikes: Int = 0,
    val countRepost: Int = 0,
    val countViews: Int = 0
) {
    fun toDto() =
        Post(id, author, content, published, likedByMe, video, countLikes, countRepost, countViews)

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.author,
            post.content,
            post.published,
            post.likedByMe,
            post.video,
            post.countLikes,
            post.countRepost,
            post.countViews
        )
    }
}

