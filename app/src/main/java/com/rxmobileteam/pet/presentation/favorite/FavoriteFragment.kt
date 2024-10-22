package com.rxmobileteam.pet.presentation.favorite

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rxmobileteam.pet.R
import com.rxmobileteam.pet.databinding.FragmentFavoriteBinding
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.presentation.base.BaseFragment
import com.rxmobileteam.pet.presentation.detail.DetailArgs
import com.rxmobileteam.pet.presentation.ext.gone
import com.rxmobileteam.pet.presentation.ext.launchAndRepeatStarted
import com.rxmobileteam.pet.presentation.ext.safeNavigate
import com.rxmobileteam.pet.presentation.ext.visible
import com.rxmobileteam.pet.presentation.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint
import kotlin.LazyThreadSafetyMode.NONE

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate) {
  private val viewModel by viewModels<FavoriteViewModel>()

  private val favoriteCatAdapter by lazy(NONE) {
    FavoriteCatAdapter(
      onDeleteItem = { viewModel.voteDownCat(it) },
      onClickItem = ::onClickItem,
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupViews()
    bindVm()
  }

  private fun setupViews() {
    binding.recyclerView.run {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(context)
      adapter = favoriteCatAdapter
    }
  }

  private fun bindVm() {
    launchAndRepeatStarted(
      { viewModel.uiStateFlow.collect(::renderUiState) },
      { viewModel.singleEventFlow.collect(::handleEvent) },
    )

    binding.buttonRetry.setOnClickListener { viewModel.retry() }
  }

  private fun renderUiState(uiState: FavoriteUiState) = when (uiState) {
    is FavoriteUiState.Loading -> {
      binding.run {
        progressBar.visible()
        buttonRetry.gone()
        textViewEmpty.gone()
      }
      favoriteCatAdapter.submitList(emptyList())
    }

    is FavoriteUiState.Success -> {
      binding.run {
        progressBar.gone()
        buttonRetry.gone()
        textViewEmpty.visibleIf { uiState.cats.isEmpty() }
      }
      favoriteCatAdapter.submitList(uiState.cats)
    }

    is FavoriteUiState.Error -> {
      binding.run {
        progressBar.gone()
        buttonRetry.visible()
        textViewEmpty.gone()
      }
      favoriteCatAdapter.submitList(emptyList())
    }
  }

  private fun handleEvent(event: FavoriteSingleEvent) = when (event) {
    is FavoriteSingleEvent.VoteDownSuccess -> {
      Toast.makeText(requireContext(), "Removed successfully", Toast.LENGTH_SHORT).show()
    }

    is FavoriteSingleEvent.VoteDownError -> {
      Toast.makeText(requireContext(), R.string.title_error, Toast.LENGTH_SHORT).show()
    }
  }

  private fun onClickItem(cat: Cat) {
    findNavController().safeNavigate {
      navigate(
        directions = FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(
          args = DetailArgs(
            id = cat.id,
          )
        )
      )
    }
  }
}
