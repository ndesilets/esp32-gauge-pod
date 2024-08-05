package com.example.esp32gauges.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme

@Composable
fun SessionManager(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text("lol")
    }
}

@Preview(widthDp = 480, heightDp = 1056, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SessionManagerPreview() {
    ESP32GaugesTheme {
        SessionManager()
    }
}