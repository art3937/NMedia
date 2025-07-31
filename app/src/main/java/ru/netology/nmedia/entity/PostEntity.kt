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
    val authorAvatar: String? = " ",
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val countRepost: Int = 0,
    var showPost: Boolean = falsei
) {
    fun toDto() = Post(id, author,authorAvatar, content, published, likedByMe, likes,countRepost, showPost = showPost)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes,dto.countRepost,dto.showPost)

    }
}

fun List<Post>.fromDtoToEntity() = map{PostEntity.fromDto(it)}


