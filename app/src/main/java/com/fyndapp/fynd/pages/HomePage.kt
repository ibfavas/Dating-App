package com.fyndapp.fynd.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
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
    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

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

    fun fetchProfiles() {
        scope.launch {
            isLoading = true
            currentUser?.let { user ->
                val db = Firebase.firestore
                val currentUserDoc = db.collection("users").document(user.uid).get().await()
                val currentUserGender = currentUserDoc.getString("gender") ?: ""
                val currentUserLanguage = currentUserDoc.getString("language") ?: ""
                val currentUserInterests = currentUserDoc.get("interests") as? List<String> ?: emptyList()

                if (currentUserGender.isNotEmpty() && currentUserLanguage.isNotEmpty()) {
                    val query = db.collection("users")
                        .whereEqualTo("language", currentUserLanguage)
                        .whereEqualTo("gender", if (currentUserGender == "Male") "Female" else "Male")

                    val result = query.get().await()

                    // Create a list with scoring based on interests match
                    profiles = result.documents.mapNotNull { doc ->
                        if (doc.id == user.uid) return@mapNotNull null
                        val name = doc.getString("name") ?: return@mapNotNull null
                        val dob = doc.getString("dob") ?: return@mapNotNull null
                        val avatarName = doc.getString("avatar") ?: "default"
                        val avatarResId = avatars[avatarName] ?: R.drawable.user
                        val userInterests = doc.get("interests") as? List<String> ?: emptyList()

                        // Calculate match score based on common interests
                        val commonInterests = currentUserInterests.intersect(userInterests.toSet()).size
                        val randomFactor = (0..100).random() // Add some randomness
                        val matchScore = commonInterests * 10 + randomFactor

                        UserProfile(
                            id = doc.id,
                            name = name,
                            age = calculateAge(dob),
                            avatarResId = avatarResId,
                            language = currentUserLanguage,
                            matchScore = matchScore
                        )
                    }
                        // Sort by match score (higher first) and then shuffle the top candidates
                        .sortedByDescending { it.matchScore }
                        .let { sortedList ->
                            // Take top 50% of matches and shuffle them
                            val topMatches = sortedList.take(sortedList.size / 2).shuffled() +
                                    sortedList.drop(sortedList.size / 2).shuffled()
                            topMatches
                        }
                }
            }
            isLoading = false
        }
    }


    LaunchedEffect(currentUser) {
        fetchProfiles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FYND",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_coin),
                            contentDescription = "Coin",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "120", // Replace with actual coin count from state
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.Home)
        }
    ) { innerPadding ->
        // App name top-left
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Finding compatible matches...")
                }
            } else if (profiles.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No more profiles to show", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { fetchProfiles() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Refresh Profiles", color = Color.White)
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Only show the top profile (first in the list)
                    val topProfile = profiles.firstOrNull()

                    if (topProfile != null) {
                        SwipeableUserCard(
                            userProfile = topProfile,
                            isTop = true,
                            isAnimating = isAnimating,
                            onSwiped = { direction ->
                                if (!isAnimating) {
                                    isAnimating = true
                                    scope.launch {
                                        delay(300)
                                        val updated = profiles.toMutableList()
                                        updated.removeAt(0)
                                        profiles = updated
                                        isAnimating = false
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Swipe left or right to fynd a new partner",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeableUserCard(
    userProfile: UserProfile,
    isTop: Boolean,
    isAnimating: Boolean,
    onSwiped: (Direction) -> Unit
) {
    var dragOffsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isAnimating && isTop) dragOffsetX else 0f,
        animationSpec = if (isAnimating && isTop) tween(500, easing = FastOutLinearInEasing) else snap(),
        label = "AnimatedX"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isAnimating && isTop) 0f else 1f,
        animationSpec = if (isAnimating && isTop) tween(500) else snap(),
        label = "AnimatedAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(0.7f)
            .graphicsLayer {
                translationX = animatedOffsetX
                alpha = animatedAlpha
                rotationZ = animatedOffsetX / 60f
            }
            .pointerInput(isTop) {
                if (isTop && !isAnimating) {
                    detectDragGestures(
                        onDragEnd = {
                            val direction = if (dragOffsetX > 100f) Direction.RIGHT
                            else if (dragOffsetX < -100f) Direction.LEFT
                            else null

                            if (direction != null) {
                                onSwiped(direction)
                            } else {
                                dragOffsetX = 0f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffsetX += dragAmount.x
                        }
                    )
                }
            }
    ) {
        // White container box with shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true
                )
                .background(MaterialTheme.colorScheme.onTertiary, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Card with avatar image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = userProfile.avatarResId),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay at bottom for text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .align(Alignment.BottomCenter)
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

                        // User info positioned at bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${userProfile.age} • ${userProfile.language}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Buttons row at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left button (Call)
                    IconButton(
                        onClick = {  },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_call),
                            contentDescription = "Call",
                            tint = Color(0xFF8BEC8D),
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    // Center button (Chat)
                    IconButton(
                        onClick = {  },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_chat),
                            contentDescription = "Chat",
                            tint = MaterialTheme.colorScheme.inverseSurface,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    // Right button (Video Call)
                    IconButton(
                        onClick = { /* Handle super like */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_videocall),
                            contentDescription = "Video Call",
                            tint = Color(0xFFBB86FC),
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
    }
}

enum class Direction { LEFT, RIGHT }


// Helper function to calculate age
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