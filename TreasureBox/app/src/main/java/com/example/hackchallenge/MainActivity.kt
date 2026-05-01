package com.example.hackchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hackchallenge.data.TreasureBoxRepository
import com.example.hackchallenge.data.network.Network
import com.example.hackchallenge.ui.screens.TreasureBoxNavHost
import com.example.hackchallenge.ui.theme.HackChallengeTheme
import com.example.hackchallenge.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    // Single repo instance for the activity's lifetime. Replace with DI later (Hilt/Koin).
    private val repository by lazy { TreasureBoxRepository(api = Network.api) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackChallengeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val mainViewModel: MainViewModel = viewModel(
                        factory = MainViewModel.Factory(repository)
                    )
                    TreasureBoxNavHost(viewModel = mainViewModel)
                }
            }
        }
    }
}
