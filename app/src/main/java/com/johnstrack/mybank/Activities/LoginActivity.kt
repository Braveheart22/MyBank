package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.johnstrack.mybank.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginButtonClicked (view: View) {

    }

    fun createUserButtonClicked (view: View) {
        var createUserIntent = Intent (this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }
}
