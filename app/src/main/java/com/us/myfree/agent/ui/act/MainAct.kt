package com.us.myfree.agent.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.us.myfree.agent.R
import com.us.myfree.agent.ui.fragment.*
import kotlinx.android.synthetic.main.tab_menu.*


class MainAct : BaseActivity() , NavigationView.OnNavigationItemSelectedListener{

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)



        toolbar.setNavigationIcon(R.drawable.ic_hamburger2);


        welcome();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlack));
        setButton()
    }

    fun welcome()
    {
        val bundle = Bundle()
        bundle.putString("userName", "David")

        val fragment: Fragment = StartAClaimWhitnessFrag_01()
        fragment.setArguments(bundle)
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.add(R.id.frame_container, fragment)
        ft.commit()
    }

    fun setButton()
    {
        iv_main.bringToFront()
        iv_main.setOnClickListener {

           supportActionBar?.show()

            tv_profile.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            iv_profile.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

            tv_claim_history.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            iv_claim.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
            toolbar.getNavigationIcon()?.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

            welcome()
            //onBackPressed()
        }

        rl_claim.setOnClickListener {
            tv_claim_history.setTextColor(ContextCompat.getColor(this, R.color.colorBlue4));
            iv_claim.setColorFilter(ContextCompat.getColor(this, R.color.colorBlue4), android.graphics.PorterDuff.Mode.SRC_IN);

            tv_profile.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            iv_profile.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

            toolbar.getNavigationIcon()?.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue4));



            val bundle = Bundle()


            val fragment: Fragment = ClaimHistory()
            fragment.setArguments(bundle)
            val fm: FragmentManager = supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.add(R.id.frame_container, fragment)
            ft.commitNowAllowingStateLoss()
        }

        rl_profile.setOnClickListener {
            tv_profile.setTextColor(ContextCompat.getColor(this, R.color.colorBlue4));
            iv_profile.setColorFilter(ContextCompat.getColor(this, R.color.colorBlue4), android.graphics.PorterDuff.Mode.SRC_IN);

            tv_claim_history.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            iv_claim.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

            //toolbar.getNavigationIcon()?.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            toolbar.getNavigationIcon()?.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_ATOP);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue4));

            val bundle = Bundle()
            bundle.putString("userName", "David")

            val fragment: Fragment = ProfileFragment()
            fragment.setArguments(bundle)
            val fm: FragmentManager = supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.add(R.id.frame_container, fragment)
            ft.commit()

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                //Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainAct, MyPoliciesAct::class.java)
                intent.putExtra("key", "Kotlin")
                startActivity(intent)

            }

            R.id.nav_logout -> {
                logout()
               // Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun logout()
    {
        val sharedPreference =  getSharedPreferences(_FreeAgent, Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putBoolean(_IsLogged,false)
        editor.commit()

        editor.putString(_UserID,"")
        editor.commit()
        finish();

        val intent = Intent(this@MainAct, FreeAgentGetStartedAct::class.java)
        intent.putExtra("key", "Kotlin")
        startActivity(intent)
    }

    override fun onBackPressed() {

        supportActionBar?.show()

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentsSize = fragmentManager.fragments.size
        val fragmentsX = fragmentManager.fragments[fragmentsSize-1]

            if  (fragmentsX is WitnessSomethingFra_02) {
                welcome()
            }
            else if  (fragmentsX is WasThisAccidenteFra_03) {
                supportActionBar?.hide()

                val bundle = Bundle()
                bundle.putString("source", "StartAClaim")

                val fragment: Fragment = com.us.myfree.agent.ui.fragment.WitnessSomethingFra_02()
                fragment.setArguments(bundle)
                val fm: FragmentManager =supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.add(R.id.frame_container, fragment)
                ft.commit()
            }
            else if  (fragmentsX is NaturalAccidentsFrag_04) {
                supportActionBar?.hide()

                val bundle = Bundle()
                bundle.putString("source", "StartAClaim")

                val fragment: Fragment = WasThisAccidenteFra_03()
                fragment.setArguments(bundle)
                val fm: FragmentManager = supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.add(R.id.frame_container, fragment)
                ft.commit()
            }



    }
}



