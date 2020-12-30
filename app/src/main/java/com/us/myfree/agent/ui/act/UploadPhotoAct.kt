package com.us.myfree.agent.ui.act

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.otaliastudios.cameraview.PictureResult
import com.us.myfree.agent.R
import com.us.myfree.agent.common.extensions.gone
import com.us.myfree.agent.ui.beans.*
import com.us.myfree.agent.ui.tools.TextUtil2
import com.us.myfree.agent.worker.MyWorker
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.lang.Runnable
import java.util.*

//,CoroutineScope
class UploadPhotoAct() : BaseActivity() {

    //override val coroutineContext: CoroutineContext
      //   get() =  Dispatchers.Main + job

    //1/5*100
    private lateinit var  job: Job

    private lateinit var auth: FirebaseAuth
    var sTitle=false
    private var picture: PictureResult? = null
    val db = FirebaseFirestore.getInstance()
    private var catastrophe: Boolean=false
    private var indexImg:Int=0
    private var indexVideo:Int=0
    private var indexSum:Int=0
    private var count:Int=0
    private var porce:Double=0.0

    private lateinit var imgLstC: ArrayList<ClaimObj>
    private lateinit var videoLstC: ArrayList<ClaimObj>


    companion object {
        private lateinit var imagesArray: ArrayList<ClaimObj>
        private lateinit var videoArray: ArrayList<ClaimObj>

        fun setData(arrayLstPhoto: ArrayList<ClaimObj>,arrayLstVideo: ArrayList<ClaimObj>) {
            imagesArray = arrayLstPhoto
            videoArray = arrayLstVideo
            Log.i("Tag","")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo)
        //setProgressBar(progressBar)
        auth = Firebase.auth

        indexImg=imagesArray.size
        indexVideo=videoArray.size
        indexSum=indexImg+indexVideo;

        val extras = intent.extras
        if (extras != null) {
            val nologged = extras.getBoolean("noLogged")
            val claimType = extras.getString("claimType")
            catastrophe= extras.getBoolean("catastrophe")
            //ramdomId()
            val textUtil2 = TextUtil2()
            val claimId=textUtil2.getRandomString(3)+textUtil2.getRandomNumber(7)

            val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
            val userLogged=sharedPreference?.getString(_UserID,"")

          //  uploadFiles(randomID,claimType,userLogged)
            callWorker(claimId,claimType,userLogged,nologged)

            var progress=0

            Thread(Runnable {
                //val timerEnd = System.currentTimeMillis() + 60 * 1000
                val timerEnd = System.currentTimeMillis() + 20 * 1000
                while (timerEnd > System.currentTimeMillis()) {
                    //progress = 20 - (timerEnd - System.currentTimeMillis()).toInt() / 1000
                    progress = 20- (timerEnd - System.currentTimeMillis()).toInt() / 1000
                    // Update the progress bar
                    progressBar.post(Runnable {
                        progressBar.setProgress(progress)
                    })
                    try {
                        //Thread.sleep(500)
                        Thread.sleep(1)
                    } catch (e: InterruptedException) {
                        Log.w("App", "Progress thread cannot sleep")
                    }
                }
                progressBar.post(Runnable {
                   // finish()
                    //if (nologged ?: true)
                        openReportAct(claimId,nologged)
                })
            }).start()


            //val claimObjLst = mutableListOf<ClaimObj>()
            //uploadVideo(randomID,claimType, claimObjLst as ArrayList<ClaimObj>,userLogged.toString())
            //40 seconds
            //upload(randomID,claimType,userLogged)
            //execCouru(randomID,claimType,userLogged)
            //executeCourutineSecuencial(randomID,claimType,userLogged)
            //executeCourutine(randomID,claimType,userLogged)
        }
        //Log.i(" ",""+ imagesArray.size);
        //Log.i(" ",""+ videoArray.size);
    }

