package ru.netology.nmedia

class Likes {
    fun like(countLike: Int): String {

        return when {
            countLike >= 1000_000 -> "${countLike / 1000_000}. ${countLike % 1000_000 / 100_000} M"
            countLike >= 10_000 -> "${countLike / 1000} K"
            countLike >= 1000 -> "${countLike / 1000}. ${countLike / 100 % 10} K"
            else -> countLike.toString()
        }
    }
}