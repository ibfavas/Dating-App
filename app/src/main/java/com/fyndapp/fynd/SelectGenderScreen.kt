package com.fyndapp.fynd

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fyndapp.fynd.other.Screens
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun SelectGenderScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            },
            year, month, day
        ).apply {
            this.datePicker.maxDate = System.currentTimeMillis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App name top-left
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = "FYND",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Image(
            painter = painterResource(id = R.drawable.valentine),
            contentDescription = "Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 12.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Complete Your Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                textStyle = TextStyle(color = Color.Black),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Attractive Date of Birth Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFF7F48D7),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { datePicker.show() },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (dob.isEmpty()) "Select Date of Birth" else dob,
                    color = if (dob.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Select Gender",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(75.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GenderBox(
                    text = "Male",
                    isSelected = selectedGender == "Male",
                    onClick = { selectedGender = "Male" }
                )

                GenderBox(
                    text = "Female",
                    isSelected = selectedGender == "Female",
                    onClick = { selectedGender = "Female" }
                )
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && dob.isNotBlank() && selectedGender != null) {
                        isLoading = true
                        errorMessage = null

                        authState.user?.let { user ->
                            coroutineScope.launch {
                                try {
                                    val success = authViewModel.saveUserDetails(
                                        userId = user.uid,
                                        name = name,
                                        dob = dob,
                                        gender = selectedGender!!
                                    )

                                    if (success) {
                                        navController.navigate(Screens.Home.route) {
                                            popUpTo(Screens.SelectGender.route) { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = "Failed to save profile. Please try again."
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading && name.isNotBlank() && dob.isNotBlank() && selectedGender != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F48D7))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.LightGray)
                } else {
                    Text("Continue", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun GenderBox(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF7F48D7) else Color.Black
    val textColor = if (isSelected) Color(0xFF7F48D7) else Color.Black

    Box(
        modifier = Modifier
            .size(120.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
