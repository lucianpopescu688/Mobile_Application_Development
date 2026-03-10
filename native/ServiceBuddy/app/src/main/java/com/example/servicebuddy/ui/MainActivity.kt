package com.example.servicebuddy.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.servicebuddy.R
import com.google.android.material.appbar.MaterialToolbar
import androidx.lifecycle.Observer 

class MainActivity : AppCompatActivity() {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController


        val statusBanner = findViewById<TextView>(R.id.tvServerStatusBanner)
        viewModel.isOnline.observe(this) { isOnline ->
            if (isOnline) {
                statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.status_green))
                statusBanner.text = "SERVER ONLINE 🟢"
                statusBanner.visibility = View.VISIBLE
            } else {
                statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.status_red))
                statusBanner.text = "SERVER OFFLINE 🔴"
                statusBanner.visibility = View.VISIBLE
            }
        }

        
        val topNotificationView = findViewById<View>(R.id.top_notification_view)
        val topNotificationTextView = findViewById<TextView>(R.id.top_notification_text)

        viewModel.snackbarMessage.observe(this) { message ->
            message?.let {
                topNotificationTextView.text = it
                topNotificationView.visibility = View.VISIBLE
                topNotificationView.postDelayed({
                    topNotificationView.visibility = View.GONE
                }, 3000) 
                viewModel.onSnackbarMessageShown()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
