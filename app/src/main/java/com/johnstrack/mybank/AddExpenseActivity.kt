package com.johnstrack.mybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
    }

    fun addExpenseClicked (view: View) {

    }

    fun cancelBtnClicked (view: View) {
        finish()
    }
}
