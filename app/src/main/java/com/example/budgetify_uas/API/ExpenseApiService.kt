package com.example.budgetify_uas.API

import com.example.budgetify_uas.database.Expense
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExpenseApiService {
    @GET("expenses")
    fun getExpenses(): Call<List<Expense>>

    @POST("expenses")
    suspend fun addExpense(@Body expense: Expense)

    @DELETE("expenses/{id}")
    fun deleteExpense(@Path("id") id: String): Call<Void>
}