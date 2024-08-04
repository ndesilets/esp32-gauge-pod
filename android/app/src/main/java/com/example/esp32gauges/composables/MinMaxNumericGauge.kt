package com.example.esp32gauges.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

@Composable
fun MinMaxNumericGauge(
    title: String,
    curVal: Float,
    minVal: Float,
    maxVal: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(title)
        Text(
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            text = String.format("%.2f", curVal)
        )
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 8.sp,
                text = "min"
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 8.sp,
                text = "max"
            )
        }
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                text = String.format("%.2f", minVal)
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                text = String.format("%.2f", maxVal)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 100, heightDp = 80)
@Composable
fun MinMaxNumericGaugePreview() {
    MinMaxNumericGauge(
        title = "My Gauge",
        curVal = 20f,
        minVal = -1f,
        maxVal = 1f,
        modifier = Modifier
    )
}