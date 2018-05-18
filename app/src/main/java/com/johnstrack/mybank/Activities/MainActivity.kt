package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.johnstrack.mybank.Adapters.ExpenseAdapter
import com.johnstrack.mybank.Models.Expense
import com.johnstrack.mybank.R
import com.johnstrack.mybank.Utilities.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.String.format
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenses = arrayListOf<Expense>()
    private val expensesCollectionRef = FirebaseFirestore.getInstance().collection(EXPENSES_REF)
    private lateinit var expenseListener : ListenerRegistration
    private var runningTotal = 0.00

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

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    override fun onResume() {
        super.onResume()
        setListener()
    }

    private fun setListener () {
        expenseListener = expensesCollectionRef
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(USERNAME, "Some User")
                .addSnapshotListener(this) { snapshot, exception ->
            if (exception != null) {
                Log.e("Error", "Could nor retrieve expenses: ${exception.localizedMessage}")
            }

            if (snapshot != null) {
                expenses.clear()
                runningTotal = 0.00
                for (document in snapshot.documents) {
                    val data = document.data
                    val category = data!![CATEGORY] as String
                    val itemName = data[ITEM_NAME] as String
                    val price = data[PRICE] as Double
                    val timestamp = data[TIMESTAMP] as Date
                    val username = data[USERNAME] as String

                    val newExpense = Expense(category, itemName, price, timestamp, username)
                    expenses.add(newExpense)

                    runningTotal += price
                }
                val totalSpent = findViewById<TextView>(R.id.totalSpentLabel)
                totalSpent.text = format (Locale.getDefault(),"$%,.2f", runningTotal)
                expenseAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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