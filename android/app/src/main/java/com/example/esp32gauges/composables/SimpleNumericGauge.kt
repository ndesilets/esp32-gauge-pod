package com.example.esp32gauges.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun SimpleNumericGauge(
    title: String,
    curVal: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(title)
        Text(
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            text = "${curVal.toInt()}"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 100, heightDp = 60)
@Composable
fun SimpleNumericGaugePreview() {
    SimpleNumericGauge(
        title = "My Gauge",
        curVal = 20f,
        modifier = Modifier
    )
}