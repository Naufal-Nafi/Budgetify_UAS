package com.example.budgetify_uas

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.budgetify_uas.databinding.ActivityMainBinding
import com.example.budgetify_uas.model.ViewModel
import com.example.budgetify_uas.sharedPref.PreferenceManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this@MainActivity).get(ViewModel::class.java)
        preferenceManager = PreferenceManager(this@MainActivity)


//        // set tema TODO Ubah Tema
        if (preferenceManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Base_Theme_Budgetify_Dark)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Base_Theme_Budgetify_Light)
        }
//
//        viewModel.syncExpenses()

        with(binding) {
            try {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                bottomNavigationView.setupWithNavController(navController)
            } catch (e: Exception) {
                Log.e("NavigationError", "Error setting up navigation: ${e.message}")
            }

        }
    }

}