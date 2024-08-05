package com.example.esp32gauges.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(fontSize = 10.sp, text = "AIR AND FUEL")
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            MinMaxNumericGauge(
                title = "S. Trim",
                curVal = afCorrection.value,
                minVal = afCorrection.summary.minSession,
                maxVal = afCorrection.summary.maxSession,
                modifier = modifier.weight(1f)
            )
            MinMaxNumericGauge(
                title = "L. Trim",
                curVal = afLearn.value,
                minVal = afLearn.summary.minSession,
                maxVal = afLearn.summary.maxSession,
                modifier = modifier.weight(1f)
            )
            SimpleNumericGauge(
                title = "Trim Sum",
                curVal = afCorrection.value + afLearn.value,
                modifier = modifier
                    .weight(1f)
                    .align(Alignment.Top)
            )
            MinMaxNumericGauge(
                title = "AFR",
                curVal = afRatio.value,
                minVal = afRatio.summary.minSession,
                maxVal = afRatio.summary.maxSession,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 400, heightDp = 120)
@Composable
fun AirFuelFunLandPreview() {
    AirFuelFunLand(
        afCorrection = MonitoredNumericSensor(),
        afLearn = MonitoredNumericSensor(),
        afRatio = MonitoredNumericSensor(),
        modifier = Modifier
    )
}