    fun callWorker(claimId:String?,claimType:String?,userLogged:String?, nologged:Boolean?)
    {
        val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .build()

        val data = Data.Builder()
        data.putString("claimId", claimId)
        data.putString("claimType", claimType)
        data.putString("userLogged", userLogged)

        if (nologged ?: true)
           data.putBoolean("noLogged", true)
        else
            data.putBoolean("noLogged", false)

        MyWorker.setData(imagesArray,videoArray)

        var request = OneTimeWorkRequestBuilder<MyWorker>()
                .setConstraints(constraints).setInputData(data.build())
                .build()

        WorkManager.getInstance(this).enqueue(request)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
                .observe(this, androidx.lifecycle.Observer {
                    //val status: String = it.state.name
                    //Toast.makeText(this,status, Toast.LENGTH_SHORT).show()
                })
    }

    fun uploadFiles(claimId:String?,claimType:String?,userLogged:String?)
    {
        val claimImageObjLst = mutableListOf<ClaimObj>()
        val claimVideoObjLst = mutableListOf<ClaimObj>()
        var contador=0

        lifecycleScope.launch {

            imagesArray.forEach {
                c ->

                val urlPhoto= async(Dispatchers.IO)
                {
                    uploadPhoto(c.url,c.dateString.toString(),c.location)
                }


                async(Dispatchers.Main)
                {
                    refreshProgress(count++)
                }


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

                if (claimImageObjLst.size>0 && claimImageObjLst.size==1)
                {
                    saveDataFirestore(userLogged,claimType.toString(),claimId.toString(),claimImageObjLst[0].url.toString())
                }

                if (imagesArray.size==claimImageObjLst.size)
                {
                   saveData(userLogged.toString(),claimId.toString(),claimType, claimImageObjLst  as ArrayList<ClaimObj>, claimVideoObjLst as ArrayList<ClaimObj>)
                   // openReportAct(claimId)
                }


            }


        }


        lifecycleScope.launch {
            videoArray.forEach { c ->
                val urlVideo: Deferred<String> = async(Dispatchers.IO)
                {
                    uploadVideo(c.url,c.dateString.toString(),c.location)
                }

                async(Dispatchers.Main)
                {
                    refreshProgress(count++)
                }


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

    fun refreshProgress(count:Int)
    {
        porce= (count.toDouble()  / indexSum.toDouble()  * 100)
        val value=porce.toInt()
        progressBar.setProgress(value)

    }

    fun uploadFiles2(claimId:String?,claimType:String?,userLogged:String?)
    {
        val claimImageObjLst = mutableListOf<ClaimObj>()
        val claimVideoObjLst = mutableListOf<ClaimObj>()

        lifecycleScope.launch {

            imagesArray.forEach {
                c ->

                val urlPhoto= withContext(Dispatchers.IO)
                {
                    uploadPhoto(c.url,c.dateString.toString(),c.location)
                }

                count++
                porce= (count.toDouble()  / indexSum.toDouble()  * 100)

                progressBar.post(Runnable {
                    //progressBar.setProgress(porce.toInt())
                    val value=porce.toInt()
                    progressBar.setProgress(value)
                })

                var claimObj: ClaimObj? =ClaimObj()
                if (claimObj != null) {
                    claimObj.location=c.location
                    claimObj.mediaSubject=c.mediaSubject
                    claimObj.mediaType=c.mediaType
                    claimObj.date=c.date
                    claimObj.dateString=c.dateString
                    claimObj.url=urlPhoto
                    claimImageObjLst +=claimObj
                    Log.i("Tag","");
                }

                if (claimImageObjLst.size>0 && claimImageObjLst.size==1)
                {
                    saveDataFirestore(userLogged,claimType.toString(),claimId.toString(),claimImageObjLst[0].url.toString())
                }
            }

            videoArray.forEach { c ->
                val urlVideo= withContext(Dispatchers.IO)
                {
                    uploadVideo(c.url,c.dateString.toString(),c.location)
                }

                progressBar.post(Runnable {
                    val value=porce.toInt()
                    progressBar.setProgress(value)
                })


                var claimObjVideo: ClaimObj? =ClaimObj()
                if (claimObjVideo != null) {
                    claimObjVideo.location=c.location
                    claimObjVideo.mediaSubject=c.mediaSubject
                    claimObjVideo.mediaType=c.mediaType
                    claimObjVideo.date=c.date
                    claimObjVideo.dateString=c.dateString
                    claimObjVideo.url=urlVideo
                    claimVideoObjLst +=claimObjVideo
                    Log.i("Tag","");
                }

            }

            saveData(userLogged.toString(),claimId.toString(),claimType, claimImageObjLst  as ArrayList<ClaimObj>, claimVideoObjLst as ArrayList<ClaimObj>)

        }
    }


    fun execCouru(claimId:String?,claimType:String?,userLogged:String?)
    {
        lifecycleScope.launch {

            val success = withContext(Dispatchers.IO)
            {
                upload(claimId,claimType,userLogged)
            }
        }

    }

    fun executeCourutineSecuencial(claimId:String?,claimType:String?,userLogged:String?)
    {
        val claimObjLst = mutableListOf<ClaimObj>()

        lifecycleScope.launch {

            val success= withContext(Dispatchers.IO)
            {
                uploadImageC(claimId,claimType,userLogged)
            }

            val success2= withContext(Dispatchers.IO)
            {

                uploadVideoC(claimId,claimType, claimObjLst as ArrayList<ClaimObj>,userLogged.toString())
            }


            val success3= withContext(Dispatchers.IO)
            {

                saveData(userLogged.toString(),claimId.toString(),claimType,imgLstC, videoLstC)
            }

        }
    }

    fun executeCourutine(claimId:String?,claimType:String?,userLogged:String?)
    {
        val claimObjLst = mutableListOf<ClaimObj>()
        lifecycleScope.launch {
            Log.e("tag","000000000 000000000 000000000 000000000 000000000 000000000 000000000 000000000")

            val success= async(Dispatchers.IO)
            {
                uploadImageC(claimId,claimType,userLogged)
            }

            val success2= async(Dispatchers.IO)
            {

                uploadVideoC(claimId,claimType, claimObjLst as ArrayList<ClaimObj>,userLogged.toString())
            }

            success.await()
            success2.await()
            saveData(userLogged.toString(),claimId.toString(),claimType,imgLstC, videoLstC)

        }
    }


    fun courutineMethod()
    {
        //job = SupervisorJob()
        //val job=GlobalScope.launch (Dispatchers.Main){
        //launch {
        lifecycleScope.launch {
            //val success= withContext(Dispatchers.IO)
            val success= async(Dispatchers.IO)
            {
                validateLogin("","")
            }

            toast(if (success.await()) "Success" else "Failure");
        }
    }

    val STRING_LENGTH = 3;
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z')

    @RequiresApi(Build.VERSION_CODES.N)
    fun ramdomId() {

        val randomString = (1..STRING_LENGTH)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");

        Log.i("Tag","")
    }


    private fun saveData(userLogged: String, clamId: String, claimType:String?, imgLst:ArrayList<ClaimObj>, videoLst:ArrayList<ClaimObj>):UserBean2?
    {
        var userData:UserBean2?=null
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
            progressBar?.visibility = View.GONE
        }

        return userData;
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




        var sortedList = imgLst.sortedWith(compareBy({ it.date }))




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
                        hideProgressBar()
                        //openReportAct(clamId)
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        hideProgressBar()
                        typeMessage(_OtherError)
                    }
        }
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
                        //hideProgressBar()

                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        //hideProgressBar()
                        typeMessage(_OtherError)
                    }
        }

    }




    public override fun onStart() {
        super.onStart()

    }

    public override fun onStop() {
        super.onStop()
       // progressBar.gone()
    }


    private fun uploadImageC(claimId:String?,claimType:String?,userLogged:String?)
    {
        imgLstC= ArrayList<ClaimObj>()
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = Firebase.storage
        val storageRef = storage.reference
        val claimObjLst = mutableListOf<ClaimObj>()
        Log.e("tag","uploadImageC uploadImageC uploadImageC uploadImageC uploadImage CuploadImageC")

        imagesArray.forEach {
            c ->
            var file = Uri.fromFile(File(c.url))

            // Create the file metadata
            var metadata = storageMetadata {
                contentType = "image/jpeg"
                if (c.location!=null)
                {
                    setCustomMetadata("latitude", c.location!!.latitude.toString())
                    setCustomMetadata("longitude", c.location!!.longitude.toString())
                }

                setCustomMetadata("date", c.dateString)
                setCustomMetadata("creatorsUID", uuid)
            }

            val ref = storageRef.child("Images/${file.lastPathSegment}")
            var uploadTask = ref.putFile(file,metadata)


            uploadTask.addOnFailureListener {
                exception -> Log.i("error:", exception.toString())
            }
                    .addOnSuccessListener{taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                Log.d("Tag","")
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                count++
                                porce= (count.toDouble()  / indexSum.toDouble()  * 100)

                                Log.d("Tag","")
                                progressBar.post(Runnable {
                                    //progressBar.setProgress(porce.toInt())
                                    val value=porce.toInt()
                                    progressBar.setProgress(value)
                                })

                                val downloadUri = task.result
                                val url = downloadUri!!.toString()
                                //ClaimObjLst
                                var claimObj: ClaimObj? =ClaimObj()
                                if (claimObj != null) {
                                    claimObj.location=c.location
                                    claimObj.mediaSubject=c.mediaSubject
                                    claimObj.mediaType=c.mediaType
                                    claimObj.date=c.date
                                    claimObj.dateString=c.dateString
                                    claimObj.url=url
                                    claimObjLst +=claimObj
                                    Log.i("Tag","");
                                    //ClaimObjLst.add(claimObj)
                                }

                                //simultaneo
                                if (claimObjLst.size>0 && claimObjLst.size==1)
                                {
                                    saveDataFirestore(userLogged,claimType.toString(),claimId.toString(),claimObjLst[0].url.toString())
                                }

                                if (imagesArray.size==claimObjLst.size)
                                {

                                    imgLstC = claimObjLst as ArrayList<ClaimObj>
                                    Log.e("tag","Upload Photo Upload Photo Upload Photo Upload Photo Upload Photo Upload Photo Upload Photo Upload PhotoUpload Photo")
                                    if (videoArray.size>0)
                                    {
                                       //uploadVideo(claimId,claimType, claimObjLst as ArrayList<ClaimObj>,userLogged.toString())

                                    }
                                    else
                                    {
                                        progressBar.gone()

                                    }
                                }

                                Log.i("seeThisUri", downloadUri.toString())// This is the one you should store*/



                            } else {
                                Log.d("Tag","")
                            }
                        }
                    }.addOnProgressListener { taskSnapshot ->

                    }
        }

        Log.e("tag"," return true  return true  return true  return true  return true  return true  return true  return true  return true  return true")
        //return true
    }

    private fun uploadVideoC(claimId:String?, claimType:String?, imgLst:ArrayList<ClaimObj>,userLogged:String)
    {
        videoLstC= ArrayList<ClaimObj>()
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = Firebase.storage
        val storageRef = storage.reference
        val ClaimObjLst = mutableListOf<ClaimObj>()

        videoArray.forEach {
            c ->
            var file = Uri.fromFile(File(c.url))

            // Create the file metadata
            var metadata = storageMetadata {
                contentType = "video/mp4"
                if (c.location!=null)
                {
                    setCustomMetadata("latitude", c.location!!.latitude.toString())
                    setCustomMetadata("longitude", c.location!!.longitude.toString())
                }

                setCustomMetadata("date", c.dateString)
                setCustomMetadata("creatorsUID", uuid)
            }

            val ref = storageRef.child("Videos/${file.lastPathSegment}")
            var uploadTask = ref.putFile(file,metadata)


            uploadTask.addOnFailureListener {
                exception -> Log.i("error:", exception.toString())
            }
                    .addOnSuccessListener{taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                Log.d("Tag","")
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                val url = downloadUri!!.toString()

                                var claimObj: ClaimObj? =ClaimObj()
                                if (claimObj != null) {
                                    claimObj.location=c.location
                                    claimObj.mediaSubject=c.mediaSubject
                                    claimObj.mediaType=c.mediaType
                                    claimObj.date=c.date
                                    claimObj.dateString=c.dateString
                                    claimObj.url=url
                                    ClaimObjLst +=claimObj
                                    Log.i("Tag","");
                                    //ClaimObjLst.add(claimObj)
                                }

                                if (videoArray.size==ClaimObjLst.size)
                                {
                                    videoLstC = ClaimObjLst as ArrayList<ClaimObj>

                                    if (ClaimObjLst.size>0)
                                    {
                                        //saveData(userLogged,claimId.toString(),claimType,imgLst, ClaimObjLst as ArrayList<ClaimObj>)
                                    }

                                    count++
                                    porce= (count.toDouble()  / indexSum.toDouble()  * 100)

                                    Log.d("Tag","")
                                    progressBar.post(Runnable {
                                        //progressBar.setProgress(porce.toInt())
                                        val value=porce.toInt()
                                        progressBar.setProgress(value)
                                    })
                                }

                                Log.i("seeThisUri", downloadUri.toString())// This is the one you should store

                            } else {
                                Log.d("Tag","")
                            }
                        }
                    }.addOnProgressListener { taskSnapshot ->

                        Log.d("Tag","")
                    }
        }
        //return true
    }

    private fun upload(claimId:String?,claimType:String?,userLogged:String?)
    {
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = Firebase.storage
        val storageRef = storage.reference

        //val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
        //val userLogged=sharedPreference?.getString(_UserID,"")
        val claimObjLst = mutableListOf<ClaimObj>()

        imagesArray.forEach {
            c ->
                var file = Uri.fromFile(File(c.url))

                /*val thread: Thread = object : Thread() {
                    override fun run() {
                        count++
                        porce= (count.toDouble()  / indexImg.toDouble()  * 100)
                        progressBar.setProgress(porce.toInt())
                        Log.i("Tag","");
                    }
                }
                thread.start()*/


                // Create the file metadata
                var metadata = storageMetadata {
                    contentType = "image/jpeg"
                    if (c.location!=null)
                    {
                        setCustomMetadata("latitude", c.location!!.latitude.toString())
                        setCustomMetadata("longitude", c.location!!.longitude.toString())
                    }

                    setCustomMetadata("date", c.dateString)
                    setCustomMetadata("creatorsUID", uuid)
                }

                val ref = storageRef.child("Images/${file.lastPathSegment}")
                var uploadTask = ref.putFile(file,metadata)


                uploadTask.addOnFailureListener {
                    exception -> Log.i("error:", exception.toString())
                }
                .addOnSuccessListener{taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                Log.d("Tag","")
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                count++
                                porce= (count.toDouble()  / indexSum.toDouble()  * 100)

                                Log.d("Tag","")
                                progressBar.post(Runnable {
                                    //progressBar.setProgress(porce.toInt())
                                    val value=porce.toInt()
                                    progressBar.setProgress(value)
                                })

                                val downloadUri = task.result
                                val url = downloadUri!!.toString()
                                //ClaimObjLst
                                var claimObj: ClaimObj? =ClaimObj()
                                if (claimObj != null) {
                                    claimObj.location=c.location
                                    claimObj.mediaSubject=c.mediaSubject
                                    claimObj.mediaType=c.mediaType
                                    claimObj.date=c.date
                                    claimObj.dateString=c.dateString
                                    claimObj.url=url
                                    claimObjLst +=claimObj
                                    Log.i("Tag","");
                                    //ClaimObjLst.add(claimObj)
                                }

                                //simultaneo
                                if (claimObjLst.size>0 && claimObjLst.size==1)
                                {
                                    saveDataFirestore(userLogged,claimType.toString(),claimId.toString(),claimObjLst[0].url.toString())
                                }

                                /*if (imagesArray.size==claimObjLst.size) {
                                    openReportAct(claimId)
                                }*/

                                if (imagesArray.size==claimObjLst.size)
                                {

                                    if (videoArray.size>0)
                                    {
                                        uploadVideo(claimId,claimType, claimObjLst as ArrayList<ClaimObj>,userLogged.toString())
                                    }
                                    else
                                    {
                                        progressBar.gone()

                                    }
                                }

                                Log.i("seeThisUri", downloadUri.toString())// This is the one you should store*/



                            } else {
                                Log.d("Tag","")
                            }
                        }
                    }.addOnProgressListener { taskSnapshot ->


                    }
             }

    }


    private fun uploadVideo(claimId:String?, claimType:String?, imgLst:ArrayList<ClaimObj>,userLogged:String)
    {
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = Firebase.storage
        val storageRef = storage.reference
        val ClaimObjLst = mutableListOf<ClaimObj>()

        videoArray.forEach {
            c ->
            var file = Uri.fromFile(File(c.url))

            // Create the file metadata
            var metadata = storageMetadata {
                contentType = "video/mp4"
                if (c.location!=null)
                {
                    setCustomMetadata("latitude", c.location!!.latitude.toString())
                    setCustomMetadata("longitude", c.location!!.longitude.toString())
                }

                setCustomMetadata("date", c.dateString)
                setCustomMetadata("creatorsUID", uuid)
            }

            val ref = storageRef.child("Videos/${file.lastPathSegment}")
            var uploadTask = ref.putFile(file,metadata)


            uploadTask.addOnFailureListener {
                exception -> Log.i("error:", exception.toString())
            }
                    .addOnSuccessListener{taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                Log.d("Tag","")
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                val url = downloadUri!!.toString()

                                var claimObj: ClaimObj? =ClaimObj()
                                if (claimObj != null) {
                                    claimObj.location=c.location
                                    claimObj.mediaSubject=c.mediaSubject
                                    claimObj.mediaType=c.mediaType
                                    claimObj.date=c.date
                                    claimObj.dateString=c.dateString
                                    claimObj.url=url
                                    ClaimObjLst +=claimObj
                                    Log.i("Tag","");
                                    //ClaimObjLst.add(claimObj)
                                }

                                if (videoArray.size==ClaimObjLst.size)
                                {
                                    if (ClaimObjLst.size>0)
                                    {
                                        saveData(userLogged,claimId.toString(),claimType,imgLst, ClaimObjLst as ArrayList<ClaimObj>)
                                    }

                                    count++
                                    porce= (count.toDouble()  / indexSum.toDouble()  * 100)

                                    Log.d("Tag","")
                                    progressBar.post(Runnable {
                                        //progressBar.setProgress(porce.toInt())
                                        val value=porce.toInt()
                                        progressBar.setProgress(value)
                                    })
                                }

                                Log.i("seeThisUri", downloadUri.toString())// This is the one you should store

                            } else {
                                Log.d("Tag","")
                            }
                        }
                    }.addOnProgressListener { taskSnapshot ->

                        Log.d("Tag","")
                    }
        }

    }

    fun openReportAct(text: String?,nologged:Boolean?)
    {
        progressBar?.visibility = View.GONE
        finish()
        val intent = Intent(this, ReportCompleAct::class.java)
        intent.putExtra("claimid", text.toString())
        intent.putExtra("catastrophe", catastrophe)
        intent.putExtra("noLogged", nologged)

        startActivity(intent)
    }

    private fun Context.toast(message:String)
    {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    private fun validateLogin(user:String, pass:String):Boolean
    {
        return user.isNotEmpty() && pass.isNotEmpty()
    }



    override fun onDestroy() {
        super.onDestroy()
    }


    suspend fun uploadPhoto(urlFile:String?,dateString:String,location:GeoPoint?): String {
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var uri = Uri.fromFile(File(urlFile))
        val fileRef = storageRef.child("Images/${uri.lastPathSegment}")

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

    suspend fun uploadVideo(urlFile:String?,dateString:String,location:GeoPoint?): String {
        var uuid = Firebase.auth.currentUser!!.uid
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var uri = Uri.fromFile(File(urlFile))
        val fileRef = storageRef.child("Videos/${uri.lastPathSegment}")

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

}