package com.example.smartalarm


import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.TextFieldDefaults


@Composable
fun AlarmEditPage( alarm: AlarmEntity,
                   onSave: (AlarmEntity) -> Unit,
                   onCancel: () -> Unit,
                   onDelete: () -> Unit
) {

    var time by remember { mutableStateOf(alarm.time) }
    var label by remember { mutableStateOf(alarm.label) }
    var repeatDays by remember { mutableStateOf(alarm.repeatDays) }
    var sound by remember { mutableIntStateOf(alarm.sound) }
    var mission by remember { mutableStateOf(alarm.mission) }
    var snooze by remember { mutableStateOf(alarm.snooze) }

    var mode by remember { mutableStateOf("main") }
    // "main", "editRepeatDays", "editTime", "editLabel", "editSound", "editMission"
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
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
                    Text(text = "edit alarm", color = Color(0xFF0F141E),
                        fontSize = 25.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    TextButton(
                        onClick = { onSave(alarm.copy(
                            time = time,
                            label = label,
                            repeatDays = repeatDays,
                            sound = sound,
                            mission = mission,
                            snooze = snooze
                        )) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "save", fontSize = 20.sp, textAlign = TextAlign.End,
                            fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, color = Color(0xFF202426))
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
                        Text(text = time, fontSize = 45.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily =  FontFamily.SansSerif, color = Color(0xFFFCFAEE))
                    }
                }
                LabelsSection(alarm = alarm,
                    onSnoozeChange = {snooze = !snooze},
                    onEditRepeatDays = { mode = "editRepeatDays" },
                    onEditLabel = { mode = "editLabel" },
                    onEditSound = { mode = "editSound" },
                    onEditMission = { mode = "editMission" })
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center // Center the button horizontally
                ) {
                    ElevatedButton(
                        onClick = { onDelete() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB58089)
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.7f) // 70% width
                            .height(80.dp)
                            .padding(top = 16.dp)
                    ) {
                        Text("delete alarm", fontSize = 25.sp, color = Color(0xFFFCFAEE), fontFamily =  FontFamily.SansSerif)
                    }
                }
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
fun LabelsSection(alarm: AlarmEntity,
                  onSnoozeChange: () -> Unit,
                  onEditRepeatDays: () -> Unit,
                  onEditLabel: () -> Unit,
                  onEditSound: () -> Unit,
                  onEditMission: () -> Unit
) {
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .wrapContentHeight()
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
                Text(text = alarm.mission+ " >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
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
                Text(text = showDays(alarm.repeatDays) + " >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
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
                Text(text = alarm.label + " >", fontSize = 25.sp, textAlign = TextAlign.End, color = Color(0xFFFCFAEE),
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
            val soundName = soundOptions.find { it.second == alarm.sound }?.first
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
                    textAlign = TextAlign.Start, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif, fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = alarm.snooze,
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

fun showDays(days: List<String>): String {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val weekendDays = listOf("Saturday", "Sunday")
    val weekdayDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    return if (days.isEmpty()) {
        "One-time"
    } else if (days.toSet() == daysOfWeek.toSet()) {
        "Everyday"
    } else if (days.toSet() == weekendDays.toSet()) {
        "Weekends"
    } else if (days.toSet() == weekdayDays.toSet()) {
        "Weekdays"
    } else if (days.size <= 3){
        days.map { it.substring(0, 3) }.joinToString(", ")
    } else {
        days.map { it.substring(0, 3) }.take(3).joinToString(", ") + ",..."
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTime(initialTime: String,
             onDone: (String) -> Unit,
             onCancel: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.split(":")[0].toInt(),
        initialMinute = initialTime.split(":")[1].toInt(),
        is24Hour = true,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text(text = "< back", fontFamily =  FontFamily.SansSerif,
                fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                textAlign = TextAlign.Start, color = Color(0xFF202426))
        }
        Text(
            text = "Time",
            fontSize = 25.sp,
            color = Color(0xFF0F141E),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily =  FontFamily.SansSerif,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(40.dp))
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter // Center contents vertically and horizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimePicker(
                state = timePickerState,
            )
            Spacer(modifier = Modifier.height(25.dp))
            ElevatedButton(onClick = {
                val selectedTime = "${timePickerState.hour}:${timePickerState.minute}"
                onDone(selectedTime) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCFAEE)),
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(40.dp)
            ) {
                Text("done", color = Color(0xFF384B70), fontSize = 18.sp, fontFamily =  FontFamily.SansSerif)
            }
        }
    }
}

@Composable
fun EditRepeatDays(initialRepeatDays: List<String>,
                   onDone: (List<String>) -> Unit,
                   onCancel: () -> Unit
) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val weekendDays = listOf("Saturday", "Sunday")
    val weekdayDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    // Track which days are selected
    val selectedDays = remember { mutableStateOf(initialRepeatDays.toMutableSet()) }
    var isWeekendChecked by remember { mutableStateOf(selectedDays.value.containsAll(weekendDays)) }
    var isWeekdayChecked by remember { mutableStateOf(selectedDays.value.containsAll(weekdayDays)) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(text = "< back", fontFamily =  FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                    textAlign = TextAlign.Start, color = Color(0xFF202426))
            }
            Text(
                text = "Repeat",
                fontSize = 25.sp,
                color = Color(0xFF0F141E),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily =  FontFamily.SansSerif,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(40.dp))

        // checkbox
        // Row with Weekends and Weekdays buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Weekends Button
            OutlinedButton(
                modifier = Modifier.padding(horizontal = 10.dp),
                onClick = {
                    isWeekendChecked = !isWeekendChecked
                    if (isWeekendChecked) {
                        selectedDays.value.addAll(weekendDays)
                    } else {
                        selectedDays.value.removeAll(weekendDays)
                    }
                },
                border = BorderStroke(2.dp, if (isWeekendChecked) Color(0xFFB58089) else Color(0xFFFCFAEE)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Weekends", fontFamily =  FontFamily.SansSerif,
                    fontSize = 18.sp,
                    color = if (isWeekendChecked) Color(0xFFB58089) else Color(0xFFFCFAEE)
                )
            }
            // Weekdays Button
            OutlinedButton(
                modifier = Modifier.padding(horizontal = 10.dp),
                onClick = {
                    isWeekdayChecked = !isWeekdayChecked
                    if (isWeekdayChecked) {
                        selectedDays.value.addAll(weekdayDays)
                    } else {
                        selectedDays.value.removeAll(weekdayDays)
                    }
                },
                border = BorderStroke(2.dp, if (isWeekdayChecked) Color(0xFFB58089) else Color(0xFFFCFAEE)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Weekdays", fontFamily =  FontFamily.SansSerif,
                    fontSize = 18.sp,
                    color = if (isWeekdayChecked) Color(0xFFB58089) else Color(0xFFFCFAEE)
                )
            }

        }
        Spacer(modifier = Modifier.height(25.dp))
        // Individual day checkboxes
        daysOfWeek.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedDays.value.contains(day),
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedDays.value.add(day)
                        } else {
                            selectedDays.value.remove(day)
                        }
                        isWeekendChecked = selectedDays.value.containsAll(weekendDays)
                        isWeekdayChecked = selectedDays.value.containsAll(weekdayDays)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFB58089),      // Color when the checkbox is checked
                        uncheckedColor = Color(0xFFFCFAEE),    // Color when the checkbox is unchecked
                        checkmarkColor = Color(0xFFFCFAEE)
                    )
                )
                Text(day, fontWeight = FontWeight.SemiBold, fontFamily =  FontFamily.SansSerif, fontSize = 18.sp, color = Color(0xFFFCFAEE))
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center button horizontally
        ) {
            ElevatedButton(onClick = { onDone(selectedDays.value.toList())},
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCFAEE)),
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(40.dp)
            ) {
                Text("done", color = Color(0xFF384B70), fontSize = 18.sp, fontFamily =  FontFamily.SansSerif)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLabel(initialLabel: String,
              onDone: (String) -> Unit,
              onCancel: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(text = "< back", fontFamily =  FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                    textAlign = TextAlign.Start, color = Color(0xFF202426))
            }
            Text(
                text = "Label",
                fontSize = 25.sp,
                color = Color(0xFF0F141E),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily =  FontFamily.SansSerif,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(40.dp))

        // Label input
        var text by remember { mutableStateOf(initialLabel) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .height(75.dp),
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    color = Color(0xFFFCFAEE),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color(0xFFFCFAEE),
                    focusedBorderColor = Color(0xFFFCFAEE),
                    cursorColor = Color(0xFFFCFAEE)),
                label = { Text("Add a label", fontSize = 15.sp, fontFamily = FontFamily.SansSerif, color = Color(0xFFFCFAEE)) }
            )

            Spacer(modifier = Modifier.height(50.dp))

            ElevatedButton(
                onClick = { onDone(text) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCFAEE)),
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(40.dp)
            ) {
                Text("done", color = Color(0xFF384B70), fontSize = 18.sp, fontFamily = FontFamily.SansSerif)
            }
        }


    }
}

