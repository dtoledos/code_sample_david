package com.us.myfree.agent.ui.beans

import com.google.firebase.Timestamp


class Claim
{
    var claimID: String? = null
    //  private set
    var claimType: String? = null
    //  private set
    var claimImage: String? = null
    //  private set
    var wasOrdered: Boolean? = null
    //private set
    var date: Timestamp? = null
        //private set

    constructor(claimID: String?, claimType: String?, claimImage: String?,wasOrdered: Boolean?) {
        this.claimID = claimID
        this.claimType = claimType
        this.claimImage = claimImage
        this.wasOrdered= wasOrdered
    }
    //Add this
    constructor() {}
}

