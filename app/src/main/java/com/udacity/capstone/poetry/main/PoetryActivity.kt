package com.udacity.capstone.poetry.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.udacity.capstone.poetry.R
import kotlinx.android.synthetic.main.activity_poetry.nav_host_fragment

class PoetryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poetry)
    }

}