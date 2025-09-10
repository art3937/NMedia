package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val countRepost: Int = 0,
    val countViews: Int = 0,
    val attachment: Attachment? = null,
    val ownerByMe: Boolean = false
    )

data class Attachment(
    val url: String ,
    val type: AttachmentType,
)

