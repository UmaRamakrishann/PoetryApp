package com.udacity.capstone.poetry.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.capstone.poetry.R
import com.udacity.capstone.poetry.databinding.ActivityMainBinding
import com.udacity.capstone.poetry.util.PoetryConstants.WELCOME_IMAGE_URL

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            navigatetoBrowsePoems()
            finish()
        }

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(
                this,
                R.layout.activity_main
            )

        Glide.with(this)
            .load(WELCOME_IMAGE_URL)
            .placeholder(R.drawable.ic_free_poem_vector)
            .into(binding.welcomeImage);

        binding.authButton.setOnClickListener { launchSignInFlow() }
    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                // <a href="https://www.vecteezy.com/free-vector/poem">Poem Vectors by Vecteezy</a>
                .setLogo(R.drawable.ic_free_poem_vector)
                .setTheme(R.style.Theme_PoetryApp)
                .build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                navigatetoBrowsePoems()
                finish()
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun navigatetoBrowsePoems() {
        val intent = Intent(this, PoetryActivity::class.java)
        startActivity(intent)
    }
}