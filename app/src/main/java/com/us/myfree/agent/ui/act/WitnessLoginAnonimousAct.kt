package com.us.myfree.agent.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.us.myfree.agent.R
import kotlinx.android.synthetic.main.act_witness_something.*


class WitnessLoginAnonimousAct : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_witness_something)
        auth = Firebase.auth

        rl_submit_ws.setOnClickListener {

            if (edtEmail_ws.text.toString().isEmpty() )
                typeMessage(_MissingPhone);
            else if (edtEmail_ws.text.toString().length<10 )
                typeMessage(_MissingPhone);
            else
            {
                if (isOnline(this)) {
                    anonymousLogin2()
                }
                else
                {
                    typeMessage(_NetworkError)
                }
            }
        }


        iv_back_fp.setOnClickListener {
            finish()
        }
    }


    fun anonymousLogin2()
    {
        showProgressBar()

        auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInAnonymously:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInAnonymously:failure", task.exception)
                        typeMessage(_OtherError);
                        updateUI(null)
                    }
                    hideProgressBar()
                    // ...
                }
    }


    private fun updateUI(user: FirebaseUser?)
    {
        if (user!=null)
        {

            val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putBoolean(_IsLogged,false)
            editor.commit()

            val uuid=user.uid
            Log.i("Tag", uuid)
            editor.putString(_UserID,user.uid)
            editor.commit()

            editor.putString(_UserPhone,edtEmail_ws.text.toString())
            editor.commit()



            //Start Activity
            val intent = Intent(this@WitnessLoginAnonimousAct, WitnessTypeAccidentAct::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }
    }

}