package com.us.myfree.agent.ui.act

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.beans.Policies
import kotlinx.android.synthetic.main.activity_add_policies.*
import kotlinx.android.synthetic.main.activity_add_policies.tv_welcome
import kotlinx.android.synthetic.main.activity_login.progressBar
import kotlinx.android.synthetic.main.activity_register.rl_createaccount
import java.util.*


class AddPolicies : BaseActivity() {
    val db = FirebaseFirestore.getInstance()
    @ServerTimestamp
    var timeBirthDay: Timestamp? = null
    //@ServerTimestamp
    //var timeBirthDay2: Date? = null

    var sTitle=false
    var policyId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_policies)
        setProgressBar(progressBar)

        val extras = intent.extras
        if (extras != null) {
            sTitle = extras.getBoolean("edit")

            if (sTitle)
            {
                val carrierName = extras.getString("carrierName")
                val policyNumber = extras.getString("policyNumber")
                val policyType = extras.getString("policyType")
                val policyName = extras.getString("policyName")
                val policyDate = extras.getString("policyDate")
                policyId = extras.getString("policyId")


                tv_welcome.setText(R.string.label_editpolicy)
                edtInsuranceCarrier.setText(carrierName)
                edtPolicyNumber.setText(policyNumber)
                if (policyType.equals("auto"))
                    spinner_Policytype.setSelection(0)
                else if (policyType.equals("business"))
                    spinner_Policytype.setSelection(1)
                else if (policyType.equals("home"))
                    spinner_Policytype.setSelection(2)

                edtNamePolicy.setText(policyName)
                edtExpiracionDate.setText(policyDate)

                //edtName.setText(holderName)

            }
            else
            {
                tv_welcome.setText(R.string.label_addpolicy)
            }

        }

        rl_createaccount.setOnClickListener {
            if (isOnline(this))
            {
                if ( edtInsuranceCarrier.text.toString().isEmpty() || edtPolicyNumber.text.toString().isEmpty() || edtNamePolicy.text.toString().isEmpty()
                        || edtExpiracionDate.text.toString().isEmpty())
                    typeMessage(_MissingInformation)
                else
                {
                    if (sTitle)
                    {
                        updateAccount()
                    }
                    else
                    {
                        createAccount()
                    }

                }
            }
            else
            {
                typeMessage(_NetworkError)
            }
        }

        iv_back_pol.setOnClickListener {
            returnList()
        }


        disableInput(edtExpiracionDate)

        edtExpiracionDate.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                //val value: String = java.lang.String.valueOf(editText.getText())
                datePickerDialog()
            }
        })

    }

    fun returnList()
    {
        val intent = intent
        intent.putExtra("key", "exit")
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    fun disableInput(editText: EditText) {
        editText.inputType = InputType.TYPE_NULL
        editText.setTextIsSelectable(true)
        editText.setOnKeyListener { v, keyCode, event ->
            true // Blocks input from hardware keyboards.
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
        val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(_UserID,"")
        Log.i("Tag","")
        saveDataFirestore(userLogged)
    }

    private fun updateAccount()
    {
        showProgressBar()
        val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(_UserID,"")
        Log.i("Tag","")
        updateDataFirestore(userLogged)
    }


    private fun updateDataFirestore(userID: String?)
    {
        val uid= userID.toString()
        val type = spinner_Policytype.selectedItem.toString().toLowerCase()


        var poli: Policies? =Policies()
        if (poli != null) {
            poli.carrierName=edtInsuranceCarrier.text.toString()
            poli.policyNumber=edtPolicyNumber.text.toString()
            poli.holderName=edtNamePolicy.text.toString()
            poli.policyType=type
            poli.expirationDate=timeBirthDay

        }

        if (poli != null) {
            db.collection("users").document(uid).collection("policies").document(policyId.toString()).
                    update(
                            "carrierName" , poli.carrierName,
                            "policyNumber" , poli.policyNumber,
                            "holderName" , poli.holderName,
                            "policyType" , poli.policyType,
                            "expirationDate" , timeBirthDay
                    )
                    .addOnSuccessListener {documentReference ->
                        Log.d("TAG", "Good")

                        hideProgressBar()
                        message(R.string.label_sucess2,R.string.title_policy_saved)
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        //progressBar?.visibility = View.GONE
                        hideProgressBar()
                    }
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

    private fun saveDataFirestore(userID: String?)
    {
        val uid= userID.toString()
        val type = spinner_Policytype.selectedItem.toString().toLowerCase()

        /*val policies = hashMapOf(
                "carrierName" to edtInsuranceCarrier.text.toString(),
                "expiracionDate" to timeBirthDay,
                "policyNumber" to edtPolicyNumber.text.toString(),
                "policyType" to type,
                "holderName" to edtNamePolicy.text.toString()
        )*/


        var poli: Policies? =Policies()
        if (poli != null) {
            poli.carrierName=edtInsuranceCarrier.text.toString()
            poli.policyNumber=edtPolicyNumber.text.toString()
            poli.holderName=edtNamePolicy.text.toString()
            poli.policyType=type
            poli.expirationDate=timeBirthDay
        }

        if (poli != null) {
            db.collection("users").document(uid).collection("policies").add(poli)
                    //db.collection("users").document(uid).set(policies)
                    .addOnSuccessListener {documentReference ->
                        Log.d("TAG", "Good")
                        hideProgressBar()
                        //typeMessage(_SavePolicy)
                        message(R.string.label_sucess2,R.string.title_policy_saved)
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        hideProgressBar()
                        typeMessage(_OtherError)
                    }
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

    fun datePickerDialog()
    {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        val sec = cal.get(Calendar.SECOND)



        val datepickerdialog: DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
            calendar.add(Calendar.HOUR, hour);
            calendar.add(Calendar.MINUTE, min);
            calendar.add(Calendar.SECOND, sec);
            timeBirthDay = Timestamp(calendar.time)
            //timeBirthDay = Timestamp(1595057533, 526939000)
            edtExpiracionDate.setText("" + returnMonth(monthOfYear) + "/" + dayOfMonth+ "/" + year)
        }, y, m, d)

        datepickerdialog.show()
    }


    fun returnMonth(month:Int):String
    {

        when (month) {
            0 -> {return "01"}
            1 -> {return "02"}
            2 -> {return "03"}
            3 -> {return "04"}
            4 -> {return "05"}
            5 -> {return "06"}
            6 -> {return "07"}
            7 -> {return "08"}
            8 -> {return "09"}
            9 -> {return "10"}
            10 -> {return "11"}
            11 -> {return "12"}
            else -> return ""
        }
    }

    fun message(title: Int, body: Int)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setCancelable(false)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            returnList()
        }



        builder.show()
    }


}