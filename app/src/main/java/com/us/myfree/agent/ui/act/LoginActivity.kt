package com.us.myfree.agent.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.us.myfree.agent.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
// Initialize Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        setProgressBar(progressBar)

        rl_login.setOnClickListener {

            if (isOnline(this))
            {
                if (edtEmail.text.toString().isEmpty() || edtPassword.text.toString().isEmpty())
                    typeMessage(_MissingEmailPassword);
                else if (!edtEmail.text.toString().isEmailValid())
                    typeMessage(_ValidEmail);
                else
                {
                    loginAccount()
                }
            }
            else
            {
                typeMessage(_NetworkError)
            }


        }

        iv_close_login.setOnClickListener {
            finish()
        }

        tv_forgot_password.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }

        rl_witness_something.setOnClickListener {
            val intent = Intent(this@LoginActivity, WitnessLoginAnonimousAct::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }


    }




    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    public override fun onStop() {
        super.onStop()
        progressBar?.visibility = View.INVISIBLE
    }

    private fun loginAccount()
    {

        showProgressBar()

        auth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success")
                        val user = auth.currentUser
                        //val userToken = auth.currentUser!!.getIdToken(true)
                        getLogged(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        typeMessage(_ErrorLoginPassword)
                        //Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                        // ...
                    }
                    hideProgressBar()
                   
                }
    }

    private fun getLogged(user: FirebaseUser?)
    {
        if (user != null) {

            val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putBoolean(_IsLogged,true)
            editor.commit()

            val uuid=user.uid
            Log.i("Tag", uuid)
            editor.putString(_UserID,user.uid)
            editor.commit()

            editor.putString(_UserEmail,user.email)
            editor.commit()

            finishAffinity()
            val intent = Intent(this@LoginActivity, MainAct::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }
        else
        {

        }
    }


}