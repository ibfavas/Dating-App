package com.fyndapp.fynd

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fyndapp.fynd.other.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.collectAsState().value
    val genderSelectionRequired = authViewModel.genderSelectionRequired.value
    val context = LocalContext.current

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authViewModel.handleSignInResult(result.data)
    }

    LaunchedEffect(authState.user, genderSelectionRequired) {
        authState.user?.let { user ->
            if (genderSelectionRequired) {
                navController.navigate(Screens.SelectGender.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (authState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp) // Moved content a bit upward
            ) {
                // Illustration Image
                Spacer(modifier = Modifier.height(100.dp))
                Image(
                    painter = painterResource(id = R.drawable.dating_illustration),
                    contentDescription = "Dating Illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp) // Slightly reduced height
                )

                Text(
                    text = "FYND",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Swipe less. Connect more. Fynd the one.",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Fynd is a  modern  dating  experience designed to  spark genuine connections.  From meaningful chats to real-life chemistry --- Fynd makes finding love effortless and exciting.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val signInClient = authViewModel.getGoogleSignInClient(context)
                        signInLauncher.launch(signInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo), // Add your Google logo to res/drawable
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(text = "Sign in with Google")
                    }
                }

            }

            // Disclaimer text at the bottom
            ClickableText(
                text = buildAnnotatedString {
                    append("By tapping, you agree to our ")

                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Terms of Use")
                    }
                    pop()

                    append(" and ")

                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    pop()

                    append(". Your information is 100% safe and secure.")
                },
                onClick = { offset ->
                    val annotations = buildAnnotatedString {
                        pushStringAnnotation("TERMS", "terms")
                        pushStringAnnotation("PRIVACY", "privacy")
                    }.getStringAnnotations(start = offset, end = offset)

                    annotations.firstOrNull()?.let { annotation ->
                        when (annotation.tag) {
                            "TERMS" -> {
                                // TODO: Navigate or open terms URL
                                // Example: navController.navigate(Screens.Terms.route)
                            }
                            "PRIVACY" -> {
                                // TODO: Navigate or open privacy URL
                                // Example: navController.navigate(Screens.Privacy.route)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}