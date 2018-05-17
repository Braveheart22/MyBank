package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.mybank.Adapters.ExpenseAdapter
import com.johnstrack.mybank.Models.Expense
import com.johnstrack.mybank.R
import com.johnstrack.mybank.Utilities.CATEGORY
import com.johnstrack.mybank.Utilities.EXPENSES_REF
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenses = arrayListOf<Expense>()
    private val expensesCollectionRef = FirebaseFirestore.getInstance().collection(EXPENSES_REF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            val addExpenseIntent = Intent (this, AddExpenseActivity::class.java)
            startActivity(addExpenseIntent)
        }

        expenseAdapter = ExpenseAdapter(expenses)
        expenseListView.adapter = expenseAdapter
        val layoutManager = LinearLayoutManager(this)
        expenseListView.layoutManager = layoutManager

        expensesCollectionRef.get()
                .addOnSuccessListener { snapshot ->
                    for (document in snapshot.documents) {
                        val data = document.data
                        val category = data?.get(CATEGORY) as String
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Error", "Could not retrieve expenses: ${e.localizedMessage}")
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_expenses -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}