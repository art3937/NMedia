package ru.netology.nmedia.entity


import ru.netology.nmedia.dto.Post


data class PostEntity(
    val id: Long,
    val author: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val countRepost: Int = 0,
    val countViews: Int = 0,
    val video: String = "",
) {
    fun toDto() =
        Post(id, author, content, published, likedByMe, likes)

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.author,
            post.content,
            post.published,
            post.likedByMe,
            post.likes
        )
    }
}

