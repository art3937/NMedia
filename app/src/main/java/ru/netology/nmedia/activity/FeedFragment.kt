package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FragmentOpenPost.Companion.textArg
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.activity.NewPostFragment.Companion.textNewPost
import ru.netology.nmedia.adapter.OneInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post

class FeedFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = PostsAdapter(object : OneInteractionListener {

            override fun oneLike(post: Post) {
                viewModel.like(post.id,post.likedByMe)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
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
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textNewPost = post.content
                        text = post.video
                    })
//                newPostLauncher.launch(post.content)
            }

            override fun startActivity(url: String) {
                val openPage = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(openPage);
            }

            override fun startActivityPostRead(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_fragmentOpenPost,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }

            override fun load() {
                viewModel.loadPosts()
            }


        })//создаю адаптер

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner){data ->
            //   val newPost = adapter.currentList.size < data.posts.size
            adapter.submitList(data.posts)
//                if (newPost) {
//                    binding.list.scrollToPosition(0)//скролю вверх если новый пост
//                }
//            }
            binding.empty.isVisible = data.empty
        }

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progress.isVisible = state.loading
if (state.error) {
    Snackbar.make(binding.root, R.string.unknown_error, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.retry) {
            viewModel.loadPosts()
        }
        .show()
}
            binding.swipeRefreshLayout.isRefreshing = state.refreshing
        }

binding.swipeRefreshLayout.setOnRefreshListener {
    viewModel.refresh()
}

        viewModel.newerCount.observe(viewLifecycleOwner) {
            println(it)
            if (it > 0) {
                binding.baselineNorth.text = "К новым"
                binding.baselineNorth.isVisible = true
            }
        }

        binding.baselineNorth.setOnClickListener {
            binding.list.scrollToPosition(0)//скролю вверх если новый пост
            binding.baselineNorth.isVisible = false
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            //  newPostLauncher.launch("")
        }
        return binding.root
    }

}