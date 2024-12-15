package com.example.budgetify_uas.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetify_uas.API.RetrofitInstance
import com.example.budgetify_uas.R
import com.example.budgetify_uas.database.AppDatabase
import com.example.budgetify_uas.database.Transaction
import com.example.budgetify_uas.database.TransactionSource
import com.example.budgetify_uas.databinding.ItemTransactionBinding
import com.example.budgetify_uas.sharedPref.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val context: Context
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            val currency = PreferenceManager(binding.root.context).getCurrency()

            if (transaction.source == TransactionSource.API) {
                binding.tvAmount.setTextColor(
                    ContextCompat.getColor(binding.root.context,R.color.red_500)
                )
            }
            binding.tvAmount.text = "Amount: $currency ${transaction.amount}"
            binding.tvDescription.text = "Description: ${transaction.description}"
            binding.tvDate.text = "Date: ${transaction.date}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
        holder.binding.imageDelete.setOnClickListener {
            deleteTransaction(transaction)
            if (context is Activity) {
                context.recreate()
            }
            Toast.makeText(context, "Berhasil menghapus data", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        when (transaction.source) {
            TransactionSource.LOCAL -> deleteLocalTransaction(transaction)
            TransactionSource.API -> deleteApiTransaction(transaction)
        }
    }

    private fun deleteLocalTransaction(transaction: Transaction) {
        val executor = Executors.newSingleThreadExecutor()
        val incomeDao = AppDatabase.getInstance(context)?.incomeDao()
        executor.execute {
            incomeDao?.deleteIncome(transaction.id)
        }
    }

    private fun deleteApiTransaction(transaction: Transaction) {
        val expenseApi = RetrofitInstance.getInstance()
        expenseApi.deleteExpense(transaction.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DELETE API", "Transaction deleted successfully")
                } else {
                    Log.e("DELETE API", "Failed to delete transaction: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DELETE API", "Error deleting transaction: ${t.message}")
            }
        })
    }

}