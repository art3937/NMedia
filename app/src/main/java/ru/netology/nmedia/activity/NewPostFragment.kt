package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.viewModels.PostViewModel
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.imageLoad.load
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg


private const val MAX_SIZE = 2040

@AndroidEntryPoint
class NewPostFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val viewModel: PostViewModel by viewModels()
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.removePhoto()

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(requireContext(), "Image picker error", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                }

                val uri = it.data?.data ?: return@registerForActivityResult
                viewModel.savePhoto(uri, uri.toFile())

            }

        binding.addContent.requestFocus()
        arguments?.textNewPost?.let(binding.addContent::setText)
        // arguments?.text?.let(binding.textUrl::setText)

        viewModel.postCreated.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
            viewModel.loadPosts()
        }



        binding.ok.setOnClickListener {
            findNavController().navigateUp()
            val content = binding.addContent.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContentAndSave(content)
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "ничего не заполнено", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .maxResultSize(MAX_SIZE, MAX_SIZE)
                .cameraOnly()
                .createIntent(imagePickerLauncher::launch)
        }

        binding.openGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .maxResultSize(MAX_SIZE, MAX_SIZE)
                .galleryOnly()
                .createIntent(imagePickerLauncher::launch)
        }

        binding.remove.setOnClickListener {
            viewModel.removePhoto()
        }
//для редактирования подгружаю фото
        val url = arguments?.textUrl
        val urlImages = "http://10.0.2.2:9999/media/${url}"

        if (url != null) {
            binding.photo.load(urlImages, false)
        }
/////////////////////////
        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo == null && url == null) {
                binding.photoContainer.isGone = true
                return@observe
            }

            binding.photoContainer.isVisible = true
            if (photo != null) {
                binding.photo.setImageURI(photo.uri)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.cancel()
                    findNavController().navigateUp()
                }

            })
        return binding.root
    }


    companion object {
        var Bundle.textNewPost by StringArg
        var Bundle.textUrl by StringArg
    }
}
