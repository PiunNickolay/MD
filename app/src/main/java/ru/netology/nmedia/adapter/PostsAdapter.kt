package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Counts
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import java.util.Objects

interface OnInteractionListener {
    fun onLikeListener(post: Post)
    fun onShareListener(post: Post)
    fun onRemoveListener(post: Post)
    fun onEditListener(post: Post)
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = with(binding) {
        author.text = post.author
        published.text = post.published
        content.text = post.content
        like.apply {
            isChecked = post.likedByMe
            text = post.likeCount.toString()
        }
        share.text = post.shareCount.toString()
        viewCount.text = Counts.countFormat(post.viewCount)

        like.setOnClickListener {
            onInteractionListener.onLikeListener(post)
        }
        share.setOnClickListener {
            onInteractionListener.onShareListener(post)
        }
        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_actions)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onInteractionListener.onRemoveListener(post)
                            true
                        }

                        R.id.edit -> {
                            onInteractionListener.onEditListener(post)
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

}