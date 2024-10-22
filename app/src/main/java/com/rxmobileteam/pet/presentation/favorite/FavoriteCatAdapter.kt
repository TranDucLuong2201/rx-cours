package com.rxmobileteam.pet.presentation.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rxmobileteam.pet.databinding.ItemFavoriteCatLayoutBinding
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.presentation.ext.loadImageFromUrlWithLoading

private object CatDiffUtilItemCallback : DiffUtil.ItemCallback<Cat>() {
  override fun areItemsTheSame(oldItem: Cat, newItem: Cat) = oldItem.id == newItem.id
  override fun areContentsTheSame(oldItem: Cat, newItem: Cat) = oldItem == newItem
}

class FavoriteCatAdapter(
  private val onDeleteItem: (Cat) -> Unit,
  private val onClickItem: (Cat) -> Unit,
) : ListAdapter<Cat, FavoriteCatAdapter.VH>(CatDiffUtilItemCallback) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
    ItemFavoriteCatLayoutBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

  inner class VH(
    private val binding: ItemFavoriteCatLayoutBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    init {
      binding.imageDelete.setOnClickListener {
        bindingAdapterPosition.let { position ->
          if (position != RecyclerView.NO_POSITION) {
            onDeleteItem(getItem(position))
          }
        }
      }
      binding.root.setOnClickListener {
        bindingAdapterPosition.let { position ->
          if (position != RecyclerView.NO_POSITION) {
            onClickItem(getItem(position))
          }
        }
      }
    }

    fun bind(item: Cat) = binding.run {
      textView.text = item.id
      imageView.loadImageFromUrlWithLoading(item.url)
    }
  }
}
