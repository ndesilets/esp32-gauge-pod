package com.example.esp32gauges.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryNumericGauge(
    title: String,
    curVal: Float,
    oneMinuteMin: Float,
    oneMinuteMax: Float,
    fifteenMinuteMin: Float,
    fifteenMinuteMax: Float,
    sessionMin: Float,
    sessionMax: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(120.dp),
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
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = ""
            )
            Text(
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "min"
            )
            Text(
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "max"
            )
        }
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "1m"
            )
            Text(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "$oneMinuteMin"
            )
            Text(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "$oneMinuteMax"
            )
        }
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "15m"
            )
            Text(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "$fifteenMinuteMin"
            )
            Text(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "$fifteenMinuteMax"
            )
        }
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "Session"
            )
            Text(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "$sessionMin"
            )
            Text(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                text = "$sessionMax"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 100, heightDp = 100)
@Composable
fun HistoryNumericGaugePreview() {
    HistoryNumericGauge(
        title = "My Gauge",
        curVal = 20f,
        oneMinuteMin = -1f,
        oneMinuteMax = 1f,
        fifteenMinuteMin = -15f,
        fifteenMinuteMax = 15f,
        sessionMin = -60f,
        sessionMax = 60f,
        modifier = Modifier
    )
}