@Composable
fun EditSound(initialSound: Int,
              onDone: (Int) -> Unit,
              onCancel: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(text = "< back", fontFamily =  FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                    textAlign = TextAlign.Start, color = Color(0xFF202426))
            }
            Text(
                text = "Sound",
                fontSize = 25.sp,
                color = Color(0xFF0F141E),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily =  FontFamily.SansSerif,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(40.dp))

        SelectAlarmSound(initialSound, onDone)


    }
}
@Composable
fun SelectAlarmSound(initialSound: Int, onDone: (Int) -> Unit) {
    val context = LocalContext.current
    val soundOptions = listOf(
        Pair("Funny Rooster", R.raw.funny_rooster),
        Pair("Igor Mikić", R.raw.igor),
        Pair("Lofi", R.raw.lofi),
        Pair("Morning Joy", R.raw.morning_joy),
        Pair("Oversimplified", R.raw.oversimplified),
        Pair("Radar", R.raw.radar)
    )

    // Find the initial index based on the provided initial sound resource ID
    val initialIndex = soundOptions.indexOfFirst { it.second == initialSound }
    var selectedSoundIndex by remember { mutableIntStateOf(initialIndex) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                "Select Alarm Sound",
                color = Color(0xFFFCFAEE),
                fontSize = 20.sp,
                fontFamily =  FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display radio buttons for each sound option
        soundOptions.forEachIndexed { index, soundOption ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedSoundIndex == index,
                    onClick = {
                        if (selectedSoundIndex != index) {
                            selectedSoundIndex = index
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(context, soundOption.second)
                            mediaPlayer?.start()
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        unselectedColor = Color(0xFFFCFAEE), // Color when disabled
                        selectedColor = Color(0xFFB58089)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(soundOption.first, fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp, color = Color(0xFFFCFAEE),
                    fontFamily =  FontFamily.SansSerif)
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center button horizontally
        ) {
            ElevatedButton(
                onClick = {
                    mediaPlayer?.release()
                    onDone(soundOptions[selectedSoundIndex].second)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCFAEE)),
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(40.dp)
            ) {
                Text("done", color = Color(0xFF384B70), fontSize = 18.sp, fontFamily = FontFamily.SansSerif)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
}

// TODO nedokončane misije
@Composable
fun EditMission(initialMission: String,
                onDone: (String) -> Unit,
                onCancel: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(text = "< back", fontFamily =  FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                    textAlign = TextAlign.Start, color = Color(0xFF202426))
            }
            Text(
                text = "Mission",
                fontSize = 25.sp,
                color = Color(0xFF0F141E),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily =  FontFamily.SansSerif,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center button horizontally
        ) {
            ElevatedButton(onClick = { onDone("Math") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCFAEE)),
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(40.dp)
            ) {
                Text("done", color = Color(0xFF384B70), fontSize = 18.sp, fontFamily =  FontFamily.SansSerif)
            }
        }


    }
}

