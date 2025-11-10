package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.viewModels.SignInViewModel
import ru.netology.nmedia.databinding.FragmentSignInBinding
import java.lang.Thread.sleep

@AndroidEntryPoint
class FragmentSignIn : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        val viewModel: SignInViewModel by viewModels()

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
//                if(IsCoroutineRunning(MyCoroutine())) {
//                    findNavController().navigateUp()
//                }
            }

            if (viewModel.state.value?.error == true) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка ${viewModel.state.value?.errorServer}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }else {
                findNavController().navigateUp()
            }

        }
        return binding.root
    }
}
