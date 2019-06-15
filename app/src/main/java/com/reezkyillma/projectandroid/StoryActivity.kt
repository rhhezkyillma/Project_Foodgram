package com.reezkyillma.projectandroid


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Model.Story
import com.reezkyillma.projectandroid.Model.User

import java.util.ArrayList

import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    internal var counter = 0
    internal var pressTime = 0L
    internal var limit = 500L

    internal lateinit var storiesProgressView: StoriesProgressView
    internal lateinit var image: ImageView
    internal lateinit var story_photo: ImageView
    internal lateinit var story_username: TextView

    //
    internal lateinit var r_seen: LinearLayout
    internal lateinit var seen_number: TextView
    internal lateinit var story_delete: ImageView
    //

    internal lateinit var images: MutableList<String>
    internal lateinit var storyids: MutableList<String>
    internal lateinit var userid: String


    private val onTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        storiesProgressView = findViewById(R.id.stories)
        image = findViewById(R.id.image)
        story_photo = findViewById(R.id.story_photo)
        story_username = findViewById(R.id.story_username)

        //
        r_seen = findViewById(R.id.r_seen)
        seen_number = findViewById(R.id.seen_number)
        story_delete = findViewById(R.id.story_delete)

        r_seen.visibility = View.GONE
        story_delete.visibility = View.GONE
        //

        userid = intent.getStringExtra("userid")

        //
        if (userid == FirebaseAuth.getInstance().currentUser!!.uid) {
            r_seen.visibility = View.VISIBLE
            story_delete.visibility = View.VISIBLE
        }
        //

        getStories(userid)
        userInfo(userid)

        val reverse = findViewById<View>(R.id.reverse)
        reverse.setOnClickListener { storiesProgressView.reverse() }
        reverse.setOnTouchListener(onTouchListener)


        val skip = findViewById<View>(R.id.skip)
        skip.setOnClickListener { storiesProgressView.skip() }
        skip.setOnTouchListener(onTouchListener)

        //
        r_seen.setOnClickListener {
            val intent = Intent(this@StoryActivity, FollowersActivity::class.java)
            intent.putExtra("id", userid)
            intent.putExtra("storyid", storyids[counter])
            intent.putExtra("title", "views")
            startActivity(intent)
        }

        story_delete.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("Story")
                    .child(userid).child(storyids[counter])
            reference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@StoryActivity, "Deleted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        //

    }

    override fun onNext() {
        Glide.with(applicationContext).load(images[++counter]).into(image)
        //
        addView(storyids[counter])
        seenNumber(storyids[counter])
        //
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        Glide.with(applicationContext).load(images[--counter]).into(image)
        //
        seenNumber(storyids[counter])
        //
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        storiesProgressView.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        storiesProgressView.pause()
        super.onPause()
    }

    override fun onResume() {
        storiesProgressView.resume()
        super.onResume()
    }

    private fun getStories(userid: String) {
        images = ArrayList()
        storyids = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                images.clear()
                storyids.clear()
                for (snapshot in dataSnapshot.children) {
                    val story = snapshot.getValue(Story::class.java)
                    val timecurrent = System.currentTimeMillis()
                    if (timecurrent > story!!.timestart && timecurrent < story.timeend) {
                        story.imageurl?.let { images.add(it) }
                        story.storyid?.let { storyids.add(it) }
                    }
                }

                storiesProgressView.setStoriesCount(images.size)
                storiesProgressView.setStoryDuration(5000L)
                storiesProgressView.setStoriesListener(this@StoryActivity)
                storiesProgressView.startStories(counter)

                Glide.with(applicationContext).load(images[counter]).into(image)
                //
                addView(storyids[counter])
                seenNumber(storyids[counter])
                //
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun userInfo(userid: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Glide.with(applicationContext).load(user!!.imageurl).into(story_photo)
                story_username.text = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    //
    private fun addView(storyid: String) {
        FirebaseDatabase.getInstance().reference.child("Story").child(userid)
                .child(storyid).child("views").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(true)
    }

    private fun seenNumber(storyid: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid).child(storyid).child("views")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                seen_number.text = "" + dataSnapshot.childrenCount
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
    //
}
