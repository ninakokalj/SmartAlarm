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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
fun AddAlarmPage( onSave: (AlarmEntity) -> Unit, onCancel: () -> Unit) {

    var time by remember { mutableStateOf("07:00") }
    var label by remember { mutableStateOf("Alarm") }
    var repeatDays by remember { mutableStateOf(emptyList<String>()) }
    var sound by remember { mutableIntStateOf(R.raw.radar) }
    var mission by remember { mutableStateOf("Math") }
    var snooze by remember { mutableStateOf(true) }

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
                                mission = mission,
                                snooze = snooze
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
                    contentAlignment = Alignment.Center // Center the button horizontally
                ) {
                    ElevatedButton(
                        onClick = {mode = "editTime"},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF596266)),
                        modifier = Modifier
                            .fillMaxWidth(0.5f) // Make the button take 70% of the width (adjustable)
                            .height(75.dp)
                    ) {
                        Text(text = time, fontSize = 45.sp, fontWeight = FontWeight.ExtraBold, fontFamily =  FontFamily.SansSerif)
                    }
                }
                AddLabelsSection(label = label,
                    repeatDays = repeatDays,
                    sound = sound,
                    mission = mission,
                    snooze = snooze,
                    onSnoozeChange = {snooze = !snooze},
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

@Composable
fun AddLabelsSection(label: String,
                  repeatDays: List<String>,
                  sound: Int,
                  mission: String,
                  snooze: Boolean,
                  onSnoozeChange: () -> Unit,
                  onEditRepeatDays: () -> Unit,
                  onEditLabel: () -> Unit,
                  onEditSound: () -> Unit,
                  onEditMission: () -> Unit
) {
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = { onEditMission() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), // Make Row fill the TextButton
                horizontalArrangement = Arrangement.SpaceBetween // Align texts at both ends
            ) {
                Text(text = "Mission", fontSize = 25.sp, textAlign = TextAlign.Start, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                Text(text = "$mission >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(
            onClick = { onEditRepeatDays() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), // Make Row fill the TextButton
                horizontalArrangement = Arrangement.SpaceBetween // Align texts at both ends
            ) {
                Text(text = "Repeat", fontSize = 25.sp, textAlign = TextAlign.Start, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                Text(text =  showDays(repeatDays) + " >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(
            onClick = { onEditLabel() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), // Make Row fill the TextButton
                horizontalArrangement = Arrangement.SpaceBetween // Align texts at both ends
            ) {
                Text(text = "Label", fontSize = 25.sp, textAlign = TextAlign.Start, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                Text(text = "$label >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(
            onClick = { onEditSound() },
            modifier = Modifier.fillMaxWidth()
        ) {
            val soundOptions = listOf(
                Pair("Funny Rooster", R.raw.funny_rooster),
                Pair("Igor Mikić", R.raw.igor),
                Pair("Lofi", R.raw.lofi),
                Pair("Morning Joy", R.raw.morning_joy),
                Pair("Oversimplified", R.raw.oversimplified),
                Pair("Radar", R.raw.radar)
            )
            val soundName = soundOptions.find { it.second == sound }?.first
            Row(
                modifier = Modifier.fillMaxWidth(), // Make Row fill the TextButton
                horizontalArrangement = Arrangement.SpaceBetween // Align texts at both ends
            ) {
                Text(text = "Sound", fontSize = 25.sp, textAlign = TextAlign.Start, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                Text(text = "$soundName >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), // Make Row fill the TextButton
                horizontalArrangement = Arrangement.SpaceBetween // Align texts at both ends
            ) {
                Text(
                    text = "Snooze",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = snooze,
                    onCheckedChange = {
                        onSnoozeChange()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFBA4054),
                        uncheckedThumbColor = Color(0xFF2F393D),
                        checkedTrackColor = Color(0xFFB58089),
                        uncheckedTrackColor = Color(0xFF404F54)
                    )
                )
            }
        }
    }
}




