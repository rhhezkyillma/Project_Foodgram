package com.reezkyillma.projectandroid.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.PostAdapter
import com.reezkyillma.projectandroid.Adapter.StoryAdapter
import com.reezkyillma.projectandroid.Model.Post
import com.reezkyillma.projectandroid.Model.Story
import com.reezkyillma.projectandroid.PostActivity
import com.reezkyillma.projectandroid.R
import kotlinx.android.synthetic.main.fragment_home.*

import java.util.ArrayList

class HomeFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null

    private var recyclerView_story: RecyclerView? = null
    private var storyAdapter: StoryAdapter? = null
    private var storyList: MutableList<Story>? = null

    private var followingList: MutableList<String>? = null

    internal lateinit var progress_circular: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = mLayoutManager
        postList = ArrayList()
        postAdapter = PostAdapter(context, postList)
        recyclerView!!.adapter = postAdapter

        recyclerView_story = view.findViewById(R.id.recycler_view_story)
        recyclerView_story!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)
        recyclerView_story!!.layoutManager = linearLayoutManager
        storyList = ArrayList()
        storyAdapter = StoryAdapter(context, storyList)
        recyclerView_story!!.adapter = storyAdapter

        progress_circular = view.findViewById(R.id.progress_circular)

//        message.setOnClickListener {
//            startActivity(Intent(this@HomeFragment, MessageActivity::class.java))
//        }

        checkFollowing()

        return view
    }

    private fun checkFollowing() {
        followingList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("following")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followingList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { followingList!!.add(it) }
                }

                readPosts()
                readStory()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun readPosts() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    for (id in followingList!!) {
                        if (post!!.publisher == id) {
                            postList!!.add(post)
                        }
                    }
                }

                postAdapter!!.notifyDataSetChanged()
                progress_circular.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun readStory() {
        val reference = FirebaseDatabase.getInstance().getReference("Story")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val timecurrent = System.currentTimeMillis()
                storyList!!.clear()
                storyList!!.add(Story("", 0, 0, "",
                        FirebaseAuth.getInstance().currentUser!!.uid))
                for (id in followingList!!) {
                    var countStory = 0
                    var story: Story? = null
                    for (snapshot in dataSnapshot.child(id).children) {
                        story = snapshot.getValue(Story::class.java)
                        if (timecurrent > story!!.timestart && timecurrent < story.timeend) {
                            countStory++
                        }
                    }
                    if (countStory > 0) {
                        story?.let { storyList!!.add(it) }
                    }
                }

                storyAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
