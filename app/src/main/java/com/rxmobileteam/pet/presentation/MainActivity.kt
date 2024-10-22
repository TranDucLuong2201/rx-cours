package com.rxmobileteam.pet.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rxmobileteam.pet.data.utils.NetworkMonitor
import com.rxmobileteam.pet.databinding.ActivityMainBinding
import com.rxmobileteam.pet.presentation.ext.launchAndRepeatStarted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val binding by lazy(NONE) { ActivityMainBinding.inflate(layoutInflater) }

  private lateinit var navController: NavController

  @Inject
  internal lateinit var networkMonitor: NetworkMonitor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    setupNavigation()

    launchAndRepeatStarted(
      { networkMonitor.isOnline.collect(::handleOnlineStatus) },
    )
  }

  private fun setupNavigation() {
    val navHostFragment = binding.navHostContainer.getFragment<NavHostFragment>()
    navController = navHostFragment.navController

    binding.bottomNavView.setupWithNavController(navController)
  }

  override fun onNavigateUp(): Boolean = navController.navigateUp()

  private fun handleOnlineStatus(isOnline: Boolean) {
    if (!isOnline) {
      Toast.makeText(this, "Network is disconnected...", Toast.LENGTH_SHORT).show()
    }
  }
}
