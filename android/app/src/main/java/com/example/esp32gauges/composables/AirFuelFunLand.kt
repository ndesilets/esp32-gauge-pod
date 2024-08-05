package com.example.esp32gauges.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32gauges.models.sensors.MonitoredNumericSensor

@Composable
fun AirFuelFunLand(
    afCorrection: MonitoredNumericSensor,
    afLearn: MonitoredNumericSensor,
    afRatio: MonitoredNumericSensor,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(fontSize = 10.sp, text = "AIR AND FUEL")
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MinMaxNumericGauge(
                title = "S. Trim",
                curVal = afCorrection.value,
                minVal = afCorrection.summary.minSession,
                maxVal = afCorrection.summary.maxSession,
            )
            MinMaxNumericGauge(
                title = "L. Trim",
                curVal = afLearn.value,
                minVal = afLearn.summary.minSession,
                maxVal = afLearn.summary.maxSession,
            )
            SimpleNumericGauge(
                title = "Trim Sum",
                curVal = afCorrection.value + afLearn.value,
                modifier = modifier
                    .align(Alignment.Top)
            )
            MinMaxNumericGauge(
                title = "AFR",
                curVal = afRatio.value,
                minVal = afRatio.summary.minSession,
                maxVal = afRatio.summary.maxSession,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 400)
@Composable
fun AirFuelFunLandPreview() {
    AirFuelFunLand(
        afCorrection = MonitoredNumericSensor(),
        afLearn = MonitoredNumericSensor(),
        afRatio = MonitoredNumericSensor(),
        modifier = Modifier
    )
}