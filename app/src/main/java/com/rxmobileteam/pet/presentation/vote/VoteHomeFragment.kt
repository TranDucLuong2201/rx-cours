package com.rxmobileteam.pet.presentation.vote

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rxmobileteam.pet.R
import com.rxmobileteam.pet.databinding.FragmentHomeVoteBinding
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.presentation.base.BaseFragment
import com.rxmobileteam.pet.presentation.base.LoadingDialogManager
import com.rxmobileteam.pet.presentation.detail.DetailArgs
import com.rxmobileteam.pet.presentation.ext.launchAndRepeatStarted
import com.rxmobileteam.pet.presentation.ext.safeNavigate
import com.rxmobileteam.pet.presentation.ext.visibleIf
import com.rxmobileteam.pet.presentation.vote.adapter.VotePetsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

@AndroidEntryPoint
class VoteHomeFragment : BaseFragment<FragmentHomeVoteBinding>(FragmentHomeVoteBinding::inflate) {

  private val voteViewModel by viewModels<VoteViewModel>()
  private val votePetsAdapter by lazy(NONE) {
    VotePetsAdapter()
  }

  @Inject
  lateinit var loadingDialogManager: LoadingDialogManager

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
    observeData()
    setupViewEvent()
  }

  private fun setupViewEvent() = binding.apply {
    btnClose.setOnClickListener {
      val position = binding.recyclerViewVote.getCurrentPosition()
      recyclerViewVote.resetPosition()
      voteViewModel.voteDown(votePetsAdapter.currentList[position])
    }

    btnNext.setOnClickListener {
      recyclerViewVote.nextView()
    }

    btnFavorite.setOnClickListener {
      val position = binding.recyclerViewVote.getCurrentPosition()
      voteViewModel.voteUp(votePetsAdapter.currentList[position])
      recyclerViewVote.nextView()
    }

    recyclerViewVote.setOnItemClickListener {
      recyclerViewVote.getChildAdapterPosition(it).let { position ->
        if (position != RecyclerView.NO_POSITION) {
          onClickItem(votePetsAdapter.currentList[position])
        }
      }
    }
  }

  private fun observeData() {
    launchAndRepeatStarted(
      { voteViewModel.voteStateFlow.collect(::handleVoteState) },
      { voteViewModel.singleEventFlow.collect(::handleSingleEvent) },
    )
  }

  private fun setupView() = binding.recyclerViewVote.apply {
    adapter = votePetsAdapter
    setHasFixedSize(true)
  }

  private fun handleSingleEvent(voteSingleEvent: VoteSingleEvent) {
    when (voteSingleEvent) {
      is VoteSingleEvent.VoteError -> {
        Toast.makeText(requireContext(), getString(R.string.vote_error), Toast.LENGTH_SHORT).show()
      }

      is VoteSingleEvent.GetListError -> {
        Toast.makeText(requireContext(), getString(R.string.title_error), Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun handleVoteState(voteUiState: VoteUiState) = when (voteUiState) {
    is VoteUiState.Loading -> {
      loadingDialogManager.showLoading(show = true)
    }

    is VoteUiState.Success -> {
      submitVoteSuccess(voteUiState = voteUiState)
      loadingDialogManager.showLoading(show = false)
    }

    is VoteUiState.Error -> {
      loadingDialogManager.showLoading(show = false)
      Toast.makeText(requireContext(), getString(R.string.title_error), Toast.LENGTH_SHORT).show()
    }
  }

  private fun submitVoteSuccess(voteUiState: VoteUiState.Success) {
    binding.btnClose.visibleIf(voteUiState.data.size != 1)
    binding.btnNext.visibleIf(voteUiState.data.size != 1)
    votePetsAdapter.submitList(voteUiState.data)
  }

  private fun onClickItem(cat: Cat) {
    findNavController().safeNavigate {
      navigate(
        directions = VoteHomeFragmentDirections.actionVoteHomeFragmentToDetailFragment(
          args = DetailArgs(
            id = cat.id,
          )
        )
      )
    }
  }
}
