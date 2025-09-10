package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewModelScope
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.SignInViewModel
import ru.netology.nmedia.activity.NewPostFragment.Companion.textNewPost
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.imageLoad.load
import ru.netology.nmedia.util.StringArg

class FragmentSignIn : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        val viewModel: SignInViewModel by viewModels(ownerProducer = ::requireParentFragment)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.go.setOnClickListener {
            val login = binding.loginIn.text.toString()
            val password = binding.password.text.toString()

            if (login.isNotBlank() && password.isNotBlank()) {
                viewModel.signIn(login, password)
            }
            if (viewModel.state.value?.error == true) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка ${viewModel.state.value?.errorServer}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }
}
