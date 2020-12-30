package com.us.myfree.agent.ui.beans

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


class ImageData
{

    var mediaType: String? = null
    var mediaSubject: String? = null
    var url: String? = null
    var date: Timestamp? = null
    var location :GeoPoint?=null


    //Add this
    constructor() {}
}

