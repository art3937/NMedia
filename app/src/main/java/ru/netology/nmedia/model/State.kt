package ru.netology.nmedia.model

data class State(

    val errorServer: String = "",
    val error: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false

)