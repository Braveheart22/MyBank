package com.johnstrack.mybank.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.mybank.R
import com.johnstrack.mybank.Utilities.*
import kotlinx.android.synthetic.main.activity_add_expense.*

class AddExpenseActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        auth = FirebaseAuth.getInstance()
    }

    fun addExpenseClicked (view: View) {

        val data = HashMap<String, Any>()
        val username = if (auth.currentUser != null) {
            auth.currentUser?.displayName.toString()
        } else {
            "Unknown"
        }

        data[ITEM_NAME] = addItemNameText.text.toString()
        data[PRICE] = addPriceText.text.toString().toDouble()
        data[CATEGORY] = addCategoryText.text.toString()
        data[TIMESTAMP] = FieldValue.serverTimestamp()
        data[USERNAME] = username
        FirebaseFirestore.getInstance().collection(EXPENSES_REF)
                .add(data)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("Error", "Could not add new expense: ${e.localizedMessage}")
                }
    }

    fun cancelBtnClicked (view: View) {
        finish()
    }
}