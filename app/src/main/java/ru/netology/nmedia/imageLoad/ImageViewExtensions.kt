package ru.netology.nmedia.imageLoad

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R


fun ImageView.load(url: String,transform:Boolean){
    if(transform){
        Glide.with(this)
            .load(url)
            .circleCrop()
            .error(R.drawable.ic_error_100dp)
            .placeholder(R.drawable.ic_loading_100dp)
            .timeout(10000)
            .into(this)
    }else {
        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_error_100dp)
            .placeholder(R.drawable.ic_loading_100dp)
            .timeout(10000)
            .into(this)
    }
}