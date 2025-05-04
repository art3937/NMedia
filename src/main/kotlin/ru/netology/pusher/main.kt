package ru.netology.pusher

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import java.io.FileInputStream


fun main() {
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(FileInputStream("fcm.json")))
        .build()

    FirebaseApp.initializeApp(options)

    val message = Message.builder()
        .putData("action", "LIKE")
        .putData("content", """{
          "userId": 1,
          "userName": "Vasiliy",
          "postId": 2,
          "postAuthor": "Netology"
        }""".trimIndent())
        .setToken(token)
        .build()

    val messageShared = Message.builder()
        .putData("action", "SHARED")
        .putData("content", """{
          "userId": 1,
          "userName": "Vasiliy",
          "postId": 2,
          "postAuthor": "Netology"
        }""".trimIndent())
        .setToken(token)
        .build()

    val messageShare = Message.builder()
        .putData("action", "SHARE")
        .putData("content", """{
          "userId": 1,
          "userName": "Vasiliy",
          "postId": 2,
          "postAuthor": "Netology"
        }""".trimIndent())
        .setToken(token)
        .build()

    val messageAddedPost = Message.builder()
        .putData("action", "ADDED")
        .putData("content", """{
          "userId": 1,
          "userName": "Artemyi",
          "postId": 2,
          "postAuthor": "Artemii",
          "contentText": "Практика от партнёров вдобавок к привычным заданиям
           — бизнес-игры, митапы, хакатоны, pet-проекты.
            Вы получите ценный опыт и сможете выгодно выделяться на рынке."
        }""".trimIndent())
        .setToken(token)
        .build()

   // FirebaseMessaging.getInstance().send(messageShare)
    FirebaseMessaging.getInstance().send(messageAddedPost)
}
