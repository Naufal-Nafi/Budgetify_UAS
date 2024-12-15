package com.example.budgetify_uas.ui

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.budgetify_uas.R
import com.example.budgetify_uas.database.Expense
import com.example.budgetify_uas.database.Income
import com.example.budgetify_uas.databinding.FragmentAddBinding
import com.example.budgetify_uas.model.ViewModel
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: ViewModel
    private var category: String = "Pengeluaran"

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
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this@AddFragment).get(ViewModel::class.java)
        with(binding) {
            etDate.setOnClickListener {
                showDatePickerDialog()
            }

            btnPendapatan.setOnClickListener {
                category = "Pendapatan"

                etAmount.setBackgroundResource(R.drawable.edit_background_income)
                etDescription.setBackgroundResource(R.drawable.edit_background_income)
                etDate.setBackgroundResource(R.drawable.edit_background_income)
                btnSave.setBackgroundResource(R.drawable.button_background_income)
            }

            btnPengeluaran.setOnClickListener {
                category = "Pengeluaran"

                etAmount.setBackgroundResource(R.drawable.edit_background_expense)
                etDescription.setBackgroundResource(R.drawable.edit_background_expense)
                etDate.setBackgroundResource(R.drawable.edit_background_expense)
                btnSave.setBackgroundResource(R.drawable.button_background_expense)
            }

            btnSave.setOnClickListener {
                val amount = etAmount.text.toString().toDoubleOrNull()
                val description = etDescription.text.toString()
                val date = etDate.text.toString()

                if (amount == null || description.isEmpty() || date.isEmpty()) {
                    Toast.makeText(requireContext(), "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
                } else {
                    if (category.equals("Pengeluaran")) {
                        val expense = Expense(
                            amount = amount,
                            description = description,
                            date = date
                        )
                        viewModel.insertExpense(expense)
                    } else {
                        val income = Income(
                            amount = amount,
                            description = description,
                            date = date
                        )
                        viewModel.insertIncome(income)
                    }
                }
                // clear form
                etAmount.setText("")
                etDescription.setText("")
                etDate.setText("")
                Toast.makeText(binding.root.context, "Berhasil menambahkan data", Toast.LENGTH_SHORT).show()
            }


            fabNukeTable.setOnClickListener {
                viewModel.nukeTable()
            }

        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(binding.root.context, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(selectedYear - 1900, selectedMonth, selectedDay))
            // Set tanggal ke EditText
            binding.etDate.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}