package com.us.myfree.agent.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.act.ClaimDetailAct
import com.us.myfree.agent.ui.beans.Claim
import com.us.myfree.agent.ui.tools.TextUtil2
import java.text.SimpleDateFormat


class ClaimAdapter : RecyclerView.Adapter<ClaimAdapter.ViewHolder>() {

    var claimLst: MutableList<Claim>  = ArrayList()
    lateinit var context:Context




    fun ClaimAdapter(claimBean : MutableList<Claim>, context: Context){
        this.claimLst = claimBean
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = claimLst.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.claim_row, parent, false))
    }

    override fun getItemCount(): Int {
        return claimLst.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val type = view.findViewById(R.id.tv_type) as TextView
        private val carrierName = view.findViewById(R.id.tv_carrierName) as TextView
        private val expiracionDate = view.findViewById(R.id.tv_expiracionDate) as TextView
        private val policieNumber = view.findViewById(R.id.tv_policyNumberValue) as TextView
        private val imageType= view.findViewById(R.id.iv_imageType) as ImageView
        private val iv_clipboard= view.findViewById(R.id.iv_clipboard) as ImageView


        fun bind(claimBean:Claim, context: Context){


            if (claimBean.wasOrdered!!)
            {
                carrierName.text = context.getString(R.string.label_ordered)
            }
            else
            {
                carrierName.text = context.getString(R.string.label_notordered)
            }

            expiracionDate.text = "Exp:"

            val sdf = SimpleDateFormat("MM/dd/yy")
            val netDate =(claimBean.date as Timestamp).toDate()
            val date =sdf.format(netDate)
            expiracionDate.text = "Exp: $date "

            policieNumber.text = claimBean.claimID

            if (claimBean.claimType?.contains("business",true)!!)
            {
                imageType.setImageResource(R.drawable.ic_business)
                type.text = context.getString(R.string.label_business)
            }
            else if (claimBean.claimType?.contains("auto",true)!!)
            {
                imageType.setImageResource(R.drawable.ic_auto)
                type.text = context.getString(R.string.label_auto)
            }
            else if (claimBean.claimType?.contains("home",true)!!)
            {
                imageType.setImageResource(R.drawable.ic_home)
                type.text = context.getString(R.string.label_home)
            }

            iv_clipboard.setOnClickListener(
                    View.OnClickListener
                    {
                        val us = TextUtil2()
                        us.copyText(claimBean.claimID.toString(), context)
                        policieNumber.text=context.getString(R.string.label_copy_clipboard)
                        Handler().postDelayed(Runnable {
                            policieNumber.text=claimBean.claimID
                        }, 500)
                    }
            )


            itemView.setOnClickListener(
                    View.OnClickListener
                    {
                        val intent = Intent(context, ClaimDetailAct::class.java)



                        if (claimBean.claimType?.contains("business",true)!!)
                        {
                            intent.putExtra("claimType",context.getString(R.string.label_business))
                        }
                        else if (claimBean.claimType?.contains("auto",true)!!)
                        {
                            intent.putExtra("claimType",context.getString(R.string.label_auto))
                        }
                        else if (claimBean.claimType?.contains("home",true)!!)
                        {
                            intent.putExtra("claimType", context.getString(R.string.label_home))
                        }


                        intent.putExtra("date", date)

                        if (claimBean.wasOrdered!!)
                        {
                            intent.putExtra("order",context.getString(R.string.label_ordered))
                        }
                        else
                        {
                            intent.putExtra("order",context.getString(R.string.label_notordered))
                        }


                        intent.putExtra("claimId", claimBean.claimID)
                        context.startActivity(intent)
                    }
            )
        }



    }




}
