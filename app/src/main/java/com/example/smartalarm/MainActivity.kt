package com.example.smartalarm

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import java.util.Calendar

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // primarni ključ alarma auto-generated
    val time: String,                                   // čas "07:03"
    val label: String = "Alarm",                        // label / ime alarma
    val isEnabled: Boolean = true,                      // ali naj zvoni
    val repeatDays: List<String> = listOf(),            // katere dni
    val sound: Int,                                     // R.raw.alarm1
    val mission: String                                 // Math, None
)


class MainActivity : ComponentActivity() {

    private lateinit var alarmViewModel: AlarmViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        alarmViewModel = ViewModelProvider(this)[AlarmViewModel::class.java]


        setContent {

            val navController = rememberNavController()
            // pogledam če je app odprl alarm
            val startDestination = if (intent.hasExtra("ALARM_MISSION")) {
                "mathMission"
            } else {
                "alarmList"
            }

            SmartAlarmTheme {
                val ctx = LocalContext.current
                val alarms by alarmViewModel.allAlarms.observeAsState(emptyList())

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF384B70))
                            .padding(paddingValues)
                    ) {
                        // navigation graph
                        NavHost(navController = navController, startDestination = startDestination) {
                            // First screen: Alarm list
                            composable("alarmList") {
                                AlarmScreen(
                                    alarms = alarms,
                                    context = ctx,
                                    modifier = Modifier.padding(paddingValues),
                                    onAlarmChange = { updatedAlarm ->
                                        alarmViewModel.updateAlarm(ctx, updatedAlarm)
                                    },
                                    onAlarmClick = { alarm ->
                                        navController.navigate("editAlarm/${alarm.id}")
                                    },
                                    addAlarm = { navController.navigate("addAlarm") },
                                    scheduleAlarm = { context, alarm -> alarmViewModel.scheduleAlarm(context, alarm) },
                                    unscheduleAlarm = { context, alarm -> alarmViewModel.unscheduleAlarm(context, alarm) }
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
                                            alarmViewModel.updateAlarm(ctx, updatedAlarm)
                                            navController.popBackStack()
                                        },
                                        onCancel = { navController.popBackStack() },
                                        onDelete = {
                                            alarmViewModel.deleteAlarm(ctx, alarm)
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                            // Third screen: Add alarm screen
                            composable("addAlarm") {
                                AddAlarmPage(
                                    onSave = { newAlarm ->
                                        alarmViewModel.addAlarm(ctx, newAlarm)
                                        navController.popBackStack()
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                            // Fourth screen: Math mission screen
                            composable("mathMission") {
                                val mission = intent.getStringExtra("ALARM_MISSION") ?: "None"
                                val id = intent?.getIntExtra("ALARM_ID", 0) ?: 0

                                MathMissionPage(
                                    mission = mission,
                                    navController = navController
                                )

                                // odenablaš ko enkrat zvoni
                                val alarm = alarms.find {it.id == id}
                                val ctx = LocalContext.current
                                if (alarm != null && alarm.isEnabled && alarm.repeatDays.isEmpty()) {
                                    val updatedAlarm = alarm.copy(isEnabled = false)
                                    alarmViewModel.updateAlarm(ctx, updatedAlarm)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// funkcija, ki displaya alarm details
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(alarms: List<AlarmEntity>, context: Context, modifier: Modifier, onAlarmChange: (AlarmEntity) -> Unit,
                onAlarmClick: (AlarmEntity) -> Unit, addAlarm: () -> Unit,
                scheduleAlarm: (Context, AlarmEntity) -> Unit,
                unscheduleAlarm: (Context, AlarmEntity) -> Unit,) {

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getDailyText(),
                            color = Color(0xFFFCFAEE),
                            fontSize = 30.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(alarms) { alarm ->
                    AlarmButton(alarm = alarm, onAlarmChange = onAlarmChange,
                        onAlarmClick = onAlarmClick,
                        scheduleAlarm = scheduleAlarm,
                        unscheduleAlarm = unscheduleAlarm,
                        context = context,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDailyText(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (currentHour) {
        in 5..11 -> "Good Morning ☕"
        in 12..17 -> "Hello \uD83C\uDF1E"
        in 18..21 -> "Good Evening \uD83C\uDF19"
        else -> "Good Night \uD83D\uDE34"
    }
}

@Composable
fun AlarmButton(alarm: AlarmEntity,  context: Context, onAlarmChange: (AlarmEntity) -> Unit,
                onAlarmClick: (AlarmEntity) -> Unit, scheduleAlarm: (Context, AlarmEntity) -> Unit,
                unscheduleAlarm: (Context, AlarmEntity) -> Unit, modifier: Modifier = Modifier) {

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
                    val updatedAlarm = alarm.copy(isEnabled = it)
                    onAlarmChange(updatedAlarm)
                    if (it) {
                        scheduleAlarm(context, updatedAlarm)
                    } else {
                        unscheduleAlarm(context, updatedAlarm)
                    }
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

