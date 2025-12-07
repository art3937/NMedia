package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.viewModels.AuthViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textNewPost
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationsPermission()
        val binding = ActivityAppBinding.inflate(layoutInflater)


        val authViewModel: AuthViewModel by viewModels()

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)

                    authViewModel.isAuthorized.observe(this@AppActivity) { authorized ->
                        menu.setGroupVisible(R.id.unauthorized, !authorized)
                        menu.setGroupVisible(R.id.authorized, authorized)
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signin -> {
                            findNavController(binding.root.id).navigate(
                                R.id.action_feedFragment_to_fragmentSignIn
                            )
                            true
                        }

                        R.id.signup -> {
                            findNavController(binding.root.id).navigate(
                                R.id.action_feedFragment_to_fragmentSignUp
                            )
                            true
                        }

                        R.id.logout -> {
                            appAuth.removeAuth()
                            true
                        }

                        else -> false
                    }
            }
        )


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
            findNavController(R.id.nav_controller).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textNewPost = text
                }
            )
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestPermissions(arrayOf(permission), 1)
    }
}