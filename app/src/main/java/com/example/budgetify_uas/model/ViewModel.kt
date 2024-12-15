package com.example.budgetify_uas.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetify_uas.API.RetrofitInstance
import com.example.budgetify_uas.database.AppDatabase
import com.example.budgetify_uas.database.Expense
import com.example.budgetify_uas.database.Income
import com.example.budgetify_uas.repository.Repository
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var repository : Repository

    private val executor: Executor = Executors.newSingleThreadExecutor()
    lateinit var allIncome: List<Income>

    init {
        executor.execute {
            val incomeDao = AppDatabase.getInstance(application).incomeDao()
            repository = Repository(incomeDao, RetrofitInstance.getInstance())
            allIncome = repository.allIncome
        }
    }

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun insertIncome(income: Income) = viewModelScope.launch {
        repository.insertIncome(income)
    }

    fun nukeTable() = viewModelScope.launch {
        repository.nukeTable()
    }
}