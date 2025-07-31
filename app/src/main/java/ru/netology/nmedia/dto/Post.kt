package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String? = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val countRepost: Int = 0,
    val countViews: Int = 0,
    val video: String = "",
    var attachment: Attachment? = null,
    var showPost: Boolean = false
    )

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)

