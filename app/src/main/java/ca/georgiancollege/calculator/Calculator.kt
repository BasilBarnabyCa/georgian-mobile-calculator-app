package ca.georgiancollege.calculator

import android.content.Context
import android.util.Log
import ca.georgiancollege.calculator.databinding.ActivityMainBinding

class Calculator(dataBinding: ActivityMainBinding, context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var formula: String
    private var currentNumber: String
    private var isCurrentNumber: Boolean
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text)
    )

    init {
        formula = ""
        currentNumber = "0"
        isCurrentNumber = true
        createButtons()
    }

    private fun createButtons() {
        val operandButtons = arrayOf(
            binding.oneButton,
            binding.twoButton,
            binding.threeButton,
            binding.fourButton,
            binding.fiveButton,
            binding.sixButton,
            binding.sevenButton,
            binding.eightButton,
            binding.nineButton,
            binding.zeroButton,
            binding.decimalButton,
            binding.deleteButton,
            binding.plusMinusButton
        )

        val operatorButtons = arrayOf(
            binding.plusButton,
            binding.minusButton,
            binding.multiplyButton,
            binding.divideButton
        )

        operandButtons.forEach { it.setOnClickListener { attachOperand(it.tag as String) } }
        operatorButtons.forEach { it.setOnClickListener { attachOperator(it.tag as String) } }

        binding.clearButton.setOnClickListener {
            clearScreen()
        }
    }

    private fun attachOperand(tag: String) {
        when (tag) {
            "." -> {
                if (!currentNumber.contains(".")) {
                    if (currentNumber.isEmpty()) {
                        currentNumber = "0."
                    } else {
                        currentNumber += "."
                    }
                }

                // TODO: REFACTOR REPEATING CODE
                binding.formulaTextView.text = String.format("%s%s", formula, currentNumber)
            }

            "delete" -> {
                if (currentNumber.isNotEmpty()) {
                    currentNumber = currentNumber.dropLast(1)
                } else if (formula.isNotEmpty()) {
                    formula = formula.dropLast(1)
                }
                binding.formulaTextView.text = if (formula.isEmpty()) "0" else formula
            }

            "plus_minus" -> {
                if (currentNumber.startsWith("-")) {
                    currentNumber = currentNumber.substring(1)
                } else {
                    if (currentNumber.isNotEmpty() && currentNumber != "0") {
                        currentNumber = "-$currentNumber"
                    }
                }

                // TODO: REFACTOR REPEATING CODE
                binding.formulaTextView.text = String.format("%s%s", formula, currentNumber)
            }

            "clear" -> {
                clearScreen()
            }

            else -> {
                if (currentNumber == "0") {
                    currentNumber = tag
                } else {
                    currentNumber += tag
                }

                // TODO: REFACTOR REPEATING CODE
                binding.formulaTextView.text = String.format("%s%s", formula, currentNumber)
            }
        }
        Log.i("currentNumber", currentNumber)
    }

    private fun attachOperator(tag: String) {
        if (tag == "plus" || tag == "minus" || tag == "multiply" || tag == "divide") {
            if (currentNumber.isNotEmpty()) {
                formula += currentNumber
                currentNumber = ""
            }

            if (formula.isEmpty() || !formula.last().isDigit() || formula.last() == '.') {
                formula = formula.dropLast(1)
            }

            formula += tag
            formatFormula()
            binding.formulaTextView.text = formula
        }
    }

    private fun formatFormula() {
        operatorMap.forEach { (tag, buttonText) ->
            formula = formula.replace(tag, buttonText)
        }
    }

    private fun clearScreen() {
        formula = ""
        currentNumber = "0"
        binding.formulaTextView.text = "0"
    }
}
