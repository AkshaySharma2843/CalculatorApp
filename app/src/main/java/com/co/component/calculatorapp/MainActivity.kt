package com.co.component.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.co.component.calculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CalculatorViewModel
    private val historyAdapter by lazy { HistoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }

        binding.button0.setOnClickListener { viewModel.appendExpression("0") }
        binding.button1.setOnClickListener { viewModel.appendExpression("1") }
        binding.button2.setOnClickListener { viewModel.appendExpression("2") }
        binding.button3.setOnClickListener { viewModel.appendExpression("3") }
        binding.button4.setOnClickListener { viewModel.appendExpression("4") }
        binding.button5.setOnClickListener { viewModel.appendExpression("5") }
        binding.button6.setOnClickListener { viewModel.appendExpression("6") }
        binding.button7.setOnClickListener { viewModel.appendExpression("7") }
        binding.button8.setOnClickListener { viewModel.appendExpression("8") }
        binding.button9.setOnClickListener { viewModel.appendExpression("9") }
        binding.buttonAdd.setOnClickListener { viewModel.appendExpression("+") }
        binding.buttonSubtract.setOnClickListener { viewModel.appendExpression("-") }
        binding.buttonMultiply.setOnClickListener { viewModel.appendExpression("*") }
        binding.buttonDivide.setOnClickListener { viewModel.appendExpression("/") }
        binding.buttonEquals.setOnClickListener { viewModel.calculateResult() }
        binding.buttonClear.setOnClickListener { viewModel.clearExpression() }
        binding.buttonMemory.setOnClickListener { viewModel.storeResultInMemory() }
        binding.buttonMemoryRecall.setOnClickListener { viewModel.recallMemory() }

        binding.buttonEquals.setOnClickListener {
            viewModel.calculateResult()
        }

        binding.buttonClear.setOnClickListener {
            viewModel.clearExpression()
        }
    }

    private fun setupObservers() {
        viewModel.expression.observe(this, { binding.expressionTextView.text = it })
        viewModel.result.observe(this, { binding.resultTextView.text = it })
        viewModel.history.observe(this) { history ->
            historyAdapter.submitList(history)
            historyAdapter.notifyDataSetChanged() // Notify adapter of data set change
        }
    }
}