package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityNewPostBinding.inflate

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(binding.root)

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        binding.addContent.setText(text)
        binding.addContent.requestFocus()
        binding.ok.setOnClickListener {
            val content = binding.addContent.text.toString()
            if (content.isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, content)
                }.let { intent ->
                    setResult(Activity.RESULT_OK, intent)
                }
            }
            finish()
        }
    }
}