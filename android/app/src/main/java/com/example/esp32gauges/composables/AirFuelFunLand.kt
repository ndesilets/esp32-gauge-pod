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
fun AirFuelFunLand(
    afCorrectionCurVal: Float,
    afCorrectionMinVal: Float,
    afCorrectionMaxVal: Float,
    afLearnCurVal: Float,
    afLearnMinVal: Float,
    afLearnMaxVal: Float,
    afRatioCurVal: Float,
    afRatioMinVal: Float,
    afRatioMaxVal: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Air Fuel Fun Land")
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            MinMaxNumericGauge(
                title = "S. Trim",
                curVal = afCorrectionCurVal,
                minVal = afCorrectionMinVal,
                maxVal = afCorrectionMaxVal,
                modifier = modifier.weight(1f)
            )
            MinMaxNumericGauge(
                title = "L. Trim",
                curVal = afLearnCurVal,
                minVal = afLearnMinVal,
                maxVal = afLearnMaxVal,
                modifier = modifier.weight(1f)
            )
            SimpleNumericGauge(
                title = "Trim Sum",
                curVal = afCorrectionCurVal + afLearnCurVal,
                modifier = modifier
                    .weight(1f)
                    .align(Alignment.Top)
            )
            MinMaxNumericGauge(
                title = "AFR",
                curVal = afRatioCurVal,
                minVal = afRatioMinVal,
                maxVal = afRatioMaxVal,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 400, heightDp = 120)
@Composable
fun AirFuelFunLandPreview() {
    AirFuelFunLand(
        afCorrectionCurVal = 0.0f,
        afCorrectionMinVal = 0.0f,
        afCorrectionMaxVal = 0.0f,
        afLearnCurVal = 0.0f,
        afLearnMinVal = 0.0f,
        afLearnMaxVal = 0.0f,
        afRatioCurVal = 0.0f,
        afRatioMinVal = 0.0f,
        afRatioMaxVal = 0.0f,
        modifier = Modifier
    )
}