package com.us.myfree.agent.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import com.us.myfree.agent.ui.beans.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.us.myfree.agent.ui.act.BaseActivity
import kotlinx.coroutines.*

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val db = FirebaseFirestore.getInstance()
    val _context=context

    override fun doWork(): Result {
        //createNotification("Background Task", "This notification is generated by workManager")

        val claimId =  inputData.getString("claimId")
        val claimType =  inputData.getString("claimType")
        val userLogged =  inputData.getString("userLogged")
        val nologged =  inputData.getBoolean("noLogged",false)
//        /var images=   inputData.keyValueMap("images")
        Log.i("Tag",""+imagesArray);

        uploadFiles(claimId,claimType,userLogged,nologged)
        return Result.success()
    }

    fun createNotification(title: String, description: String) {


    }

    companion object {
        private lateinit var imagesArray: ArrayList<ClaimObj>
        private lateinit var videoArray: ArrayList<ClaimObj>

        fun setData(arrayLstPhoto: ArrayList<ClaimObj>, arrayLstVideo: ArrayList<ClaimObj>) {
            imagesArray = arrayLstPhoto
            videoArray = arrayLstVideo
            Log.i("Tag","")
        }
    }


    fun uploadFiles(claimId:String?,claimType:String?,userLogged:String?,nologged:Boolean)
    {
        val claimImageObjLst = mutableListOf<ClaimObj>()
        val claimVideoObjLst = mutableListOf<ClaimObj>()
        var contador=0

        GlobalScope.launch {

            imagesArray.forEach {
                c ->

                val urlPhoto= async(Dispatchers.IO)
                {
                    uploadPhoto(c.url,c.dateString.toString(),c.location)
                }


                /*async(Dispatchers.Main)
                {
                    refreshProgress(count++)
                }*/


                var claimObj: ClaimObj? =ClaimObj()
                if (claimObj != null) {
                    claimObj.location=c.location
                    claimObj.mediaSubject=c.mediaSubject
                    claimObj.mediaType=c.mediaType
                    claimObj.date=c.date
                    claimObj.dateString=c.dateString
                    claimObj.url=urlPhoto.await()
                    claimImageObjLst +=claimObj
                }

                if (!nologged) {
                    if (claimImageObjLst.size>0 && claimImageObjLst.size==1)
                    {
                        saveDataFirestore(userLogged,claimType.toString(),claimId.toString(),claimImageObjLst[0].url.toString())
                    }
                }


                if (imagesArray.size==claimImageObjLst.size)
                {
                    saveData(userLogged.toString(),claimId.toString(),claimType, claimImageObjLst  as ArrayList<ClaimObj>, claimVideoObjLst as ArrayList<ClaimObj>,nologged)
                        // openReportAct(claimId)
                }



            }


        }


        GlobalScope.launch {
            videoArray.forEach { c ->
                val urlVideo: Deferred<String> = async(Dispatchers.IO)
                {
                    uploadVideo(c.url,c.dateString.toString(),c.location)
                }

                /*async(Dispatchers.Main)
                {
                    refreshProgress(count++)
                }*/


                var claimObjVideo: ClaimObj? =ClaimObj()
                if (claimObjVideo != null) {
                    claimObjVideo.location=c.location
                    claimObjVideo.mediaSubject=c.mediaSubject
                    claimObjVideo.mediaType=c.mediaType
                    claimObjVideo.date=c.date
                    claimObjVideo.dateString=c.dateString
                    claimObjVideo.url=urlVideo.await()
                    claimVideoObjLst +=claimObjVideo
                }

            }

        }

    }



    suspend fun uploadPhoto(urlFile:String?,dateString:String,location: GeoPoint?): String {
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var uri = Uri.fromFile(File(urlFile))
        val fileRef = storageRef.child("ImagesTestDavid/${uri.lastPathSegment}")

        var metadata = storageMetadata {
            contentType = "image/jpeg"
            if (location!=null)
            {
                setCustomMetadata("latitude", location!!.latitude.toString())
                setCustomMetadata("longitude", location!!.longitude.toString())
            }
            setCustomMetadata("date", dateString)
            setCustomMetadata("creatorsUID", uuid)
        }

        fileRef.putFile(uri, metadata).await()
        val url =  fileRef.downloadUrl.await().toString()
        return url
    }

    suspend fun uploadVideo(urlFile:String?,dateString:String,location: GeoPoint?): String {
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var uri = Uri.fromFile(File(urlFile))
        val fileRef = storageRef.child("VideoTestDavid/${uri.lastPathSegment}")

        var metadata = storageMetadata {
            contentType = "video/mp4"
            if (location!=null)
            {
                setCustomMetadata("latitude", location!!.latitude.toString())
                setCustomMetadata("longitude", location!!.longitude.toString())
            }
            setCustomMetadata("date", dateString)
            setCustomMetadata("creatorsUID", uuid)
        }

        fileRef.putFile(uri, metadata).await()
        val url =  fileRef.downloadUrl.await().toString()
        return url
    }


    private fun saveDataFirestore(userID: String?, claimType:String?, claimID:String, claimUrlImage:String)
    {

        val uid= userID.toString()

        var timeBirthDay: Timestamp? = null
        val calendar: Calendar = GregorianCalendar()
        timeBirthDay = Timestamp(calendar.time)


        var claim: Claim? = Claim()
        if (claim != null) {
            claim.claimID=claimID
            claim.claimType=claimType.toString()
            claim.claimImage=claimUrlImage
            claim.wasOrdered=false
            claim.date=timeBirthDay
        }

        if (claim != null) {
            db.collection("users").document(uid).collection("claims").document(claimID).set(claim)
                    //db.collection("users").document(uid).collection("claims").add(claim)
                    .addOnSuccessListener {documentReference ->
                        Log.d("TAG", "Good")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        //typeMessage(BaseActivity._OtherError)
                    }
        }

    }

    private fun saveData(userLogged: String, clamId: String, claimType:String?, imgLst:ArrayList<ClaimObj>, videoLst:ArrayList<ClaimObj>,nologged:Boolean)
    {

        val sharedPreference =  _context.getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
        val userPhone=sharedPreference?.getString(BaseActivity._UserPhone,"")

        if (nologged)
        {

            var userObj2: UserBean2? = UserBean2()
            if (userObj2!=null)
            {
                userObj2.address=""
                userObj2.city=""
                userObj2.country=""
                userObj2.email=""
                userObj2.name =""
                userObj2.phoneNumber=userPhone
                userObj2.state=""
                userObj2.documentId=userLogged
                saveDataFirestorClaimMain(claimType.toString(),clamId,userObj2,imgLst,videoLst)
            }

        }
        else
        {
            var userData: UserBean2?=null
            try {

                val docRef = db.collection("users").document(userLogged)
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    Log.i("tag","");
                    userData = documentSnapshot.toObject(UserBean2::class.java)
                    saveDataFirestorClaimMain(claimType.toString(),clamId,userData,imgLst,videoLst)

                    Log.i("tag","");
                }


            }catch (ex: java.lang.Exception)
            {
                Log.e("tag",ex.message)
                //progressBar?.visibility = View.GONE
            }
        }

    }

    private fun saveDataFirestorClaimMain(claimType:String, clamId:String, userData:UserBean2?, imgLst:ArrayList<ClaimObj>, videoLst:ArrayList<ClaimObj>)
    {

        var timeBirthDay: Timestamp? = null
        val calendar: Calendar = GregorianCalendar()
        timeBirthDay = Timestamp(calendar.time)


        var userObj2: UserObj = UserObj()
        if (userObj2!=null)
        {
            if (userData!=null)
            {
                userObj2.address=userData.address
                userObj2.city=userData.city
                userObj2.country=userData.country
                userObj2.email=userData.email
                userObj2.name =userData.name
                userObj2.phoneNumber=userData.phoneNumber
                userObj2.state=userData.state
                userObj2.userID=userData.documentId
            }
        }


        /*imgLst.forEach { c ->
            Log.e("Tag","imgLst: "+c.mediaSubject)
        }*/

        var sortedList = imgLst.sortedWith(compareBy({ it.date }))

        /*sortedList.forEach { c ->
            Log.e("Tag","sortedList: "+c.mediaSubject)
        }*/


        var claim: ClaimMain? = ClaimMain()
        if (claim != null) {
            claim.claimType=claimType
            claim.date=timeBirthDay
            claim.images= sortedList
            claim.videos=videoLst
            claim.user=userObj2
        }



        if (claim != null) {
            db.collection("claims").document(clamId).set(claim)
                    .addOnSuccessListener {documentReference ->
                        Log.d("TAG", "Good")
                        //hideProgressBar()
                        //openReportAct(clamId)
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        //hideProgressBar()
                        //typeMessage(BaseActivity._OtherError)
                    }
        }
    }


}