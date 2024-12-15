package com.example.budgetify_uas.sharedPref

import android.content.Context

class PreferenceManager(context: Context) {
    private val preferences = context.getSharedPreferences("BudgetifyPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "key_theme"
        private const val KEY_CURRENCY = "key_currency"
    }


    fun setTheme(isDarkMode: Boolean) {
        preferences.edit().putBoolean(KEY_THEME, isDarkMode).apply()
    }


    fun isDarkMode(): Boolean {
        return preferences.getBoolean(KEY_THEME, false) // Default-nya Light Mode
    }


    fun setCurrency(currency: String) {
        preferences.edit().putString(KEY_CURRENCY, currency).apply()
    }


    fun getCurrency(): String {
        return preferences.getString(KEY_CURRENCY, "USD") ?: "USD"
    }
}
