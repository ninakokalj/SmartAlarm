package com.example.smartalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.smartalarm.ui.theme.SmartAlarmTheme
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontFamily

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // primarni ključ alarma auto-generated
    val time: String,                                   // čas "07:03"
    val label: String = "Alarm",                        // label / ime alarma
    val isEnabled: Boolean = true,                      // ali naj zvoni
    val repeatDays: List<String> = listOf(),            // katere dni
    val sound: Int,                                     // R.raw.alarm1
    val mission: String,                                // 0-Math, 1-Missing Symbol
    val snooze: Boolean = true                          // ali je snooze omogočen
)



class MainActivity : ComponentActivity() {

    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)


        setContent {

            val navController = rememberNavController()

            SmartAlarmTheme {

                val alarms by alarmViewModel.allAlarms.observeAsState(emptyList())

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF384B70)) // Set the background color here
                            .padding(paddingValues) // Apply padding values from Scaffold
                    ) {
                        // navigation graph
                        NavHost(navController = navController, startDestination = "alarmList") {
                            // First screen: Alarm list
                            composable("alarmList") {
                                AlarmScreen(
                                    alarms = alarms,
                                    modifier = Modifier.padding(paddingValues),
                                    onAlarmChange = { updatedAlarm ->
                                        alarmViewModel.updateAlarm(updatedAlarm)
                                    },
                                    onAlarmClick = { alarm ->
                                        navController.navigate("editAlarm/${alarm.id}")
                                    },
                                    addAlarm = { navController.navigate("addAlarm") }
                                )
                            }
                            // Second screen: Edit alarm screen
                            composable("editAlarm/{alarmId}") { navBackStackEntry ->
                                // poberem argumente dol
                                val alarmId = navBackStackEntry.arguments?.getString("alarmId")?.toInt() ?: 0
                                // dobim alarm entity
                                val alarm = alarms.find {it.id == alarmId}
                                if (alarm != null) {
                                    AlarmEditPage(
                                        alarm = alarm,
                                        onSave = { updatedAlarm ->
                                            alarmViewModel.updateAlarm(updatedAlarm)
                                            navController.popBackStack()
                                        },
                                        onCancel = { navController.popBackStack() },
                                        onDelete = {
                                            alarmViewModel.deleteAlarm(alarm)
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                            composable("addAlarm") {
                                AddAlarmPage(
                                    onSave = { newAlarm ->
                                        alarmViewModel.addAlarm(newAlarm)
                                        navController.popBackStack()
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// This function is used to display alarm details

@Composable
fun AlarmScreen(alarms: List<AlarmEntity>, modifier: Modifier, onAlarmChange: (AlarmEntity) -> Unit, onAlarmClick: (AlarmEntity) -> Unit, addAlarm: () -> Unit) {

    Scaffold (
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addAlarm() },
                modifier = Modifier.size(72.dp),
                containerColor = Color(0xFFB58089),
                contentColor = Color(0xFFFCFAEE)
            ) {
                Icon(imageVector = Icons.Filled.Add,
                    contentDescription = "Floating action button",
                    modifier = Modifier.size(45.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF384B70))
                .padding(paddingValues)
        ) {
            LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp), // Adjust vertical padding for spacing
                        contentAlignment = Alignment.Center // Center-align the text within the box
                    ) {
                        Text(
                            text = "Ring in ...",
                            color = Color(0xFFFCFAEE),
                            fontSize = 25.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                     // Add space between this text and the next items
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(alarms) { alarm ->
                    AlarmButton(alarm = alarm, onAlarmChange = onAlarmChange,
                        onAlarmClick = onAlarmClick,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

}
@Composable
fun AlarmButton(alarm: AlarmEntity, onAlarmChange: (AlarmEntity) -> Unit,
                onAlarmClick: (AlarmEntity) -> Unit, modifier: Modifier = Modifier) { // funk ki prejme AlarmEntity in vrne Unit=void

    var switchState by remember { mutableStateOf(alarm.isEnabled) }

    ElevatedButton(
        onClick = { onAlarmClick(alarm) },
        colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF596266)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
        ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

                Text(
                    text = alarm.time,
                    fontSize = 45.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFCFAEE),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = alarm.label + ", Repeat: " + showDays(alarm.repeatDays),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFCFAEE),
                    fontFamily = FontFamily.SansSerif
                )
            }
            Switch(
                checked = alarm.isEnabled,
                onCheckedChange = {
                    switchState = it
                    // naredim nov alarm s spremenjenim samo isEnabled
                    onAlarmChange(alarm.copy(isEnabled = it))
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

