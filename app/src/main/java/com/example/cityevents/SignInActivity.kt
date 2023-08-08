package com.example.cityevents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cityevents.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            .getBoolean(IS_LOGGED_TAG, false)

        if (isLoggedIn) startMainActivity()

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            auth = Firebase.auth
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    fbAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.e("MyLog", e.toString())
            }
        }
        binding.bSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val sigInClient = getClient()
        launcher.launch(sigInClient.signInIntent)
    }

    private fun fbAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("MyLog", "Google sign in done")
                // После успешной авторизации сохраняем информацию в SharedPreferences
                getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(IS_LOGGED_TAG, true)
                    .apply()

                startMainActivity()
            } else {
                Log.d("MyLog", "Google sign in error")
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val APP_NAME = "MY_APP"
        const val IS_LOGGED_TAG = "IS_LOGGED_IN"
    }
}