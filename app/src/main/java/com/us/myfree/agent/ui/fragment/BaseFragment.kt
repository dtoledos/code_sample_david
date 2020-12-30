package com.us.myfree.agent.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.act.BaseActivity
import com.us.myfree.agent.ui.act.FreeAgentGetStartedAct
import java.util.regex.Pattern

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

open class BaseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    companion object {
        const val _MissingEmailPassword = 1
        const val _ValidEmail = 2
        const val _UserNotFound = 3
        const val _NetworkError = 4
        const val _NotError = 5
        const val _MissingEmail = 6
        const val _OtherError = 7
        const val _MissingInformation = 8
        const val _PasswordMitmatch = 9
        const val _WeakPassword = 10
        const val _AlreadyRegistered = 11
        const val _ErrorLoginPassword = 12
        const val _MissingPhone = 13
        const val _UpdatePassword = 14

        const val _FreeAgent="FreeAgent"
        const val _IsLogged="Logged"

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_startaclaim_wit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //if (connectivityManager != null) {
        val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        //}
        return false
    }


    fun typeMessage(typeMsg: Int)
    {
        when (typeMsg) {
            _ValidEmail -> {
                errorLoginEmpty(R.string.label_valid_email, R.string.label_valid_email_desc)
            }

            _UserNotFound -> {
                errorLoginEmpty(R.string.label_user_not_found, R.string.label_user_not_found_desc)
            }

            _MissingInformation -> {
                errorLoginEmpty(R.string.label_missing_information, R.string.label_enter_all_fields)
            }

            _PasswordMitmatch -> {
                errorLoginEmpty(R.string.label_passsword_mitmatch, R.string.label_passsword_mitmatch_desc)
            }

            _WeakPassword -> {
                errorLoginEmpty(R.string.label_passsword_weak, R.string.label_passsword_weak_desc)
            }

            _NetworkError -> {
                errorLoginEmpty(R.string.label_network_error, R.string.label_network_error_desc)
            }

            _AlreadyRegistered -> {
                errorLoginEmpty(R.string.label_already_registered, R.string.label_already_registered_desc)
            }

            _MissingEmail -> {
                errorLoginEmpty(R.string.label_missing_email,R.string.label_missing_email_desc)
            }


            _NotError -> {
                errorLoginEmpty(R.string.label_email_sent,R.string.label_link_password)
            }

            _OtherError -> {
                errorLoginEmpty(R.string.label_other_error,R.string.label_other_error_desc)
            }

            _MissingEmailPassword -> {
                errorLoginEmpty(R.string.label_missing_information,R.string.label_missing_email_password)
            }

            _ErrorLoginPassword -> {
                errorLoginEmpty(R.string.label_password_error,R.string.label_password_error_desc)
            }

            _MissingPhone -> {
                errorLoginEmpty(R.string.label_error_phone_number,R.string.label_error_phone_number_desc)
            }

            _UpdatePassword -> {
                errorLoginEmpty(R.string.label_sucess,R.string.label_password_update)
            }
        }
    }

    fun errorLoginEmpty(title: Int, body: Int)
    {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(body)

        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


    fun String.isEmailValid() =
            Pattern.compile(
                    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                            "\\@" +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                            "(" +
                            "\\." +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                            ")+"
            ).matcher(this).matches()

}