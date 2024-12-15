package com.example.budgetify_uas.repository

import com.example.budgetify_uas.API.ExpenseApiService
import com.example.budgetify_uas.database.Expense
import com.example.budgetify_uas.database.Income
import com.example.budgetify_uas.database.IncomeDao


class Repository(
    private val incomeDao: IncomeDao,
    private val api: ExpenseApiService
) {
    val allIncome: List<Income> = incomeDao.getAllIncome()

    suspend fun insertExpense(expense: Expense) {
        api.addExpense(expense) //simpan di server
    }

    suspend fun insertIncome(income: Income) {
        incomeDao.insertIncome(income)
    }

    suspend fun nukeTable() {
        incomeDao.nukeTable()
    }

}