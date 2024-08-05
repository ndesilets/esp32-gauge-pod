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
        Text(fontSize = 10.sp, text ="KNOCK")
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            MinMaxNumericGauge(
                title = "DAM",
                curVal = dam.value,
                minVal = dam.summary.minSession,
                maxVal = dam.summary.maxSession,
                modifier = modifier.weight(1f)
            )
            MinMaxNumericGauge(
                title = "Feedback",
                curVal = afCorrection.value,
                minVal = afCorrection.summary.minSession,
                maxVal = afCorrection.summary.maxSession,
                modifier = modifier.weight(1f)
            )
            MinMaxNumericGauge(
                title = "Learned",
                curVal = afLearn.value,
                minVal = afLearn.summary.minSession,
                maxVal = afLearn.summary.maxSession,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000, widthDp = 400, heightDp = 120)
@Composable
fun KnockFunLandPreview() {
    KnockFunLand(
        dam = MonitoredNumericSensor(),
        afLearn = MonitoredNumericSensor(),
        afCorrection = MonitoredNumericSensor(),
        modifier = Modifier
    )
}