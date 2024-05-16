package com.enkefalostechnologies.calendarpro.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.TaskType
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.ActivityHomeBinding
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.viewModel.HomeActivityViewModel
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {
    private var doubleBackToExitPressedOnce = false

    val viewModel: HomeActivityViewModel by viewModels { HomeActivityViewModel.Factory }

    override fun onViewBindingCreated() {
        if(isUserLoggedIn()){
            amplifyDataModelUtil.fetchTodaySTask(getUserEmail(),deviceId(),{},{})
        }
        setSupportActionBar(binding.toolbar)
        loadFragments()
        if( intent?.extras?.getString("open").equals("createTask", true)){
            openTaskFragmentAndNewTaskBottomSheet()
        }
//        if( intent?.extras?.getBoolean("isReOpenedAfterSignOut")==true){
//            Handler(Looper.getMainLooper()).postDelayed({
//                val fnv = findNavController(R.id.navHost)
//                fnv.navigate(R.id.navigation_hub)
//            },100)
//        }
        //checkSignIn()
        //saveUserDetailsToDb()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        showToast("Please click BACK again to exit")
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun addObserver() {

    }

    override fun removeObserver() {

    }

    override fun onClick(v: View?) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.extras?.getString("open").equals("createTask", true)){
            openTaskFragmentAndNewTaskBottomSheet()
        }else if (intent?.extras?.getString("screen").equals("profile", false)) {
            openSettingFragment()
        } else {
            intent?.let {
                dialog.close()
                Amplify.Auth.handleWebUISignInResponse(it)
            }
        }
    }

    fun signOut() {

        val fnv = findNavController(R.id.navHost)
        fnv.popBackStack(fnv.graph.startDestinationId, true);
        fnv.navigate(R.id.navigation_hub)
//        val intent = intent
//        intent.putExtra("isReOpenedAfterSignOut",true)
//        finish()
//        startActivity(intent)

//        val dialog = dialogLoading()
//        AmplifyUtil.signOut(object : AmplifyUtil.AmplifyListener {
//            override fun onSuccess() {
//                runOnUiThread {
//                    showToast(Constants.LOGOUT_SUCCESS)
//                    preferenceManager.clear(this@HomeActivity)
//                    startActivity(intent)
//                    finishAffinity()
//                }
//            }
//
//            override fun onFailed() {
//                runOnUiThread {
////                    dialog.dismiss()
//                }
//            }
//
//        })
    }

    fun openTaskFragment() {
        findNavController(R.id.navHost).navigate(R.id.action_navigation_hub_to_navigation_task)
    }

    fun openTaskFragmentAndFilterData(taskType: TaskType, monthOrWeek: String) {
        val bundle = Bundle()
        bundle.putString("taskType", taskType.name)
        bundle.putString("monthOrWeek", monthOrWeek)
        findNavController(R.id.navHost).navigate(
            R.id.action_navigation_hub_to_navigation_task,
            bundle
        )
    }
    fun openTaskFragmentAndNewTaskBottomSheet() {
        val bundle = Bundle()
        bundle.putString("open", "createTask")
        findNavController(R.id.navHost).navigate(
            R.id.action_navigation_task_to_navigation_task,
            bundle
        )
    }

    private fun openSettingFragment() {
        findNavController(R.id.navHost).navigate(R.id.action_navigation_hub_to_navigation_setting)
    }

    fun openListTasksFragment(listGroupId: String, title: String) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("listGroupId", listGroupId)
        findNavController(R.id.navHost).navigate(
            R.id.action_navigation_list_to_navigation_list_task,
            bundle
        )
    }

    fun openListFragment() {
        findNavController(R.id.navHost).navigate(R.id.action_navigation_list_task_to_navigation_list)
    }


    private fun loadFragments() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navView: BottomNavigationView = binding.bnvController
        navView.itemIconTintList = null
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_hub,
                R.id.navigation_task,
                R.id.navigation_setting,
                R.id.navigation_list,
                R.id.navigation_list_task
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
//        val navView: BottomNavigationView = binding.bnvController
//        navView.itemIconTintList = null
//        val navController = navHostFragment.navController
//
//// Get the reference to your navigation graph
//        val navGraph = navController.navInflater.inflate(R.navigation.navigation)
//   val condition=intent?.extras?.getBoolean("isReOpenedAfterSignOut")==true
//// Set the start destination ID dynamically
//        val startDestinationId = when (condition) {
//            // Choose your start destination based on a condition
//            true -> {
//                Log.d("StartDestination", "Condition is $condition, using R.id.navigation_hub as start destination")
//                R.id.navigation_hub
//            }
//            false -> {
//                Log.d("StartDestination", "Condition is $condition, using R.id.navigation_task as start destination")
//                R.id.navigation_task
//            }
//        }
//
//// Set the start destination
//        navGraph.setStartDestination(startDestinationId)
//
//// Set up action bar with navigation controller
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//// Set up bottom navigation view with navigation controller
//        navView.setupWithNavController(navController)
//
//// Set the modified graph to the NavController
//        navController.graph = navGraph


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1358->{
                //contact permission

            }
            1234->{
                //notification permission
                var grant = true
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        grant = false
                    }
                }
                if(grantResults.isNotEmpty() && grant) {
                    val params = Bundle()
                    params.putString("device_id", deviceId())
                    params.putString("user_id", getUserEmail())
                    params.putString("status", ((grantResults.isNotEmpty() && grant).toString()))
                    firebaseAnalytics.logEvent(Constants.FIREBASE_EVENT_NOTIFICATION_ACCEPT, params)
                }
            }
        }
    }

//    fun checkSignIn(){
//        viewModel.checkSessionValue()
//        viewModel.getSessionValue().observe(this, Observer { check ->
//            setUserLoggedIn(check)
//        })
//    }

}