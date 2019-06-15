package com.reezkyillma.projectandroid.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.PostAdapter
import com.reezkyillma.projectandroid.Model.Post
import com.reezkyillma.projectandroid.R

import java.util.ArrayList

import android.content.Context.MODE_PRIVATE

class PostDetailFragment : Fragment() {

    internal var postid: String? = null

    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val prefs = context!!.getSharedPreferences("PREFS", MODE_PRIVATE)
        postid = prefs.getString("postid", "none")

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager

        postList = ArrayList()
        postAdapter = PostAdapter(context, postList)
        recyclerView!!.adapter = postAdapter

        readPost()

        return view
    }

    private fun readPost() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid!!)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                val post = dataSnapshot.getValue(Post::class.java)
                post?.let { postList!!.add(it) }

                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}