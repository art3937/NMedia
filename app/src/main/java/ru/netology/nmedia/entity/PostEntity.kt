package ru.netology.nmedia.entity


import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType


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
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() =
        Post(id, author, content, published, likedByMe, likes, authorAvatar = authorAvatar, attachment = attachment?.toDto())

    companion object {
        fun fromDto(post: Post) = PostEntity(
            id = post.id,
            author = post.author,
            authorAvatar = post.authorAvatar,
            content = post.content,
            published = post.published,
            likedByMe = post.likedByMe,
            likes = post.likes,
            attachment = AttachmentEmbeddable.fromDto(post.attachment)
        )
    }
}

    data class AttachmentEmbeddable(
        var url: String,
        var description: String?,
        var type: AttachmentType,
    ) {
        fun toDto() = Attachment(url, description, type)

        companion object {
            fun fromDto(dto: Attachment?) = dto?.let {
                AttachmentEmbeddable(it.url, it.description, it.type)
            }
        }
    }