package com.johnstrack.mybank.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.johnstrack.mybank.R

class CreateUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun createUserButtonClicked(view: View) {

    }

    fun cancelButtonClicked (view: View) {
        finish()
    }
}
