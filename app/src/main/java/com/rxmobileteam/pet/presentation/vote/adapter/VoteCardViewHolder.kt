package com.rxmobileteam.pet.presentation.vote.adapter

import androidx.recyclerview.widget.RecyclerView
import com.rxmobileteam.pet.databinding.ItemVoteImageBinding
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.presentation.ext.loadImageFromUrlWithLoading

class VoteCardViewHolder(
  private val binding: ItemVoteImageBinding,
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(vote: Cat) {
    binding.imgThumb.loadImageFromUrlWithLoading(vote.url)
  }
}
