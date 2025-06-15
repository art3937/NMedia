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
    var authorAvatar: String? = null,
) {
    fun toDto() =
        Post(id, author, content, published, likedByMe, likes, authorAvatar = authorAvatar)

    companion object {
        fun fromDto(post: Post) = PostEntity(
            id = post.id,
            author = post.author,
            authorAvatar = post.authorAvatar,
            content = post.content,
            published = post.published,
            likedByMe = post.likedByMe,
            likes = post.likes,
            )
    }
}

