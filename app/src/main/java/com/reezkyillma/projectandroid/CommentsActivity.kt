package com.reezkyillma.projectandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.CommentAdapter
import com.reezkyillma.projectandroid.Model.Comment
import com.reezkyillma.projectandroid.Model.User

import java.util.ArrayList
import java.util.HashMap

class CommentsActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<Comment>? = null

    internal lateinit var addcomment: EditText
    internal lateinit var image_profile: ImageView
    internal lateinit var post: TextView

    internal lateinit var postid: String
    internal lateinit var publisherid: String

    internal var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Comments"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val intent = intent
        postid = intent.getStringExtra("postid")
        publisherid = intent.getStringExtra("publisherid")

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = mLayoutManager
        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList, postid)
        recyclerView!!.adapter = commentAdapter

        post = findViewById(R.id.post)
        addcomment = findViewById(R.id.add_comment)
        image_profile = findViewById(R.id.image_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        post.setOnClickListener {
            if (addcomment.text.toString() == "") {
                Toast.makeText(this@CommentsActivity, "You can't send empty message", Toast.LENGTH_SHORT).show()
            } else {
                addComment()
            }
        }

        getImage()
        readComments()

    }

    private fun addComment() {

        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid)

        val commentid = reference.push().key.toString()

        val hashMap = HashMap<String, Any>()
        hashMap["comment"] = addcomment.text.toString()
        hashMap["publisher"] = firebaseUser!!.uid
        hashMap["commentid"] = commentid

        reference.child(commentid!!).setValue(hashMap)
        addNotification()
        addcomment.setText("")

    }

    private fun addNotification() {
        val reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid)

        val hashMap = HashMap<String, Any>()
        hashMap["userid"] = firebaseUser!!.uid
        hashMap["text"] = "commented: " + addcomment.text.toString()
        hashMap["postid"] = postid
        hashMap["ispost"] = true

        reference.push().setValue(hashMap)
    }

    private fun getImage() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Glide.with(applicationContext).load(user!!.imageurl).into(image_profile)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun readComments() {
        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(Comment::class.java)
                    comment?.let { commentList!!.add(it) }
                }

                commentAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}