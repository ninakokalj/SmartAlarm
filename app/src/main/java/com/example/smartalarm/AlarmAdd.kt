package com.example.smartalarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddAlarmPage(onSave: (AlarmEntity) -> Unit, onCancel: () -> Unit) {

    var time by remember { mutableStateOf("07:00") }
    var label by remember { mutableStateOf("Alarm") }
    var repeatDays by remember { mutableStateOf(emptyList<String>()) }
    var sound by remember { mutableIntStateOf(R.raw.radar) }
    var mission by remember { mutableStateOf("Math") }

    var mode by remember { mutableStateOf("main") }
    // "main", "editRepeatDays", "editTime", "editLabel", "editSound", "editMission"

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (mode) {
            "main" -> {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = { onCancel() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "cancel", fontSize = 20.sp, textAlign = TextAlign.Start,
                            fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, color = Color(0xFF202426))
                    }
                    Text(text = "add alarm", color = Color(0xFF0F141E), fontSize = 25.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold, fontFamily =  FontFamily.SansSerif)
                    TextButton(
                        onClick = { onSave(
                            AlarmEntity(
                                time = time,
                                label = label,
                                repeatDays = repeatDays,
                                sound = sound,
                                mission = mission
                            )
                        )},
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "save", fontSize = 20.sp, textAlign = TextAlign.End, fontFamily =  FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold, color = Color(0xFF202426))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // TIME BUTTON
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedButton(
                        onClick = {mode = "editTime"},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF596266)),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(75.dp)
                    ) {
                        Text(text = time, fontSize = 45.sp, fontWeight = FontWeight.ExtraBold, fontFamily =  FontFamily.SansSerif)
                    }
                }
                LabelsSection(label = label,
                    repeatDays = repeatDays,
                    sound = sound,
                    mission = mission,
                    onEditRepeatDays = { mode = "editRepeatDays" },
                    onEditLabel = { mode = "editLabel" },
                    onEditSound = { mode = "editSound" },
                    onEditMission = { mode = "editMission" })
            }
            "editTime" -> EditTime(
                initialTime = time,
                onDone = { newTime ->
                    time = newTime
                    mode = "main"
                },
                onCancel = { mode = "main" }
            )
            "editRepeatDays" -> EditRepeatDays(
                initialRepeatDays = repeatDays,
                onDone = { newDays ->
                    repeatDays = newDays
                    mode = "main"
                },
                onCancel = { mode = "main" })
            "editLabel" -> EditLabel(
                initialLabel = label,
                onDone = { newLabel ->
                    label = newLabel
                    mode = "main"
                },
                onCancel = { mode = "main" }
            )
            "editSound" -> EditSound(
                initialSound = sound,
                onDone = { newSound ->
                    sound = newSound
                    mode = "main"
                },
                onCancel = { mode = "main" }
            )
            "editMission" -> EditMission(
                initialMission = mission,
                onDone = { newMission ->
                    mission = newMission
                    mode = "main"
                },
                onCancel = { mode = "main" }
            )
        }
    }
}





