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
fun KnockFunLand(
    dam: MonitoredNumericSensor,
    afCorrection: MonitoredNumericSensor,
    afLearn: MonitoredNumericSensor,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(fontSize = 10.sp, text = "KNOCK")
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MinMaxNumericGauge(
                title = "DAM",
                curVal = dam.value,
                minVal = dam.summary.minSession,
                maxVal = dam.summary.maxSession,
            )
            MinMaxNumericGauge(
                title = "Feedback",
                curVal = afCorrection.value,
                minVal = afCorrection.summary.minSession,
                maxVal = afCorrection.summary.maxSession,
            )
            MinMaxNumericGauge(
                title = "Learned",
                curVal = afLearn.value,
                minVal = afLearn.summary.minSession,
                maxVal = afLearn.summary.maxSession,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 400)
@Composable
fun KnockFunLandPreview() {
    KnockFunLand(
        dam = MonitoredNumericSensor(),
        afLearn = MonitoredNumericSensor(),
        afCorrection = MonitoredNumericSensor(),
        modifier = Modifier
    )
}