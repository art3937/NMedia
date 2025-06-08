package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg


class NewPostFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        // binding.addContent.setText(text)
        binding.addContent.requestFocus()
        arguments?.textArg?.let(binding.addContent::setText)
        arguments?.text?.let(binding.textUrl::setText)
 viewModel.postCreated.observe(viewLifecycleOwner){
     AndroidUtils.hideKeyboard(requireView())
viewModel.load()
     findNavController().navigateUp()
 }
        binding.ok.setOnClickListener {
            val content = binding.addContent.text.toString()
            val url = binding.textUrl.text.toString()
            if (content.isNotBlank() || url.isNotBlank()) {
                viewModel.changeContentAndSave(content, url)
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
        var Bundle.textArg by StringArg
        var Bundle.text by StringArg
    }
}
