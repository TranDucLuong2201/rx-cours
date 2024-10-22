package com.rxmobileteam.pet.presentation.vote.adapter

import androidx.recyclerview.widget.DiffUtil
import com.rxmobileteam.pet.domain.model.Cat

object VoteDiffCallBack : DiffUtil.ItemCallback<Cat>() {
  override fun areItemsTheSame(
    oldItem: Cat,
    newItem: Cat,
  ) = oldItem.id == newItem.id

  override fun areContentsTheSame(
    oldItem: Cat,
    newItem: Cat,
  ) = oldItem == newItem
}
