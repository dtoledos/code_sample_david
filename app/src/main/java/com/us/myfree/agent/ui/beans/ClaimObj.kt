package com.us.myfree.agent.ui.beans

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

class ClaimObj
{
    var date: Timestamp? = null
    var mediaSubject: String? = null
    var mediaType: String? = null
    var url: String? = null
    var location : GeoPoint?=null
    var dateString:String?=null

    //Add this
    constructor() {}
}