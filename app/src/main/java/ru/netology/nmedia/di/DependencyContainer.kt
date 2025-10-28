//package ru.netology.nmedia.di
//
//import android.content.Context
//import androidx.room.Room
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.BuildConfig.BASE_URL
//import ru.netology.nmedia.api.ApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//import java.util.concurrent.TimeUnit
//
//class DependencyContainer(
//    private val context: Context
//) {
//
//    companion object {
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context) {
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance(): DependencyContainer {
//            return instance!!
//        }
//    }
//
//    private val logging = HttpLoggingInterceptor().apply {
//        if (BuildConfig.DEBUG) {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
//
//
//    val appAuth = AppAuth(context)
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .addInterceptor { chain ->
//            val request = appAuth.state.value?.token?.let { token ->
//                chain.request().newBuilder()
//                    .header("Authorization", token)
//                    .build()
//            } ?: chain.request()
//
//            chain.proceed(request)
//        }
//        .addInterceptor(logging)
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .client(client)
//        .addConverterFactory(GsonConverterFactory.create())
//        .baseUrl(BASE_URL)
//        .build()
//
//    private val appBd = Room.databaseBuilder(context, AppDb::class.java, "app.db")
//        .fallbackToDestructiveMigration()
//        .build()
//
//    private val postDao = appBd.postDao()
//
//    val apiService = retrofit.create<ApiService>()
//
//    val repository: PostRepository = PostRepositoryImpl(postDao, apiService)
//
//}