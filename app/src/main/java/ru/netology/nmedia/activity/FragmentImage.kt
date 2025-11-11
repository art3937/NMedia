package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.imageLoad.load
import ru.netology.nmedia.util.StringArg

class FragmentImage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPhotoBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val res = arguments?.textImage
        val urlImages = "http://10.0.2.2:9999/media/$res"
        binding.photo.load(urlImages, false)
        return binding.root
    }

    companion object {
        var Bundle.textImage by StringArg
        var Bundle.textOpenPost by StringArg
    }
}
