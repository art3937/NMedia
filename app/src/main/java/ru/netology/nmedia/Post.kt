package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val video: String = "",
    val countLikes: Int = 0,
    val countRepost: Int = 0,
    val countViews: Int = 0
)

