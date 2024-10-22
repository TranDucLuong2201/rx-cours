package com.rxmobileteam.pet.presentation.vote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.rxmobileteam.pet.databinding.ItemVoteImageBinding
import com.rxmobileteam.pet.domain.model.Cat

class VotePetsAdapter : ListAdapter<Cat, VoteCardViewHolder>(VoteDiffCallBack) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteCardViewHolder =
    VoteCardViewHolder(
      binding = ItemVoteImageBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ),
    )

  override fun onBindViewHolder(holder: VoteCardViewHolder, position: Int) =
    holder.bind(getItem(position))
}
