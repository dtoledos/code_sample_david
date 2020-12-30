package com.us.myfree.agent.ui.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.act.BaseActivity
import com.us.myfree.agent.ui.act.BaseActivity.Companion._UserID
import com.us.myfree.agent.ui.beans.UserBean2
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.progressBar
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : BaseFragment() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i("Tag","");
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPreference =  activity?.getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
        val userLogged=sharedPreference?.getString(_UserID,"")

        if (!userLogged!!.isEmpty()) {
            progressBar?.visibility = View.VISIBLE
            listenToDocumentLocal(userLogged)
        }

        tv_accountIdValue.setText(userLogged);
        //datePickerDialog()

        rl_save.setOnClickListener {
            if (isOnline(activity?.applicationContext!!))
            {

                if (!edtEmalProfile.text.toString().isEmailValid())
                    typeMessage(BaseActivity._ValidEmail);
                else
                {
                    saveData(userLogged)
                }

            }
            else
            {
                typeMessage(_NetworkError)
            }

        }

        rl_change_password.setOnClickListener {
            //typeMessage(_NetworkError)
            changePasswordScreen()
        }

        edtDateProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i("Tag","")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("Tag","")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i("Tag","")
            }
        })


        disableInput(edtDateProfile)

        edtDateProfile.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                //val value: String = java.lang.String.valueOf(editText.getText())
                datePickerDialog()
            }
        })

    }

    private fun saveData(userId:String)
    {
        progressBar?.visibility = View.VISIBLE
        //edtDateProfile
        if (!edtDateProfile.getText().toString().isEmpty())
        {
            db.collection("users").document(userId)
                    .update(
                            "name" , edtNameProfile.text.toString(),
                            "email" , edtEmalProfile.text.toString(),
                            "phoneNumber" , edtPhoneNumberProfile.text.toString(),
                            "address" , edtAddressProfile.text.toString(),
                            "state" , edtStateProfile.text.toString(),
                            "city" , edtCityProfile.text.toString(),
                            "country" , edtCountryProfile.text.toString(),
                            "dateOfBirth", timeBirthDay
                    ).addOnSuccessListener {documentReference ->
                        Log.d("TAG", "Good")
                        progressBar?.visibility = View.GONE
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        progressBar?.visibility = View.GONE
                    }
        }
        else
        {
            db.collection("users").document(userId)
                    .update(
                            "name" , edtNameProfile.text.toString(),
                            "email" , edtEmalProfile.text.toString(),
                            "phoneNumber" , edtPhoneNumberProfile.text.toString(),
                            "address" , edtAddressProfile.text.toString(),
                            "state" , edtStateProfile.text.toString(),
                            "city" , edtCityProfile.text.toString(),
                            "country" , edtCountryProfile.text.toString()
                    ).addOnSuccessListener {documentReference ->
                        Log.d("TAG", "Good")
                        progressBar?.visibility = View.GONE
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        progressBar?.visibility = View.GONE
                    }
        }

    }

    private fun listenDocumentLocal2(userLogged: String)
    {

        try {

            val docRef = db.collection("users").document(userLogged)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                Log.i("tag","");
                val userData = documentSnapshot.toObject(UserBean2::class.java)
                if (userData != null) {

                    if (userData.name!=null)
                        edtNameProfile.setText(userData?.name)
                    if (userData.email!=null)
                        edtEmalProfile.setText(userData?.email)
                    if (userData.phoneNumber!=null)
                        edtPhoneNumberProfile.setText(userData?.phoneNumber)
                    if (userData.state!=null)
                        edtStateProfile.setText(userData?.state)
                    if (userData.address!=null)
                        edtAddressProfile.setText(userData?.address)
                    if (userData.city!=null)
                        edtCityProfile.setText(userData?.city)
                    if (userData.country!=null)
                        edtCountryProfile.setText(userData?.country)
                    //add DateofBirth
                }


                progressBar?.visibility = View.GONE

                Log.i("tag","");
            }


        }catch (ex: java.lang.Exception)
        {
            Log.e("tag",ex.message)
        }
    }


    private fun listenToDocumentLocal(userLogged:String) {
        // [START listen_document_local]
        val docRef = db.collection("users").document(userLogged)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                progressBar?.visibility = View.GONE
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "$source data: ${snapshot.data}")

                if (!snapshot.data?.get("name").toString().equals("null"))
                    edtNameProfile.setText(snapshot.data?.get("name").toString())

                edtEmalProfile.setText(snapshot.data?.get("email").toString())

                if (!snapshot.data?.get("phoneNumber").toString().equals("null"))
                    edtPhoneNumberProfile.setText(snapshot.data?.get("phoneNumber").toString())

                if (!snapshot.data?.get("state").toString().equals("null"))
                    edtStateProfile.setText(snapshot.data?.get("state").toString())

                if (!snapshot.data?.get("address").toString().equals("null"))
                    edtAddressProfile.setText(snapshot.data?.get("address").toString())

                if (!snapshot.data?.get("city").toString().equals("null"))
                    edtCityProfile.setText(snapshot.data?.get("city").toString())

                if (!snapshot.data?.get("country").toString().equals("null"))
                    edtCountryProfile.setText(snapshot.data?.get("country").toString())
                //edtDateProfile.setText(snapshot.data?.get("dateOfBirth").toString())

                if (!snapshot.data?.get("dateOfBirth").toString().isEmpty())
                {
                    try {

                        val sdf = SimpleDateFormat("MMMM dd,yyyy")
                        //val netDate = Date(snapshot.data?.get("dateOfBirth").toString())
                        //timeBirthDay= (snapshot.data?.get("dateOfBirth") as Timestamp).toDate()
                        val netDate =(snapshot.data?.get("dateOfBirth") as Timestamp).toDate()
                        timeBirthDay=netDate
                        val date =sdf.format(netDate)
                        Log.i("Tag","");
                        edtDateProfile.setText(date)

                    }catch (ex:Exception)
                    {
                        Log.e("Tag",ex.message)
                    }

                }


            } else {
                Log.d("TAG", "$source data: null")
            }

            progressBar?.visibility = View.GONE
        }
        // [END listen_document_local]
    }


    fun disableInput(editText: EditText) {
        editText.inputType = InputType.TYPE_NULL
        editText.setTextIsSelectable(true)
        editText.setOnKeyListener { v, keyCode, event ->
            true // Blocks input from hardware keyboards.
        }
    }

   // var timeProfile= Calendar.getInstance().timeInMillis
    @ServerTimestamp
    var timeBirthDay: Date? = null


    fun datePickerDialog()
    {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        val sec = cal.get(Calendar.SECOND)

        val datepickerdialog: DatePickerDialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
            calendar.add(Calendar.HOUR, hour);
            calendar.add(Calendar.MINUTE, min);
            calendar.add(Calendar.SECOND, sec);
            timeBirthDay = calendar.time
            edtDateProfile.setText("" + returnMonth(monthOfYear) + " " + dayOfMonth+ ", " + year)
        }, y, m, d)

        datepickerdialog.show()
    }




    fun returnMonth(month:Int):String
    {

        when (month) {
            0 -> {return getString(R.string.label_january)}
            1 -> {return getString(R.string.label_february)}
            2 -> {return getString(R.string.label_march)}
            3 -> {return getString(R.string.label_april)}
            4 -> {return getString(R.string.label_may)}
            5 -> {return getString(R.string.label_june)}
            6 -> {return getString(R.string.label_july)}
            7 -> {return getString(R.string.label_august)}
            8 -> {return getString(R.string.label_september)}
            9 -> {return getString(R.string.label_october)}
            10 -> {return getString(R.string.label_november)}
            11 -> {return getString(R.string.label_december)}
            else -> return ""
        }
    }

    /*private operator fun DatatypeConstants.Field.get(monthOfYear: Int) {
        monthOfYear+1
    }*/


    private fun changePasswordScreen()
    {
        (activity as AppCompatActivity).supportActionBar?.hide()

        val bundle = Bundle()
        bundle.putString("userName", "David")

        val fragment: Fragment = ChangePasswordFragment()
        fragment.setArguments(bundle)
        val fm: FragmentManager = getActivity()?.supportFragmentManager!!
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.add(R.id.frame_container, fragment)
        ft.commit()
    }

}

