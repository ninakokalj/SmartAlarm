package com.example.smartalarm

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun MathMissionPage(
    mission: String,
    navController: NavController
) {

    var state by remember { mutableIntStateOf(0) }
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }

    // ko se state spremeni, generiraj novo vprašanje
    LaunchedEffect(state) {
        if (state < 3) {
            val (text, res) = mathEquation(state)
            questionText = text
            correctAnswer = res
        }
    }

    // Mission UI
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (mission) {
            "Math" -> MathMissionContent(
                state = state,
                questionText = questionText,
                userInput = userInput,
                onUserInputChange = { userInput = it },
                correctAnswer = correctAnswer,
                onCorrectAnswer = {
                    state++
                    userInput = ""
                    if (state >= 3) {
                        SoundManager.stopSound()
                        navController.navigate("alarmList") {
                            popUpTo("alarmList") { inclusive = false }
                        }
                    } // končaj alarm in zaključi po 3 vprašanjih
                },
                innerPadding = innerPadding
            )
            "None" -> {
                SoundManager.stopSound()
                navController.navigate("alarmList") {
                    popUpTo("alarmList") { inclusive = false }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathMissionContent(
    state: Int,
    questionText: String,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    correctAnswer: Int,
    onCorrectAnswer: () -> Unit,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF384B70))
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state < 3) {
                Text(
                    text = "Solve the equation:",
                    color = Color(0xFFFCFAEE),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )

                Text(
                    text = questionText,
                    color = Color(0xFFFCFAEE),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFB58089),
                            unfocusedBorderColor = Color(0xFF546E7A),
                            cursorColor = Color(0xFFB58089)
                        ),
                        textStyle = TextStyle(
                            color = Color(0xFFFCFAEE),
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        ),
                        onValueChange = onUserInputChange,
                        label = { Text("Your answer", color = Color(0xFFFCFAEE), fontSize = 15.sp, fontFamily = FontFamily.SansSerif) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val userAnswer = userInput.toIntOrNull()
                            if (userAnswer == correctAnswer) {
                                onCorrectAnswer()
                            } else {
                                Toast.makeText(context, "Incorrect, try again!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFCFAEE),
                            contentColor = Color(0xFF384B70)
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Check", fontSize = 20.sp, fontFamily = FontFamily.SansSerif)
                    }
                }

            }
        }
    }
}



fun mathEquation(state: Int): Pair<String, Int> {
    return when (state) {
        0 -> {
            val num1 = Random.nextInt(10, 61)
            val num2 = Random.nextInt(10, 24)
            val sign = if (Random.nextBoolean()) "+" else "-"
            if (sign == "+") "$num1 + $num2" to (num1 + num2)
            else "$num1 - $num2" to (num1 - num2)
        }
        1 -> {
            val num1 = Random.nextInt(2, 17)
            val num2 = Random.nextInt(0, 8)
            val num3 = Random.nextInt(0, 8)
            "$num2 * $num3 + $num1" to (num2 * num3 + num1)
        }
        2 -> {
            val num1 = Random.nextInt(2, 51)
            val num2 = Random.nextInt(0, 8)
            val num3 = Random.nextInt(0, 8)
            val sign = if (Random.nextBoolean()) "+" else "-"
            if (sign == "+") "$num1 + $num2 * $num3" to (num1 + num2 * num3)
            else "$num1 - $num2 * $num3" to (num1 - num2 * num3)
        }
        else -> "" to 0
    }
}
