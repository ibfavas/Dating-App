package com.fyndapp.fynd.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.other.Screens
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.fyndapp.fynd.R
import com.fyndapp.fynd.other.UserProfile
import java.util.Calendar
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val currentUser = authViewModel.authState.collectAsState().value.user
    var profiles by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffset by animateDpAsState(targetValue = offsetX.dp)

    // Avatar resources mapping
    val avatars = remember {
        mapOf(
            "male1" to R.drawable.male_avatar1,
            "male2" to R.drawable.male_avatar2,
            "male3" to R.drawable.male_avatar3,
            "male4" to R.drawable.male_avatar4,
            "male5" to R.drawable.male_avatar5,
            "male6" to R.drawable.male_avatar6,
            "female1" to R.drawable.female_avatar1,
            "female2" to R.drawable.female_avatar2,
            "female3" to R.drawable.female_avatar3,
            "female4" to R.drawable.female_avatar4,
            "female5" to R.drawable.female_avatar5,
            "female6" to R.drawable.female_avatar6
        )
    }

    // Fetch compatible profiles
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val db = Firebase.firestore
            val currentUserDoc = db.collection("users").document(user.uid).get().await()
            val currentUserGender = currentUserDoc.getString("gender") ?: ""
            val currentUserLanguage = currentUserDoc.getString("language") ?: ""
            val currentUserAvatar = currentUserDoc.getString("avatar") ?: ""

            if (currentUserGender.isNotEmpty() && currentUserLanguage.isNotEmpty()) {
                val query = db.collection("users")
                    .whereEqualTo("language", currentUserLanguage)
                    .whereEqualTo("gender", if (currentUserGender == "Male") "Female" else "Male")

                val result = query.get().await()
                profiles = result.documents.mapNotNull { doc ->
                    if (doc.id == user.uid) return@mapNotNull null // Skip current user

                    val name = doc.getString("name") ?: return@mapNotNull null
                    val dob = doc.getString("dob") ?: return@mapNotNull null
                    val avatarName = doc.getString("avatar") ?: "default"
                    val avatarResId = avatars[avatarName] ?: R.drawable.user

                    UserProfile(
                        id = doc.id,
                        name = name,
                        age = calculateAge(dob),
                        avatarResId = avatarResId,
                        language = currentUserLanguage
                    )
                }.shuffled()
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, currentScreen = Screens.Home) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Light gray background
        ) {
            if (profiles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (currentUser == null) {
                        Text("Please sign in to see profiles")
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Finding compatible matches...")
                        }
                    }
                }
            } else {
                val currentProfile = profiles[currentIndex]
                val nextProfile = profiles.getOrNull(currentIndex + 1)

                // Next profile peek (shown behind the main card)
                if (nextProfile != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 32.dp, start = 32.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth(0.9f)
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .shadow(4.dp, RoundedCornerShape(24.dp))
                    ) {
                        Image(
                            painter = painterResource(id = nextProfile.avatarResId),
                            contentDescription = "Next Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Main Profile Card
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.95f)
                        .aspectRatio(0.75f)
                        .offset(x = animatedOffset)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { _, dragAmount ->
                                    offsetX = dragAmount.x
                                },
                                onDragEnd = {
                                    when {
                                        abs(offsetX) > 100f -> { // Swiped left or right
                                            if (offsetX < 0) { // Swiped left
                                                if (currentIndex < profiles.size - 1) currentIndex++
                                            } else { // Swiped right
                                                if (currentIndex > 0) currentIndex--
                                            }
                                        }
                                    }
                                    offsetX = 0f
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .fillMaxSize()
                            .shadow(16.dp, RoundedCornerShape(24.dp))
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = currentProfile.avatarResId),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Gradient overlay at bottom
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            ),
                                            startY = 0f,
                                            endY = 100f
                                        )
                                    )
                            )
                        }

                        // Profile Info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = currentProfile.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${currentProfile.age}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White.copy(alpha = 0.9f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Professional model", // You might want to make this dynamic from your UserProfile
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            // Like button (heart icon)
                            IconButton(
                                onClick = { /* Handle like */ },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(48.dp)
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_plus), // Make sure you have this icon
                                    contentDescription = "Like",
                                    tint = Color.Red,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Navigation buttons at bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous button
                    IconButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_left),
                            contentDescription = "Previous",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    // Next button
                    IconButton(
                        onClick = {
                            if (currentIndex < profiles.size - 1) currentIndex++
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_right),
                            contentDescription = "Next",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
// Helper function to calculate age from DOB (format: "dd/MM/yyyy")
private fun calculateAge(dob: String): Int {
    val parts = dob.split("/")
    if (parts.size != 3) return 0
    val (day, month, year) = parts

    val dobCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year.toInt())
        set(Calendar.MONTH, month.toInt() - 1)
        set(Calendar.DAY_OF_MONTH, day.toInt())
    }

    val today = Calendar.getInstance()
    var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}