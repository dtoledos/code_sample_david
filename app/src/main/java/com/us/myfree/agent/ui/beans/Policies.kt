package com.us.myfree.agent.ui.beans

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


class Policies
{
    var carrierName: String? = null
    var policyType: String? = null
    var holderName: String? = null
    var policyNumber: String? = null
    var expirationDate: Timestamp? = null

    @DocumentId
    val documentId: String? =null

    constructor(carrierName: String?, policyType: String?, holderName: String?) {
        this.carrierName = carrierName
        this.policyType = policyType
        this.holderName = holderName
    }
    //Add this
    constructor() {}
}

