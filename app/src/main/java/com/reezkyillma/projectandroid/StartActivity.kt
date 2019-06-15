package com.reezkyillma.projectandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class StartActivity : AppCompatActivity() {


    internal lateinit var login: Button
    internal lateinit var register: Button

    internal var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        login = findViewById(R.id.login)
        register = findViewById(R.id.register)

        login.setOnClickListener { startActivity(Intent(this@StartActivity, LoginActivity::class.java)) }

        register.setOnClickListener { startActivity(Intent(this@StartActivity, RegisterActivity::class.java)) }

    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        //check if user is null
        if (firebaseUser != null) {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
