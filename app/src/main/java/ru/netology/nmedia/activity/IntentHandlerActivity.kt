package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityIntentHandlerActivityBinding

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (text.isNullOrBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_content,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    finish()
                }.show()
            }
        }
    }
}