package com.reezkyillma.projectandroid


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.UserAdapter
import com.reezkyillma.projectandroid.Model.User

import java.util.ArrayList

class FollowersActivity : AppCompatActivity() {

    internal lateinit var id: String
    internal lateinit var title: String

    private var idList: MutableList<String>? = null

    internal lateinit var recyclerView: RecyclerView
    internal lateinit var userAdapter: UserAdapter
    internal lateinit var userList: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)

        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter = UserAdapter(this, userList, false)
        recyclerView.adapter = userAdapter

        idList = ArrayList()


        when (title) {
            "likes" -> getLikes()
            "following" -> getFollowing()
            "followers" -> getFollowers()
            "views" -> getViews()
        }

    }

    private fun getViews() {
        val reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(id).child(intent.getStringExtra("storyid")).child("views")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { idList!!.add(it) }
                }
                showUsers()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { idList!!.add(it) }
                }
                showUsers()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getFollowing() {
        val reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { idList!!.add(it) }
                }
                showUsers()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getLikes() {
        val reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { idList!!.add(it) }
                }
                showUsers()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun showUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    for (id in idList!!) {
                        if (user!!.id == id) {
                            userList.add(user)
                        }
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
