package com.reezkyillma.projectandroid

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    internal lateinit var email: EditText
    internal lateinit var password: EditText
    internal lateinit var login: Button
    internal lateinit var txt_signup: TextView

    internal lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        txt_signup = findViewById(R.id.txt_signup)

        auth = FirebaseAuth.getInstance()

        txt_signup.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }

        login.setOnClickListener {
            val pd = ProgressDialog(this@LoginActivity)
            pd.setMessage("Please wait...")
            pd.show()

            val str_email = email.text.toString()
            val str_password = password.text.toString()

            if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                Toast.makeText(this@LoginActivity, "All fields are required!", Toast.LENGTH_SHORT).show()
            } else {

                auth.signInWithEmailAndPassword(str_email, str_password)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {

                                val reference = FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(auth.currentUser!!.uid)

                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        pd.dismiss()
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        pd.dismiss()
                                    }
                                })
                            } else {
                                pd.dismiss()
                                Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }
}
