package com.reezkyillma.projectandroid.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reezkyillma.projectandroid.Adapter.NotificationAdapter
import com.reezkyillma.projectandroid.Model.Notification
import com.reezkyillma.projectandroid.R

import java.util.ArrayList
import java.util.Collections


class NotificationFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationList: MutableList<Notification>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(context, notificationList)
        recyclerView!!.adapter = notificationAdapter

        readNotifications()

        return view
    }

    private fun readNotifications() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser!!.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notificationList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val notification = snapshot.getValue(Notification::class.java)
                    notification?.let { notificationList!!.add(it) }
                }

                Collections.reverse(notificationList)
                notificationAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
