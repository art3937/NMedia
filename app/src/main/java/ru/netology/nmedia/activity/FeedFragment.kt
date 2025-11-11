package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.viewModels.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FragmentImage.Companion.textImage
import ru.netology.nmedia.activity.FragmentOpenPost.Companion.textArg
import ru.netology.nmedia.activity.NewPostFragment.Companion.textNewPost
import ru.netology.nmedia.activity.NewPostFragment.Companion.textUrl
import ru.netology.nmedia.adapter.OneInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post

@AndroidEntryPoint
class FeedFragment() : Fragment() {
    val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = PostsAdapter(object : OneInteractionListener {

            override fun oneLike(post: Post) {
                viewModel.like(post.id, post.likedByMe)
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
                        textUrl = post.attachment?.url
                    })
            }

            override fun startActivity(url: String?) {
                //  val openPage = Intent(Intent.CATEGORY_APP_GALLERY,url)
                //   startActivity(openPage);
                findNavController().navigate(
                    R.id.action_feedFragment_to_fragmentImage, Bundle().apply {
                        textImage = url
                    }
                )
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

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { adapter.submitData(it) }

        }
//        viewModel.data.observe(viewLifecycleOwner) { data ->
//            val newPost = adapter.currentList.size < data.posts.size
//            adapter.submitList(data.posts) {
//                if (newPost) {
//                    binding.list.scrollToPosition(0)//скролю вверх если новый пост
//                }
//            }
//            binding.empty.isVisible = data.empty
//        }

//        viewModel.state.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            if (state.error) {
//                Snackbar.make(binding.root, R.string.unknown_error, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.retry) {
//                        viewModel.loadPosts()
//                    }
//                    .show()
//            }
//            binding.swipeRefreshLayout.isRefreshing = state.refreshing
        //}
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // viewModel.refresh()
            adapter.refresh()
            binding.baselineNorth.isVisible = false
        }

//        viewModel.newerCount.observe(viewLifecycleOwner) {
//            println(it)
//            if (it > 0) {
//                binding.baselineNorth.text = "К  ${it}"
//                binding.baselineNorth.isVisible = true
//            }
//        }

        binding.baselineNorth.setOnClickListener {
            viewModel.loadDaoNewPost()
            binding.baselineNorth.isVisible = false
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}