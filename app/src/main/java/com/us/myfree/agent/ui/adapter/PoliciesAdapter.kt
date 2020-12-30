package com.us.myfree.agent.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.act.AddPolicies
import com.us.myfree.agent.ui.act.BaseActivity
import com.us.myfree.agent.ui.beans.Policies
import com.us.myfree.agent.ui.tools.TextUtil2
import ru.rambler.libs.swipe_layout.SwipeLayout
import ru.rambler.libs.swipe_layout.SwipeLayout.OnSwipeListener
import java.text.SimpleDateFormat


class PoliciesAdapter : RecyclerView.Adapter<PoliciesAdapter.ViewHolder>() {

    private val COUNT = 30
    private val itemsOffset = IntArray(COUNT)

    var policiesLst: MutableList<Policies>  = ArrayList()
    lateinit var context:Context

    fun PoliciesAdapter(policiesBean : MutableList<Policies>, context: Context){
        this.policiesLst = policiesBean
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = policiesLst.get(position)

        holder.bind(item, context)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        if (holder.adapterPosition !== RecyclerView.NO_POSITION) {
            //itemsOffset.get(holder.adapterPosition) = holder.swipeLayout.offset
            val x= holder.swipeLayout.offset
            itemsOffset.get(holder.adapterPosition) ==x
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.policies_row, parent, false))
    }

    override fun getItemCount(): Int {
        return policiesLst.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type = view.findViewById(R.id.tv_type) as TextView
        val carrierName = view.findViewById(R.id.tv_carrierName) as TextView
        val expiracionDate = view.findViewById(R.id.tv_expiracionDate) as TextView
        val policieNumber = view.findViewById(R.id.tv_policyNumberValue) as TextView
        val imageType= view.findViewById(R.id.iv_imageType) as ImageView
        val rl_poli= view.findViewById(R.id.rl_poli) as RelativeLayout
        val db = FirebaseFirestore.getInstance()

        private val iv_clipboard= view.findViewById(R.id.iv_clipboard) as ImageView
        val swipeLayout= view.findViewById(R.id.swipe_layout) as SwipeLayout

        var open: Boolean = true

        fun bind(policiesBean:Policies, context: Context){
            type.text = policiesBean.policyType?.capitalize()
            carrierName.text = policiesBean.carrierName
            expiracionDate.text = "Exp:"

            val sdf = SimpleDateFormat("MM/dd/yy")
            var date =""
            if (policiesBean.expirationDate!=null)
            {
                val netDate =(policiesBean.expirationDate as Timestamp).toDate()
                date =sdf.format(netDate)
                expiracionDate.text = "Exp: $date "
            }

            policieNumber.text = policiesBean.policyNumber

            if (type.text.toString().equals("business",true))
            {
                imageType.setImageResource(R.drawable.ic_business)
            }
            else if (type.text.toString().equals("auto",true))
            {
                imageType.setImageResource(R.drawable.ic_auto)
            }
            else if (type.text.toString().equals("home",true))
            {
                imageType.setImageResource(R.drawable.ic_home)
            }

            iv_clipboard.setOnClickListener(
                    View.OnClickListener
                    {
                        val us = TextUtil2()
                        us.copyText(policiesBean.policyNumber.toString(), context)
                        policieNumber.text=context.getString(R.string.label_copy_clipboard)
                        Handler().postDelayed(Runnable {
                            policieNumber.text=policiesBean.policyNumber
                        }, 500)
                    }
            )


            //itemView.setOnClickListener(
             rl_poli.setOnClickListener(
                    View.OnClickListener
                    {
                        if (open)
                        {
                            val intent = Intent(context, AddPolicies::class.java)
                            intent.putExtra("edit", true)
                            intent.putExtra("carrierName", policiesBean.carrierName)
                            intent.putExtra("policyNumber", policiesBean.policyNumber)
                            intent.putExtra("policyType", policiesBean.policyType)
                            intent.putExtra("policyName", policiesBean.holderName)
                            intent.putExtra("policyId", policiesBean.documentId)
                            intent.putExtra("policyDate", date)

                            context.startActivity(intent)
                        }

                    }
            )


            swipeLayout.setOnSwipeListener(object : OnSwipeListener {
                override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    Log.i("Tag","")
                }
                override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    //Toast.makeText(swipeLayout.context,(if (moveToRight) "Left" else "Right") + " clamp reached",Toast.LENGTH_SHORT).show()
                    //viewHolder.textViewPos.setText("TADA!")
                    Log.i("Tag",""+adapterPosition)


                    //MyPoliciesAct.deleteRecord(policiesBean.documentId)

                    val sharedPreference =  context.getSharedPreferences(BaseActivity._FreeAgent, Context.MODE_PRIVATE)
                    val userLogged=sharedPreference?.getString(BaseActivity._UserID,"").toString()
                    val policyId= policiesBean.documentId

                    db.collection("users").document(userLogged).collection("policies").document(policyId.toString())
                            .delete()
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                            }
                            .addOnFailureListener {
                                e -> Log.w("TAG", "Error deleting document", e)
                            }

                }

                override fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    Log.i("Tag","")
                    if (moveToRight)
                    {
                        open=false
                    }
                    else
                    {
                        open=true
                    }
                }

                override fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    Log.i("Tag","")
                    if (moveToRight)
                    {
                        open=true
                    }
                    else
                    {
                        open=false
                    }
                }
            })


        }


    }
}
