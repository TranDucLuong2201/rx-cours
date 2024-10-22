package com.rxmobileteam.pet.presentation.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rxmobileteam.pet.databinding.FragmentDetailBinding
import com.rxmobileteam.pet.presentation.base.BaseFragment
import com.rxmobileteam.pet.presentation.ext.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
  private val args by lazy(LazyThreadSafetyMode.NONE) { navArgs<DetailFragmentArgs>().value.args }
  private val viewModel by viewModels<DetailViewModel>()

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)

    // Demo using args
    binding.text.text = args.toString()
    viewModel.args

    binding.materialToolbar.setNavigationOnClickListener {
      findNavController().safeNavigate { popBackStack() }
    }
  }
}
