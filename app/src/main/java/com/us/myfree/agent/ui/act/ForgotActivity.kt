package com.us.myfree.agent.ui.act

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.us.myfree.agent.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        rl_submit_fp.setOnClickListener {


            if (isOnline(this))
            {
                if (edtEmail_fp.text.toString().isEmpty() )
                    typeMessage(_MissingEmail);
                else if (!edtEmail_fp.text.toString().isEmailValid())
                    typeMessage(_ValidEmail);
                else
                {
                    restorePassword()
                }
            }
            else
            {
                typeMessage(_NetworkError);
            }




        }

        iv_back_fp.setOnClickListener {
            finish()
        }

    }

    fun restorePassword()
    {
        //progressBar?.visibility = View.VISIBLE
        showProgressBar()

        val fAuth = FirebaseAuth.getInstance()
        fAuth.sendPasswordResetEmail(edtEmail_fp.text.toString()).addOnCompleteListener({ listener ->
            if (listener.isSuccessful) {
                // Do something when successful
                typeMessage(_NotError)
            } else {
                typeMessage(_UserNotFound)
                // Do something when not successful
            }

            //progressBar?.visibility = View.INVISIBLE
            hideProgressBar()
        })
    }





}