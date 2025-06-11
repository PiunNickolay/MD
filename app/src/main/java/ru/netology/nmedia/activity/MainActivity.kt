package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.learningandtrying.util.AndroidUtils
import ru.netology.learningandtrying.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.Counts
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLikeListener(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShareListener(post: Post) {
                viewModel.shareById(post.id)
            }

            override fun onRemoveListener(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEditListener(post: Post) {
                viewModel.edit(post)
            }

        })
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.list.scrollToPosition(0)
                }
            }
        }
        viewModel.edited.observe(this) {
            if (it.id != 0L) {
                binding.content.setText(it.content)
                binding.content.focusAndShowKeyboard()
                binding.editGroup.visibility = View.VISIBLE
                binding.editPostContent.text = it.content
            } else {
                binding.editGroup.visibility = View.GONE
                binding.editPostContent.text = ""
            }
        }
        binding.editCancel.setOnClickListener {
            viewModel.cancelEdit()
            binding.content.setText("")
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(it)
        }
        binding.add.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNullOrBlank()) {
                Toast.makeText(this, R.string.error_empty_content, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.changeAndSave(text)
            binding.content.setText("")
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(it)
        }
    }
}