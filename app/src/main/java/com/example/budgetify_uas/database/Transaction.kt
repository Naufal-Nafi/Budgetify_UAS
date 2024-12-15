package com.example.budgetify_uas.database

enum class TransactionSource {
    LOCAL, API
}

data class Transaction(
    val id: String,
    val amount: Double,
    val description: String,
    val date: String,
    val source: TransactionSource
)
