package com.us.myfree.agent.ui.beans

import com.google.firebase.firestore.DocumentId


class UserBean2 {
    var name: String? = null
    //  private set
    var email: String? = null
    //  private set
    var address: String? = null
    //  private set
    var city:String? = null
    //  private set
    var country:String? = null
    //private set
    var phoneNumber:String? = null
        //private set
    var state:String? = null
    var policies = ArrayList<Policies>()
        private set

    @DocumentId
    var documentId: String? =null


    constructor(name: String?, password: String?, email: String?) {
        this.name = name
        this.email = email
        this.address = address
    }

    //Add this
    constructor() {}

}