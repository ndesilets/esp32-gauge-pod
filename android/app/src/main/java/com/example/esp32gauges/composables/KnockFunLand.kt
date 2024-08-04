package com.example.esp32gauges.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

@Composable
fun KnockFunLand(
    damCurVal: Float,
    damMinVal: Float,
    damMaxVal: Float,
    afCorrectionCurVal: Float,
    afCorrectionMinVal: Float,
    afCorrectionMaxVal: Float,
    afLearnCurVal: Float,
    afLearnMinVal: Float,
    afLearnMaxVal: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Knock Fun Land")
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            MinMaxNumericGauge(
                title = "DAM",
                curVal = damCurVal,
                minVal = damMinVal,
                maxVal = damMaxVal,
                modifier = modifier.weight(1f)
            )
            MinMaxNumericGauge(
                title = "Feedback",
                curVal = afCorrectionCurVal,
                minVal = afCorrectionMinVal,
                maxVal = afCorrectionMaxVal,
                modifier = modifier.weight(1f)
            )
            MinMaxNumericGauge(
                title = "Learned",
                curVal = afLearnCurVal,
                minVal = afLearnMinVal,
                maxVal = afLearnMaxVal,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 400, heightDp = 120)
@Composable
fun KnockFunLandPreview() {
    KnockFunLand(
        damCurVal = 0.0f,
        damMinVal = 0.0f,
        damMaxVal = 0.0f,
        afLearnCurVal = 0.0f,
        afLearnMinVal = 0.0f,
        afLearnMaxVal = 0.0f,
        afCorrectionCurVal = 0.0f,
        afCorrectionMinVal = 0.0f,
        afCorrectionMaxVal = 0.0f,
        modifier = Modifier
    )
}