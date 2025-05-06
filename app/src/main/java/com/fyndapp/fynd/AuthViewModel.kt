package com.fyndapp.fynd

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val _genderSelectionRequired = mutableStateOf(false)
    val genderSelectionRequired: State<Boolean> = _genderSelectionRequired

    init {
        auth.currentUser?.let { user ->
            _authState.value = AuthState(user = user)
            viewModelScope.launch {
                checkGenderSelection(user.uid)
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun handleSignInResult(data: Intent?) {
        _authState.value = AuthState(isLoading = true)
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                viewModelScope.launch {
                    signInWithGoogle(token)
                }
            }
        } catch (e: ApiException) {
            _authState.value = AuthState(error = "Google sign-in failed: ${e.statusCode}")
        }
    }

    private suspend fun signInWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            authResult.user?.let { user ->
                // Check if user exists in Firestore
                val userDoc = db.collection("users").document(user.uid).get().await()
                if (!userDoc.exists()) {
                    // Create user document if it does not exist
                    val userData = hashMapOf(
                        "name" to user.displayName,
                        "email" to user.email,
                        "gender" to null,
                        "avatar" to "default" // Default avatar initially
                    )
                    db.collection("users").document(user.uid).set(userData).await()
                    // Assign a random avatar
                    assignRandomAvatar(user.uid, null)
                }
                _genderSelectionRequired.value = userDoc.getString("gender") == null
                _authState.value = AuthState(user = user)
            }
        } catch (e: Exception) {
            _authState.value = AuthState(error = e.message)
        }
    }

    private suspend fun assignRandomAvatar(userId: String, gender: String?) {
        val avatar = when (gender) {
            "Male" -> listOf("male1", "male2", "male3", "male4", "male5", "male6").random()
            "Female" -> listOf("female1", "female2", "female3", "female4", "female5", "female6").random()
            else -> listOf("male1", "male2", "male3", "male4", "male5", "male6", "female1", "female2", "female3", "female4", "female5", "female6", "default").random()
        }

        db.collection("users")
            .document(userId)
            .update("avatar", avatar)
            .await()
    }

    suspend fun deleteAccount(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            try {
                user.delete().await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.localizedMessage ?: "Error deleting account")
            }
        } else {
            onFailure("No user is logged in")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkGenderSelection(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                _genderSelectionRequired.value = document.getString("gender").isNullOrBlank()
            }
            .addOnFailureListener {
                _genderSelectionRequired.value = true
            }
    }

    suspend fun saveUserDetails(
        userId: String,
        name: String,
        dob: String,
        gender: String
    ): Boolean {
        return try {
            val updates = hashMapOf<String, Any>(
                "name" to name,
                "dob" to dob,
                "gender" to gender
            )
            db.collection("users").document(userId).update(updates).await()
            assignRandomAvatar(userId, gender)
            _genderSelectionRequired.value = false
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserLanguage(userId: String, language: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .update("language", language)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserAvatar(userId: String, avatar: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .update("avatar", avatar)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState()
    }
}