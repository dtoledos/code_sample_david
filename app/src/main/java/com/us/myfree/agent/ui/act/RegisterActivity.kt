package com.us.myfree.agent.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.us.myfree.agent.R
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_login.iv_close_login
import kotlinx.android.synthetic.main.activity_login.progressBar
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
// Initialize Firebase Auth
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        setProgressBar(progressBar)

        rl_createaccount.setOnClickListener {
            /*val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)*/

            if (isOnline(this))
            {
                if (edtEmail.text.toString().isEmpty() || edtPassword.text.toString().isEmpty() || edtconfirmPassword.text.toString().isEmpty() || edtPhoneNumber.text.toString().isEmpty()
                        || edtName.text.toString().isEmpty())
                    typeMessage(_MissingInformation)
                else if (!edtEmail.text.toString().isEmailValid())
                    typeMessage(_ValidEmail)
                else if (edtPassword.text.toString().length<6)
                    typeMessage(_WeakPassword)
                else if (!edtPassword.text.toString().equals(edtconfirmPassword.text.toString(),false))
                    typeMessage(_PasswordMitmatch)
                else
                {
                    createAccount()
                }
            }
            else
            {
                typeMessage(_NetworkError);
            }



        }

        iv_close_login.setOnClickListener {
            finish()
        }

    }




    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    public override fun onStop() {
        super.onStop()
        progressBar?.visibility = View.INVISIBLE
    }

    private fun createAccount()
    {
        showProgressBar()

        auth.createUserWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                        saveDataFirestore(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)

                        if ((task.exception as FirebaseAuthUserCollisionException).errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE"))
                        {
                            //errorLoginEmpty(R.string.label_other_error,R.string.label_other_error_desc)
                            typeMessage(_AlreadyRegistered)
                        }
                        else
                        {
                            typeMessage(_OtherError)
                        }

                    }
                    hideProgressBar()
                    // ...
                }


    }

    private fun saveDataFirestore(user: FirebaseUser?)
    {
        val uid= user?.uid
        // Create a new user with a first and last name
        //dateOfBirth

        val user = hashMapOf(
                "name" to edtName.text.toString(),
                "email" to edtEmail.text.toString(),
                "phoneNumber" to edtPhoneNumber.text.toString()
        )


        // Add a new document with a generated ID
        /*val user: MutableMap<String, Any> = HashMap()
        user["name"] = name
        user["email"] = email
        user["phone"] = phone*/

        // Add a new document with a generated ID
        db.collection("users").document(uid.toString()).set(user)
                .addOnSuccessListener {documentReference ->
                    Log.d("TAG", "Good")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
        /*db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }*/
    }

    private fun updateUI(user: FirebaseUser?)
    {
        if (user != null) {
            loggedUser(user)
        }
        else
        {

        }
    }

    private fun loggedUser(user: FirebaseUser?)
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

            val intent = Intent(this@RegisterActivity, MainAct::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }
    }

}