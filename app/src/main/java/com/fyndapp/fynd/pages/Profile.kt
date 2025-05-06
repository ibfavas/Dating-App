// com.fyndapp.fynd.pages.Profile
package com.fyndapp.fynd.pages

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.R
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.cache.ProfileCache
import com.fyndapp.fynd.other.Screens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.collectAsState().value
    val user = authState.user
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load from cache first
    ProfileCache.loadFromCache(context)

    var name by remember { mutableStateOf(ProfileCache.name) }
    var dob by remember { mutableStateOf(ProfileCache.dob) }
    var gender by remember { mutableStateOf(ProfileCache.gender) }
    var selectedLanguage by remember { mutableStateOf(ProfileCache.language.ifEmpty { "Select a language" }) }
    var expanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "Malayalam", "Hindi", "Tamil", "Marathi",
        "Bengali", "Telugu", "Kannada", "Gujarati",
        "Odia", "Punjabi", "English"
    )

    val avatars = mapOf(
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

    var avatarResId by remember { mutableStateOf(
        if (ProfileCache.avatar.isNotEmpty() && avatars.containsKey(ProfileCache.avatar)) {
            avatars[ProfileCache.avatar]!!
        } else {
            R.drawable.user
        }
    ) }

    LaunchedEffect(user) {
        user?.let {
            val db = Firebase.firestore
            val docRef = db.collection("users").document(it.uid)

            // Only fetch from network if cache is empty
            if (name.isEmpty() || dob.isEmpty() || gender.isEmpty() || selectedLanguage.isEmpty()) {
                try {
                    val userDoc = docRef.get().await()
                    name = userDoc.getString("name") ?: ""
                    dob = userDoc.getString("dob") ?: ""
                    gender = userDoc.getString("gender") ?: ""
                    selectedLanguage = userDoc.getString("language") ?: "Select a language"

                    val avatarName = userDoc.getString("avatar")
                    if (avatarName != null && avatars.containsKey(avatarName)) {
                        avatarResId = avatars[avatarName]!!
                    } else {
                        val randomEntry = avatars.entries.random()
                        avatarResId = randomEntry.value
                        docRef.update("avatar", randomEntry.key).await()
                    }

                    // Save to cache
                    ProfileCache.saveToCache(
                        context,
                        name,
                        dob,
                        gender,
                        selectedLanguage,
                        avatarName ?: avatars.entries.random().key
                    )
                } catch (e: Exception) {
                    // If network fails, we already have cached values
                    Toast.makeText(context, "Using cached data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.Profile)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(2.dp, color = MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = "Profile Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "My Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                readOnly = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Name")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = user?.email ?: "",
                onValueChange = {},
                label = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                readOnly = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Gender",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Gender",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row {
                    RadioButton(
                        selected = gender == "Male",
                        onClick = { gender = "Male" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text("Male", modifier = Modifier.padding(end = 16.dp, start = 4.dp), color = MaterialTheme.colorScheme.onBackground)

                    RadioButton(
                        selected = gender == "Female",
                        onClick = { gender = "Female" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text("Female", modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                }
            }

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of Birth", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                readOnly = true,
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Date of Birth")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = {},
                    label = { Text("Language", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.Favorite, contentDescription = "Language")
                    },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(250.dp)
                        .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = language,
                                    color = if (language == selectedLanguage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    fontWeight = if (language == selectedLanguage) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedLanguage = language
                                expanded = false
                                user?.let {
                                    coroutineScope.launch {
                                        try {
                                            Firebase.firestore
                                                .collection("users")
                                                .document(it.uid)
                                                .update("language", selectedLanguage)
                                                .await()

                                            // Update cache
                                            ProfileCache.saveToCache(
                                                context,
                                                name,
                                                dob,
                                                gender,
                                                selectedLanguage,
                                                ProfileCache.avatar
                                            )

                                            Toast.makeText(context, "Language updated to $language", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Failed to update language", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    user?.let {
                        coroutineScope.launch {
                            try {
                                Firebase.firestore
                                    .collection("users")
                                    .document(it.uid)
                                    .update(
                                        mapOf(
                                            "name" to name,
                                            "dob" to dob,
                                            "gender" to gender,
                                            "language" to selectedLanguage
                                        )
                                    )
                                    .await()

                                // Update cache
                                ProfileCache.saveToCache(
                                    context,
                                    name,
                                    dob,
                                    gender,
                                    selectedLanguage,
                                    ProfileCache.avatar
                                )

                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}