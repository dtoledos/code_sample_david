package com.us.myfree.agent.ui.act

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.us.myfree.agent.R
import java.util.regex.Pattern

open class BaseActivity : AppCompatActivity() {

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
        const val _SavePolicy=14
        const val _TakePhotoEmpty=15


        const val _FreeAgent="FreeAgent"
        const val _IsLogged="Logged"
        const val _UserID="UserID"
        const val _UserEmail="UserEmail"
        const val _UserPhone="Phone"




    }


    private var progressBar: ProgressBar? = null

    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressBar()
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

    fun errorLoginEmpty(title: Int, body: Int)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
        }



        builder.show()
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

            _SavePolicy -> {
                errorLoginEmpty(R.string.label_sucess2,R.string.title_policy_saved)
            }

            _TakePhotoEmpty -> {
                errorLoginEmpty(R.string.label_other_error,R.string.title_take_message)
            }

        }
    }


}