package com.example.hackchallenge.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hackchallenge.viewmodel.MainViewModel

@Composable
fun TreasureBoxNavHost(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = MainRoute
    ) {
        composable<MainRoute> {
            MainScreenRoute(
                viewModel = viewModel,
                onAddBoxClick = { navController.navigate(AddBoxRoute) },
                onBoxClick = { box ->
                    navController.navigate(BoxDetailRoute(boxId = box.id))
                }
            )
        }

        composable<AddBoxRoute> {
            AddBoxScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable<BoxDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<BoxDetailRoute>()
            val box = uiState.boxes.find { it.id == args.boxId }

            if (box != null) {
                BoxDetailScreen(
                    box = box,
                    onBack = { navController.popBackStack() },
                    onDeposit = { navController.navigate(DepositRoute(box.id)) },
                    onWithdraw = { navController.navigate(WithdrawRoute(box.id)) },
                    onPostComment = { txId, text ->
                        viewModel.postComment(box.id, txId, text)
                    }
                )
            }
        }

        composable<DepositRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<DepositRoute>()
            val box = uiState.boxes.find { it.id == args.boxId }

            DepositScreen(
                boxName = box?.name ?: "",
                onBack = { navController.popBackStack() },
                onConfirm = { title, amount ->
                    viewModel.deposit(args.boxId, title, amount)
                    navController.popBackStack()
                }
            )
        }

        composable<WithdrawRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<WithdrawRoute>()
            val box = uiState.boxes.find { it.id == args.boxId }

            WithdrawScreen(
                boxName = box?.name ?: "",
                boxBalance = box?.currentBalance ?: 0.0,
                onBack = { navController.popBackStack() },
                onConfirm = { title, amount, note ->
                    viewModel.withdraw(args.boxId, title, amount, note)
                    navController.popBackStack()
                }
            )
        }
    }
}
