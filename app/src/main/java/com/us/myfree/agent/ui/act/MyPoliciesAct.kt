package com.us.myfree.agent.ui.act

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.adapter.PoliciesAdapter
import com.us.myfree.agent.ui.beans.Policies
import kotlinx.android.synthetic.main.activity_mypolicies.*


class MyPoliciesAct : BaseActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var mRecyclerView : RecyclerView
    val mAdapter : PoliciesAdapter = PoliciesAdapter()

    companion object {
        private const val REQUEST_RESULT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypolicies)
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        showProgressBar()

        val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(_UserID,"")

        tv_add.setOnClickListener {
            val intent = Intent(this@MyPoliciesAct, AddPolicies::class.java)
            intent.putExtra("edit", false)
            startActivityForResult(intent, REQUEST_RESULT)
        }

        iv_back_mp.setOnClickListener {
            finish()

        }


        val docRef = db.collection("users").document(userLogged.toString()).collection("policies").orderBy("expirationDate")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                if (e.message.equals("PERMISSION_DENIED: Missing or insufficient permissions."))
                {
                    Log.w("TAG", "PERMISSION_DENIED: Missing or insufficient permissions.", e)
                }
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"



            getData(userLogged)

        }


    }

    fun getData(userLogged:String?)
    {
        if (!userLogged!!.isEmpty()) {
            showProgressBar()
            var dataLst= getDataFire(userLogged)

        }
    }

    fun setUpRecyclerView(lst: MutableList<Policies> ){

        mRecyclerView.visibility=View.VISIBLE
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.PoliciesAdapter(lst, this)
        mRecyclerView.adapter = mAdapter
    }

    fun getDataFire(userLogged:String):MutableList<Policies>
    {

        var mutableList: MutableList<Policies> = mutableListOf()

        try {
            /*var policiesBean2: Policies? = null
            val policiesArray = ArrayList<Policies>()*/
            //val docRef = db.collection("users").document(userLogged).collection("policies").orderBy("expirationDate")


            val docRef = db.collection("users").document(userLogged).collection("policies").orderBy("expirationDate", Query.Direction.DESCENDING)
            docRef.get().addOnSuccessListener { documentSnapshot ->

                mutableList=documentSnapshot.toObjects(Policies::class.java)

                if (mutableList.size==0)
                {
                    progressBar?.visibility = View.GONE
                    recycler_view.visibility = View.GONE
                    tv_notfound.visibility = View.VISIBLE
                    tv_notfound_desc.visibility = View.VISIBLE
                    rl_main01.setBackgroundColor(Color.WHITE)

                }

                else
                {
                    //val user = documentSnapshot?.documents?.get(0)?.id

                    progressBar?.visibility = View.GONE
                    mRecyclerView.visibility = View.VISIBLE
                    tv_notfound.visibility = View.GONE
                    tv_notfound_desc.visibility = View.GONE
                    rl_main01.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray3))

                }



                setUpRecyclerView(mutableList)
            }


        }catch (ex:Exception)
        {
            Log.e("tag",ex.message)
        }



        hideProgressBar()
        return mutableList
    }


    fun deleteRecord(policyId:String)
    {
        val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(_UserID,"").toString()

        db.collection("users").document(userLogged).collection("policies").document(policyId.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener {
                    e -> Log.w("TAG", "Error deleting document", e)
                }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_RESULT && resultCode == Activity.RESULT_OK) {
            val requiredValue = data?.getStringExtra("key")
            val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
            val userLogged=sharedPreference?.getString(_UserID,"")
            getData(userLogged);
        }

    }
}