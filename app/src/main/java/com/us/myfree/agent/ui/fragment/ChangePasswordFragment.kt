package com.us.myfree.agent.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.act.BaseActivity
import com.us.myfree.agent.ui.act.BaseActivity.Companion._UserEmail
import com.us.myfree.agent.ui.act.BaseActivity.Companion._UserID
import com.us.myfree.agent.ui.act.FreeAgentGetStartedAct
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_profile.progressBar
import kotlinx.android.synthetic.main.fragment_profile.rl_save


class ChangePasswordFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i("Tag","");
        auth = Firebase.auth

        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPreference =  activity?.getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(_UserID,"")


        iv_back_cp.setOnClickListener {
            (activity as AppCompatActivity).supportActionBar?.show()

            val bundle = Bundle()
            bundle.putString("userName", "David")

            val fragment: Fragment = ProfileFragment()
            fragment.setArguments(bundle)
            val fm: FragmentManager = requireActivity().supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.add(R.id.frame_container, fragment)
            ft.commit()
        }


        rl_save.setOnClickListener {
            if (isOnline(activity?.applicationContext!!))
            {

                if (edtNewPassword.text.toString().isEmpty() || edtConfirmPassword.text.toString().isEmpty() || edtCurrentPassword.text.toString().isEmpty())
                    typeMessage(_MissingInformation)
                else if (!edtNewPassword.text.toString().equals(edtConfirmPassword.text.toString(),false))
                    typeMessage(_PasswordMitmatch)
                else if (!edtNewPassword.text.toString().equals(edtConfirmPassword.text.toString(),false))
                    typeMessage(_ErrorLoginPassword)
                else if (edtNewPassword.text.toString().length<6)
                    typeMessage(_WeakPassword)
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





    }

    private fun saveData()
    {
        progressBar?.visibility = View.VISIBLE

        val user = FirebaseAuth.getInstance().currentUser
        val txtNewPass = edtNewPassword.text.toString()

        user!!.updatePassword(txtNewPass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
              
                dialogPassword(R.string.label_sucess,R.string.label_password_update)




            } else {
                typeMessage(_OtherError)
               
            }
        }

        progressBar?.visibility = View.GONE


    }


    fun dialogPassword(title: Int, body: Int)
    {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setCancelable(false)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            logout1()
        }
        builder.show()
    }


    fun logout1()
    {
        val sharedPreference =  activity?.getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
        var editor = sharedPreference!!.edit()
        editor.putBoolean(BaseActivity._IsLogged,false)
        editor.commit()

        editor.putString(_UserID,"")
        editor.commit()
        activity?.finish();

        val intent = Intent(requireActivity(), FreeAgentGetStartedAct::class.java)
        intent.putExtra("key", "Kotlin")
        startActivity(intent)
    }

    private lateinit var auth: FirebaseAuth
    private fun loginAccount()
    {
        //progressBar?.visibility = View.VISIBLE
        progressBar?.visibility = View.VISIBLE

        val sharedPreference =  activity?.getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
        val userEmail=sharedPreference?.getString(_UserEmail,"")

        if (userEmail != null && userEmail.length>0) {
            auth.signInWithEmailAndPassword(userEmail, edtCurrentPassword.text.toString())
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success")
                            val user = auth.currentUser
                            saveData()

                        } else {
                            // If sign in fails, display a message to the user.

                            typeMessage(_ErrorLoginPassword);

                        }
                        progressBar?.visibility = View.GONE

                    }
        }
    }

}

