package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.johnstrack.mybank.Adapters.ExpenseAdapter
import com.johnstrack.mybank.Interfaces.ExpenseDeleteItemClickListener
import com.johnstrack.mybank.Models.Expense
import com.johnstrack.mybank.R
import com.johnstrack.mybank.Utilities.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.String.format
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ExpenseDeleteItemClickListener {

    lateinit var auth: FirebaseAuth

    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenses = arrayListOf<Expense>()
    private val expensesCollectionRef = FirebaseFirestore.getInstance().collection(EXPENSES_REF)
    private lateinit var expenseListener: ListenerRegistration
    private var runningTotal = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            val addExpenseIntent = Intent(this, AddExpenseActivity::class.java)
            startActivity(addExpenseIntent)
        }

        expenseAdapter = ExpenseAdapter(expenses, this)
        expenseListView.adapter = expenseAdapter
        val layoutManager = LinearLayoutManager(this)
        expenseListView.layoutManager = layoutManager

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        setListener()
    }

    override fun expenseDeleteItemBtnClicked(expense: Expense) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.delete_item_dialog, null)

        builder.setView(dialogView)
                .setTitle("Delete Item?")
                .setMessage("Are you sure you want to delete this expense item? This cannot be undone.")
                .setNegativeButton("Cancel") { _, _ -> }
                .setPositiveButton("Yes") { _, _ ->
                    expensesCollectionRef.document(expense.documentId).delete()
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Error", "Could not delete expense: ${exception.localizedMessage}")
                            }
                    runningTotal -= expense.price
                    updateRunningTotal()
                }
        builder.show()
    }

    private fun setListener() {
        expenseListener = expensesCollectionRef
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(USERNAME, auth.currentUser?.displayName.toString())
                .addSnapshotListener(this) { snapshot, exception ->
                    if (exception != null) {
                        Log.e("Error", "Could not retrieve expenses: ${exception.localizedMessage}")
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
                            val documentId = document.id

                            val newExpense = Expense(category, itemName, price, timestamp, username, documentId)
                            expenses.add(newExpense)

                            runningTotal += price
                        }
                        updateRunningTotal()
                    }
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.getItem(0)
        if (auth.currentUser == null) {
            menuItem?.title = "Login"
        } else {
            menuItem?.title = "Logout"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            if (auth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                auth.signOut()
                expenses.clear()
                runningTotal = 0.00
                totalSpentLabel.text = format(Locale.getDefault(), "$%,.2f", runningTotal)
                expenseAdapter.notifyDataSetChanged()
            }
            return true
        } else {
            //delete all expenses and update running total
            //Open "Are  you sure?" dialog before deleting
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.delete_item_dialog, null)
            builder.setView(dialogView)
                    .setTitle("Delete All Expenses?")
                    .setMessage("Are you sure you want to delete ALL expense items? This cannot be undone.")
                    .setNegativeButton("Cancel") { _, _ -> }
                    .setPositiveButton("Yes") { _, _ ->
                        deleteCollection { success ->
                            if (success) {
                                runningTotal = 0.00
                                updateRunningTotal()
                            }
                        }
                    }
            builder.show()
        }
        return false
    }

    private fun updateRunningTotal() {
        val totalSpent = findViewById<TextView>(R.id.totalSpentLabel)
        totalSpent.text = format(Locale.getDefault(), "$%,.2f", runningTotal)
        expenseAdapter.notifyDataSetChanged()
    }

    fun deleteCollection(complete: (Boolean) -> Unit) {
        val query = expensesCollectionRef.whereEqualTo(USERNAME, auth.currentUser?.displayName.toString())
        query.get().addOnSuccessListener { snapshot ->
            thread {
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in snapshot) {
                    val docRef = FirebaseFirestore.getInstance().collection(EXPENSES_REF).document(document.id)
                    batch.delete(docRef)
                    }
                    batch.commit()
                            .addOnSuccessListener {
                                complete(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Error", "Could not delete all expenses: ${exception.localizedMessage}")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "Could not retrieve all expenses: ${exception.localizedMessage}")
                }
    }
}