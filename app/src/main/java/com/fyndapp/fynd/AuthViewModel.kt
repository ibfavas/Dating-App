package com.fyndapp.fynd

import android.content.Context
import android.content.Intent
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
                        "gender" to null
                    )
                    db.collection("users").document(user.uid).set(userData).await()
                }
                _genderSelectionRequired.value = userDoc.getString("gender") == null
                _authState.value = AuthState(user = user)
            }
        } catch (e: Exception) {
            _authState.value = AuthState(error = e.message)
        }
    }

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
            val userData = hashMapOf(
                "name" to name,
                "dob" to dob,
                "gender" to gender
            )
            db.collection("users").document(userId).set(userData).await()
            _genderSelectionRequired.value = false
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