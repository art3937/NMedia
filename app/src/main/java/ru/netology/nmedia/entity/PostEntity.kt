package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likes)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes)

    }
}

fun List<Post>.fromDtoToEntity() = map{PostEntity.fromDto(it)}
//val id: Long,
//val author: String ,
//val content: String,
//val published: Long,
//val likedByMe: Boolean = false,
//val likes: Int = 0,
//val countRepost: Int = 0,
//val countViews: Int = 0,
//val video: String = "",
//val authorAvatar: String? = "",
//var attachment: Attachment? = null,

