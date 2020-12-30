package com.us.myfree.agent.ui.act

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.us.myfree.agent.R
import kotlinx.android.synthetic.main.activity_add_policies.iv_back_pol
import kotlinx.android.synthetic.main.activity_claim_detail.*
import kotlinx.android.synthetic.main.activity_login.progressBar

class ClaimDetailAct : BaseActivity() {

    var sTitle=false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim_detail)
        setProgressBar(progressBar)

        val extras = intent.extras
        if (extras != null) {

                val claimType = extras.getString("claimType")
                val date = extras.getString("date")
                val order = extras.getString("order")
                val claimId = extras.getString("claimId")

                tv_claim_type_value.setText(claimType);
                tv_claim_date_value.setText(date);
                tv_order_status_value.setText(order);
                tv_claim_id_value.setText(""+claimId);
        }

        rl_order_report.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://myfreeagent.app/")
            startActivity(openURL)
        }

        iv_back_pol.setOnClickListener {
           // returnList()
            finish()
        }




    }





    public override fun onStart() {
        super.onStart()

    }

    public override fun onStop() {
        super.onStop()
        progressBar?.visibility = View.INVISIBLE
    }


}