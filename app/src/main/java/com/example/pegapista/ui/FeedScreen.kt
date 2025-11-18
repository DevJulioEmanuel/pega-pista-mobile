package com.example.pegapista.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pegapista.ui.theme.PegaPistaTheme

@Composable

fun FeedScreen(modifier: Modifier = Modifier.background(Color.White)) {
    Column (
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 10.dp)
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    PegaPistaTheme {
        FeedScreen()
    }
}