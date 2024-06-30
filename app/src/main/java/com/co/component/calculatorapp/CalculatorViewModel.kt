package com.co.component.calculatorapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData<String>("")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData<String>("")
    val result: LiveData<String> = _result

    private val _history = MutableLiveData<MutableList<HistoryItem>>(mutableListOf())
    val history: LiveData<MutableList<HistoryItem>> = _history

    private var memory: Double? = null
    private var lastResult: Double? = null

    fun appendExpression(value: String) {
        // Check if the result is displayed and no new number or operation has been entered
        if (_result.value != "" && _expression.value.isNullOrEmpty()) {
            _expression.value = lastResult.toString() // Use last calculated result
        }

        // Append value to expression
        _expression.value += value
    }

    fun calculateResult() {
        try {
            val resultValue = eval(_expression.value ?: "")
            val formattedResult = resultValue.toString()
            _result.value = formattedResult
            lastResult = resultValue // Store last calculated result
            addHistoryItem(_expression.value ?: "", formattedResult)
            _expression.value = "" // Clear expression after calculation
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun clearExpression() {
        _expression.value = ""
        _result.value = ""

        // Add history only if the expression was not empty before clearing
        if (_expression.value?.isNotEmpty() == true) {
            addHistoryItem(_expression.value ?: "", _result.value ?: "")
        }
    }

    fun storeResultInMemory() {
        _result.value?.toDoubleOrNull()?.let {
            memory = it
        }
    }

    fun recallMemory() {
        memory?.let {
            _expression.value += it.toString()
        }
    }

    private fun addHistoryItem(expression: String, result: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _history.value?.add(0, HistoryItem(expression, result, timestamp))
        _history.value = _history.value // Trigger LiveData update
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.toInt()) -> x += parseTerm()
                        eat('-'.toInt()) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.toInt()) -> x *= parseFactor()
                        eat('/'.toInt()) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.toInt())) return parseFactor()
                if (eat('-'.toInt())) return -parseFactor()

                var x: Double
                val startPos = pos
                when {
                    eat('('.toInt()) -> {
                        x = parseExpression()
                        eat(')'.toInt())
                    }
                    ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt() -> {
                        while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                        x = expression.substring(startPos, pos).toDouble()
                    }
                    else -> throw RuntimeException("Unexpected: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }
}

data class HistoryItem(val expression: String, val result: String, val timestamp: String)