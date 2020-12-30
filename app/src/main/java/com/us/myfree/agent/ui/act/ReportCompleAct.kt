package com.us.myfree.agent.ui.act

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.us.myfree.agent.R
import kotlinx.android.synthetic.main.report_complete.*


class ReportCompleAct : BaseActivity() {
    var catastrophe: Boolean=false
    var nologged: Boolean=false

    var claimid:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_complete)


        val extras = intent.extras
        if (extras != null) {
            claimid = extras.getString("claimid")
            catastrophe= extras.getBoolean("catastrophe")
            nologged= extras.getBoolean("noLogged")
            tv_claim_id.setText(""+claimid)

            if (nologged)
            {
                tv_message.setText(R.string.label_report_complete_desc)
            }


        }


        iv_close.setOnClickListener {
            /*if (!catastrophe)
            {
                finish()
            }
            else
            {*/

            if (nologged)
            {
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("key", "Kotlin")
                startActivity(intent)
            }
            else
            {
                finish()
                val intent = Intent(this, MainAct::class.java)
                intent.putExtra("key", "Kotlin")
                startActivity(intent)
            }
            /*}*/

        }

        iv_share_sms.setOnClickListener {
            val uri: Uri = Uri.parse("smsto:")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", getString(R.string.label_share_email_msg_number,claimid))
            startActivity(intent)
        }

        iv_share_email.setOnClickListener {
            sendEmail("",getString(R.string.label_free_agent_report_number),getString(R.string.label_share_email_msg_number,claimid))
        }


    }


    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }
}




