package com.example.budgetify_uas.database

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Expense(
    @PrimaryKey
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String? = null
)


