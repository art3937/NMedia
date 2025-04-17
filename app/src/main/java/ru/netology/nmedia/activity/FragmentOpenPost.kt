package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Post
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OneInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.util.StringArg

class FragmentOpenPost : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        val binding2 = CardPostBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val adapter = PostsAdapter(object : OneInteractionListener {
            override fun oneLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.sharePost(post.id)
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
                        textArg = post.content
                    })
//                newPostLauncher.launch(post.content)
            }

            override fun startActivity(url: String) {
                val openPage = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(openPage);
            }

            override fun startActivityPostRead(post: Post) {
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val res = arguments?.textArg?.toLong()
            adapter.submitList(posts.filter { it.id == res })
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }
}
