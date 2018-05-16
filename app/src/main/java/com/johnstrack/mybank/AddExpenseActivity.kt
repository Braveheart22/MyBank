package com.johnstrack.mybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.mybank.Utilities.EXPENSES_REF
import kotlinx.android.synthetic.main.activity_add_expense.*

class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
    }

    fun addExpenseClicked (view: View) {

        val data = HashMap<String, Any>()
        data["itemName"] = addCategoryText.text.toString()
        data["price"] = addPriceText.text.toString()
        data["category"] = addCategoryText.text.toString()
        data["timestamp"] = FieldValue.serverTimestamp()
        data["username"] = "Some User"
        FirebaseFirestore.getInstance().collection(EXPENSES_REF).add(data)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("Error", "Could not add data: ${e.localizedMessage}")
                }
    }

    fun cancelBtnClicked (view: View) {
        finish()
    }
}
