package com.example.treasurebox.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.treasurebox.data.`class`.TreasureBox
import com.example.treasurebox.ui.component.TreasureBoxCard
import com.example.treasurebox.ui.theme.Brown
import com.example.treasurebox.ui.theme.GaladaFontFamily
import com.example.treasurebox.viewModel.MainViewModel

@Composable
fun MainScreenRoute(
    viewModel: MainViewModel,
    onAddBoxClick: () -> Unit,
    onBoxClick: (TreasureBox) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Brown)
        }
    } else {
        MainScreen(
            boxes = uiState.boxes,
            onAddBoxClick = onAddBoxClick,
            onBoxClick = onBoxClick
        )
    }
}
@Composable
fun MainScreen(
    boxes: List<TreasureBox>,
    onAddBoxClick: () -> Unit,
    onBoxClick: (TreasureBox) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brown)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(48.dp))
            Text(
                text = "TreasureBox",
                fontFamily = GaladaFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddBoxClick, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            items(
                items = boxes,
                key = { it.id }
            ) { box ->
                TreasureBoxCard(box = box, onClick = { onBoxClick(box) })
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    val sampleBoxes = listOf(
        TreasureBox("1", "Japan Trip 2026", 12450.0, "1", listOf()),
        TreasureBox("2", "Summer Concert", 850.0, "1", listOf()),
        TreasureBox("3", "Cat Fund", 2100.0, "1", listOf())
    )

    MainScreen(
        boxes = sampleBoxes,
        onAddBoxClick = {},
        onBoxClick = {}
    )
}
