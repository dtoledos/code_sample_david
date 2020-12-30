package com.us.myfree.agent.ui.beans

import com.google.firebase.Timestamp


class ClaimMain
{
    var claimType: String? = null
    var date: Timestamp? = null
    var images: List<ClaimObj> = ArrayList()
    var videos: List<ClaimObj> = ArrayList()
    var user: UserObj?=UserObj()
        //private set
    //var images: MutableList<MutableMap<String?, ClaimObj?>?>? = null
    //Add this
    constructor() {}
}

