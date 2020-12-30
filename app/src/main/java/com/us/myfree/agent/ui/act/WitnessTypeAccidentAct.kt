package com.us.myfree.agent.ui.act

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.camera.CameraActivity
import com.us.myfree.agent.ui.gps.LocationHelper
import kotlinx.android.synthetic.main.act_witness_type_accide.*

class WitnessTypeAccidentAct : AppCompatActivity() {

    var type:String =""
    var latitude:Double?=null
    var longitude:Double?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_witness_type_accide)


        iv_arrow_home_wa.setOnClickListener {
            type=getString(R.string.label_homeWitness)
            photoCam(type)

        }

        iv_arrow_auto_wa.setOnClickListener {
            type=getString(R.string.label_autoWitness)
            photoCam(type)
        }

        iv_arrow_business_wa.setOnClickListener {
            type=getString(R.string.label_businessWitness)
            photoCam(type)
        }

        iv_back_wa.setOnClickListener {
            finish()
        }

        LocationHelper().startListeningUserLocation(this , object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                // Here you got user location :)
                latitude=location.latitude
                longitude=location.longitude
                Log.d("Location","" + location.latitude + "," + location.longitude)
            }
        })

    }

    fun photoCam(text: String)
    {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("noLogged", true)
        intent.putExtra("type", text)
        intent.putExtra("claim", false)
        startActivity(intent)
    }


  /* fun sumbitData(mediaSubject:String,mediaType:String,url:String,latitude:Double,longitude:Double)
   {
       val us = TextUtil2()
       us.saveData(mediaSubject,mediaType,url,latitude,longitude)
   }*/

  //  lateinit var uri : Uri
    //  lateinit var storageRef : StorageReference

   /* private fun upload() {

        var file = Uri.fromFile(File("/sdcard/fierita.png"))
        var mReference = mStorage.child(uri.lastPathSegment.toString())
        try {
            mReference.putFile(file).addOnSuccessListener {
                //taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                //val dwnTxt = findViewById<View>(R.id.dwnTxt) as TextView
                //dwnTxt.text = url.toString()
                //Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

        // Create file metadata including the content type
        var metadata = storageMetadata {
            contentType = "image/jpg"
        }
        // Upload the file and metadata
        var uploadTask = storageRef.child("images/mountains.jpg").putFile(file, metadata)

    }*/

   /* private fun upload()
    {
        // File or Blob
        var file = Uri.fromFile(File("/sdcard/fierita.png"))

// Create the file metadata
        var metadata = storageMetadata {
            contentType = "image/jpeg"
        }

// Upload file and metadata to the path 'images/mountains.jpg'
        var uploadTask = storageRef.child("images/${file.lastPathSegment}").putFile(file, metadata)

// Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
        }.addOnPausedListener {
            println("Upload is paused")
            Log.w("Tag","")
        }.addOnFailureListener {
            Log.e("Tag","")
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            Log.d("Tag","")
            // Handle successful uploads on complete
            // ...
        }
    }*/

}