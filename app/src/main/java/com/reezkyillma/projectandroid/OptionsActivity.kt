package com.reezkyillma.projectandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth

class OptionsActivity : AppCompatActivity() {

    internal lateinit var logout: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Options"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        logout = findViewById(R.id.logout)

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@OptionsActivity, StartActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }
}

