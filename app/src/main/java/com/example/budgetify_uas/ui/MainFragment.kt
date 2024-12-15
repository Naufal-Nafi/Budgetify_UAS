package com.example.budgetify_uas.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetify_uas.API.ExpenseApiService
import com.example.budgetify_uas.API.RetrofitInstance
import com.example.budgetify_uas.adapter.TransactionAdapter
import com.example.budgetify_uas.database.AppDatabase
import com.example.budgetify_uas.database.Expense
import com.example.budgetify_uas.database.IncomeDao
import com.example.budgetify_uas.database.Transaction
import com.example.budgetify_uas.database.TransactionSource
import com.example.budgetify_uas.databinding.FragmentMainBinding
import com.example.budgetify_uas.sharedPref.PreferenceManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var incomeDao: IncomeDao
    private lateinit var expenseApi: ExpenseApiService
    private lateinit var adapter1: TransactionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = AppDatabase.getInstance(requireContext())
        incomeDao = database!!.incomeDao()!!
        expenseApi = RetrofitInstance.getInstance()

        val currency = PreferenceManager(requireContext()).getCurrency()

        fetchAndDisplayData()
        totalDisplay(currency)
    }

    private fun totalDisplay(currency: String) {
        val executor = Executors.newSingleThreadExecutor()

        // Inisialisasi variabel
        var totalIncome = 0.0
        var totalExpense = 0.0

        // Mengambil total income dari room
        executor.execute {
            totalIncome = incomeDao.getTotalIncome()

            // Update UI dengan hasil total income di UI thread
            if (isAdded && view != null) {
                requireActivity().runOnUiThread {
                    binding.tvTotalIncome.text = "Income: $currency $totalIncome"
                }
            }
        }

        // Mengambil total expense dari API
        expenseApi.getExpenses().enqueue(object : Callback<List<Expense>> {
            override fun onResponse(call: Call<List<Expense>>, response: Response<List<Expense>>) {
                if (response.isSuccessful) {
                    response.body()?.let { expenses ->
                        totalExpense = expenses.sumOf { it.amount }

                        // Update UI dengan hasil total expense di UI thread
                        if (isAdded && view != null) {
                            requireActivity().runOnUiThread {
                                binding.tvTotalExpense.text = "Expense: $currency $totalExpense"
                            }
                        }

                        // Update UI untuk total income + expense di UI thread
                        val totalBoth = totalIncome - totalExpense
                        if (isAdded && view != null) {
                            requireActivity().runOnUiThread {
                                binding.tvTotalBoth.text = "Balance: $currency $totalBoth"
                            }
                        }
                    }
                } else {
                    Log.e("API Error", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Expense>>, t: Throwable) {
                Log.e("API Error", "Failure: ${t.message}")
            }
        })
    }


    private fun fetchAndDisplayData() {
        lifecycleScope.launch {
            // Ambil data dari database lokal dan API
            val localData = fetchLocalData()
            fetchApiData { transactions ->
                val allTransactions = localData + transactions

                // Atur adapter dengan callback penghapusan
                adapter1 = TransactionAdapter(allTransactions, binding.root.context)

                // Tampilkan data di RecyclerView
                binding.rvTransactions.apply {
                    adapter = adapter1
                    layoutManager = LinearLayoutManager(context)
                }

                // Tampilkan data ke RecyclerView
                updateRecyclerView(allTransactions)
            }
        }
    }

    private fun fetchLocalData(): List<Transaction> {
        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit<List<Transaction>> {
            val listIncome = incomeDao.getAllIncome()
            return@submit listIncome.map { income ->
                Transaction(
                    id = income.id.toString(),
                    amount = income.amount,
                    description = income.description!!,
                    date = income.date,
                    source = TransactionSource.LOCAL
                )
            }
        }
        return future.get()
    }


    private fun fetchApiData(callback: (List<Transaction>) -> Unit) {
        val listExpense = mutableListOf<Transaction>()

        // Menjalankan permintaan API secara asinkron
        expenseApi.getExpenses().enqueue(object : Callback<List<Expense>> {
            override fun onResponse(
                call: Call<List<Expense>>,
                response: Response<List<Expense>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        listExpense.addAll(it.map { expense ->
                            Transaction(
                                id = expense.id!!,
                                amount = expense.amount,
                                description = expense.description!!,
                                date = expense.date,
                                source = TransactionSource.API
                            )
                        })
                    }
                    callback(listExpense)
                } else {
                    Log.e("API Error", "Error: ${response.message()}")
                    callback(listExpense)
                }
            }

            override fun onFailure(call: Call<List<Expense>>, t: Throwable) {
                Log.e("API Error", "Failure: ${t.message}")
                callback(listExpense)
            }
        })
    }



    private fun updateRecyclerView(transactions: List<Transaction>) {
        adapter1 = TransactionAdapter(transactions, binding.root.context)

        binding.rvTransactions.apply {
            adapter = adapter1
            layoutManager = LinearLayoutManager(binding.root.context)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}