package com.johnstrack.mybank.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.johnstrack.mybank.Interfaces.ExpenseDeleteItemClickListener
import com.johnstrack.mybank.Models.Expense
import com.johnstrack.mybank.R
import java.util.*

/**
 * Created by John on 5/17/2018 at 3:27 PM.
 */
class ExpenseAdapter(private val expenses: ArrayList<Expense>, val expenseDeleteItemClickListener: ExpenseDeleteItemClickListener) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenses.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindExpense(expenses[position])
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        private val itemName = itemView?.findViewById<TextView>(R.id.expenseItemName)
        private val price = itemView?.findViewById<TextView>(R.id.expensePrice)
        val deleteBtn = itemView?.findViewById<ImageButton>(R.id.deleteItemBtn)

        fun bindExpense(expense: Expense) {
            itemName?.text = expense.itemName
            price?.text = String.format(Locale.getDefault(),"$%,.2f", expense.price)
            deleteBtn?.setOnClickListener {
                expenseDeleteItemClickListener.expenseDeleteItemBtnClicked(expense)
            }
        }
    }
}