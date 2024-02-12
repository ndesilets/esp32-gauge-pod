package com.example.esp32gauges.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarGauge(
    minVal: Float,
    maxVal: Float,
    currentVal: Float,
    detents: List<Float>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Column {
        Canvas(
            modifier = modifier
                .height(24.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RectangleShape
                )
                .padding(4.dp)
        )
        {
            val barHeight = size.height
            val barWidth = size.width

            val percentage = (currentVal - minVal) / (maxVal - minVal)

            // background
            drawRect(color = Color.Transparent, size = Size(barWidth, barHeight))

            // fill
            drawRect(color = Color.White, size = Size(barWidth * percentage, barHeight))
        }

        Canvas(
            modifier = modifier
                .height(24.dp)
                .fillMaxWidth()
        ) {
            val barWidth = size.width

            // detents

            val textStyle = TextStyle(color = Color.White, fontSize = 10.sp)

            val detentPctPositions = detents.map {
                (it - minVal) / (maxVal - minVal)
            }

            for ((i, detentPct) in detentPctPositions.withIndex()) {
                val detentX = (barWidth - 8.dp.toPx()) * detentPct + 4.dp.toPx()
                val detentHeight = 6.dp.toPx()

                drawLine(
                    color = Color.White,
                    start = Offset(detentX, 0f),
                    end = Offset(detentX, detentHeight),
                    strokeWidth = 2f
                )

                val textLayoutResult = textMeasurer.measure(detents[i].toInt().toString(), textStyle)

                val offset = when (detents[i]) {
                    minVal -> {
                        Offset(detentX, detentHeight + 2.dp.toPx())
                    }

                    maxVal -> {
                        Offset(detentX - textLayoutResult.size.width, detentHeight + 2.dp.toPx())
                    }

                    else -> {
                        Offset(detentX - textLayoutResult.size.width / 2, detentHeight + 2.dp.toPx())
                    }
                }

                drawText(textLayoutResult = textLayoutResult, topLeft = offset)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 480)
@Composable
fun BarGaugePreview() {
    BarGauge(
        minVal = -20f,
        maxVal = 300f,
        currentVal = 60f,
        detents = listOf(-20f, 0f, 32f, 100f,180f, 230f, 300f),
        modifier = Modifier
    )
}