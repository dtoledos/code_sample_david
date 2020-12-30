package com.us.myfree.agent.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.act.BaseActivity
import com.us.myfree.agent.ui.adapter.ClaimAdapter
import com.us.myfree.agent.ui.beans.Claim


import kotlinx.android.synthetic.main.fragment_claim_his.*
import kotlinx.android.synthetic.main.fragment_claim_his.progressBar
import kotlinx.android.synthetic.main.fragment_claim_his.tv_notfound
import kotlinx.android.synthetic.main.fragment_claim_his.tv_notfound_desc


class ClaimHistory : Fragment() {

    val db = FirebaseFirestore.getInstance()
    lateinit var mRecyclerView : RecyclerView
    val mAdapter : ClaimAdapter = ClaimAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView =  inflater?.inflate(R.layout.fragment_claim_his, container, false)
        val sharedPreference =  requireActivity().getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(BaseActivity._UserID,"")
        //FirebaseFirestore.setLoggingEnabled(true)

        if (!userLogged!!.isEmpty()) {
            progressBar?.visibility = View.VISIBLE
            //var dataLst= getDataFire(userLogged)
            getDataFire(userLogged,rootView)

        }
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }



    // fun getDataFire(userLogged:String):MutableList<Claim>
   fun getDataFire(userLogged:String, view:View)
    {
        var mutableList: MutableList<Claim> = mutableListOf()
        try {
            val docRef = db.collection("users").document(userLogged).collection("claims").orderBy("date", Query.Direction.DESCENDING)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                mutableList=documentSnapshot.toObjects(Claim::class.java)


                if (mutableList.size==0)
                {
                    progressBar?.visibility = View.GONE
                    recycler_view2.visibility = View.GONE
                    tv_notfound.visibility = View.VISIBLE
                    tv_notfound_desc.visibility = View.VISIBLE
                    rl_main01.setBackgroundColor(Color.WHITE)

                }

                else
                {
                    progressBar?.visibility = View.GONE
                    recycler_view2.visibility = View.VISIBLE
                    tv_notfound.visibility = View.GONE
                    tv_notfound_desc.visibility = View.GONE
                    rl_main01.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorGray3))
                }
                setUpRecyclerView(mutableList,view)
            }


        }catch (ex:Exception)
        {
            Log.e("tag",ex.message)
            progressBar?.visibility = View.GONE

        }


        progressBar?.visibility = View.GONE
      //  return mutableList
    }




    fun setUpRecyclerView(lst: MutableList<Claim> , view:View ){
        mRecyclerView = view.findViewById(R.id.recycler_view2) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        mAdapter.ClaimAdapter(lst, requireActivity())
        mRecyclerView.adapter = mAdapter
    }


}

