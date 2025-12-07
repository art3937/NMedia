package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.viewModels.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textNewPost
import ru.netology.nmedia.adapter.OneInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg

@AndroidEntryPoint
class FragmentOpenPost : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: PostViewModel by viewModels()
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val oneInteractionListener = object : OneInteractionListener {
            override fun oneLike(post: Post) {
                viewModel.like(post.id, post.likedByMe)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onShare(post: Post) {
                viewModel.sharePost(post)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.description_post_repost))
                startActivity(shareIntent)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_fragmentOpenPost_to_newPostFragment,
                        Bundle().apply {
                            textNewPost = post.content
                        })
//                newPostLauncher.launch(post.content)
            }

            override fun startActivity(url: String?) {
                val openPage = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(openPage);
            }

            override fun startActivityPostRead(post: Post) {
            }

            override fun load() {
                viewModel.loadPosts()
            }

        }
//        val holder = PostViewHolder(binding.post, oneInteractionListener)
//        val res = arguments?.textArg?.toLong()
//        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
//            val post = feedModel.posts.find { it.id == res } ?: return@observe
//            holder.bind(post)
//        }


        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
        var Bundle.textOpenPost by StringArg
    }
}
