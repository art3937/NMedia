package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val countRepost: Int = 0,
    @Embedded
    val attachment:AttachmentEmbeddable?,
) {
    fun toDto() = Post(id, author,authorAvatar, content, published, likedByMe, likes,countRepost,attachment = attachment?.toDto())

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes,dto.countRepost,AttachmentEmbeddable.fromDto(dto.attachment))

    }
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<Post>.fromDtoToEntity() = map{PostEntity.fromDto(it)}
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)


