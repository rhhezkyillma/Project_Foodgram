package com.reezkyillma.projectandroid.Model

class Notification {
    var userid: String? = null
    var text: String? = null
    var postid: String? = null
    var isIspost: Boolean = false

    constructor(userid: String, text: String, postid: String, ispost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        this.isIspost = ispost
    }

    constructor() {}
}
