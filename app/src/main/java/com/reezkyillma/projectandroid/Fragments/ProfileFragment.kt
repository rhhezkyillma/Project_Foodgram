package com.reezkyillma.projectandroid.Fragments


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.MyFotosAdapter
import com.reezkyillma.projectandroid.EditProfileActivity
import com.reezkyillma.projectandroid.FollowersActivity
import com.reezkyillma.projectandroid.Model.Post
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.OptionsActivity
import com.reezkyillma.projectandroid.R

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import android.content.Context.MODE_PRIVATE
import kotlinx.android.synthetic.main.nav_header_main.*

class ProfileFragment : Fragment() {

    internal lateinit var image_profile: ImageView
    internal lateinit var options: ImageView
    internal lateinit var posts: TextView
    internal lateinit var followers: TextView
    internal lateinit var following: TextView
    internal lateinit var fullname: TextView
    internal lateinit var bio: TextView
    internal lateinit var username: TextView
    internal lateinit var edit_profile: Button

    private var mySaves: MutableList<String>? = null

    internal var firebaseUser: FirebaseUser? = null
    internal var profileid: String? = null

    private var recyclerView: RecyclerView? = null
    private var myFotosAdapter: MyFotosAdapter? = null
    private var postList: MutableList<Post>? = null

    private var recyclerView_saves: RecyclerView? = null
    private var myFotosAdapter_saves: MyFotosAdapter? = null
    private var postList_saves: MutableList<Post>? = null

    internal lateinit var my_fotos: ImageButton
    internal lateinit var saved_fotos: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val prefs = context!!.getSharedPreferences("PREFS", MODE_PRIVATE)
        profileid = prefs.getString("profileid", "none")

        image_profile = view.findViewById(R.id.image_profile)
        posts = view.findViewById(R.id.posts)
        followers = view.findViewById(R.id.followers)
        following = view.findViewById(R.id.following)
        fullname = view.findViewById(R.id.fullname)
        bio = view.findViewById(R.id.bio)
        edit_profile = view.findViewById(R.id.edit_profile)
        username = view.findViewById(R.id.username)
        my_fotos = view.findViewById(R.id.my_fotos)
        saved_fotos = view.findViewById(R.id.saved_fotos)
//        options = view.findViewById(R.id.options)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = GridLayoutManager(context, 3)
        recyclerView!!.layoutManager = mLayoutManager
        postList = ArrayList()
        myFotosAdapter = MyFotosAdapter(context, postList)
        recyclerView!!.adapter = myFotosAdapter

        recyclerView_saves = view.findViewById(R.id.recycler_view_save)
        recyclerView_saves!!.setHasFixedSize(true)
        val mLayoutManagers = GridLayoutManager(context, 3)
        recyclerView_saves!!.layoutManager = mLayoutManagers
        postList_saves = ArrayList()
        myFotosAdapter_saves = MyFotosAdapter(context, postList_saves)
        recyclerView_saves!!.adapter = myFotosAdapter_saves

        recyclerView!!.visibility = View.VISIBLE
        recyclerView_saves!!.visibility = View.GONE

        userInfo()
        getFollowers()
        getNrPosts()
        myFotos()
        mySaves()

        if (profileid == firebaseUser!!.uid) {
            edit_profile.text = "Edit Profile"
        } else {
            checkFollow()
            saved_fotos.visibility = View.GONE
        }

        edit_profile.setOnClickListener {
            val btn = edit_profile.text.toString()

            if (btn == "Edit Profile") {

                startActivity(Intent(context, EditProfileActivity::class.java))

            } else if (btn == "follow") {

                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                        .child("following").child(profileid!!).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileid!!)
                        .child("followers").child(firebaseUser!!.uid).setValue(true)
                addNotification()
            } else if (btn == "following") {

                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                        .child("following").child(profileid!!).removeValue()
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileid!!)
                        .child("followers").child(firebaseUser!!.uid).removeValue()

            }
        }

//        options.setOnClickListener { startActivity(Intent(context, OptionsActivity::class.java)) }

        my_fotos.setOnClickListener {
            recyclerView!!.visibility = View.VISIBLE
            recyclerView_saves!!.visibility = View.GONE
        }

        saved_fotos.setOnClickListener {
            recyclerView!!.visibility = View.GONE
            recyclerView_saves!!.visibility = View.VISIBLE
        }


        followers.setOnClickListener {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("id", profileid)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }

        following.setOnClickListener {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("id", profileid)
            intent.putExtra("title", "following")
            startActivity(intent)
        }

        return view
    }

    private fun addNotification() {
        val reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid!!)

        val hashMap = HashMap<String, Any>()
        hashMap["userid"] = firebaseUser!!.uid
        hashMap["text"] = "started following you"
        hashMap["postid"] = ""
        hashMap["ispost"] = false

        reference.push().setValue(hashMap)
    }

    private fun userInfo() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (context == null) {
                    return
                }
                val user = dataSnapshot.getValue(User::class.java)

                Glide.with(context!!).load(user!!.imageurl).placeholder(R.drawable.ic_launcher_foreground).into(image_profile)

                username.text = user.username
                fullname.text = user.fullname
                bio.text = user.bio

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun checkFollow() {
        val reference = FirebaseDatabase.getInstance().reference
                .child("Follow").child(firebaseUser!!.uid).child("following")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(profileid!!).exists()) {
                    edit_profile.text = "following"
                } else {
                    edit_profile.text = "follow"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid!!).child("followers")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followers.text = "" + dataSnapshot.childrenCount
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid!!).child("following")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                following.text = "" + dataSnapshot.childrenCount
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getNrPosts() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i = 0
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post!!.publisher == profileid) {
                        i++
                    }
                }
                posts.text = "" + i
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun myFotos() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post!!.publisher == profileid) {
                        postList!!.add(post)
                    }
                }
                Collections.reverse(postList)
                myFotosAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun mySaves() {
        mySaves = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { mySaves!!.add(it) }
                }
                readSaves()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun readSaves() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList_saves!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in mySaves!!) {
                        if (post!!.postid == id) {
                            postList_saves!!.add(post)
                        }
                    }
                }
                myFotosAdapter_saves!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
