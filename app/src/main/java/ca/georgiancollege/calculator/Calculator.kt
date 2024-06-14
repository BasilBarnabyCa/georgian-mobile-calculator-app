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
            binding.divideButton,
            binding.equalsButton
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
                binding.resultTextView.text = String.format("%s%s", formula, currentNumber)
            }

            "delete" -> {
                if (currentNumber.isNotEmpty()) {
                    currentNumber = currentNumber.dropLast(1)
                } else if (formula.isNotEmpty()) {
                    formula = formula.dropLast(1)
                }
                binding.resultTextView.text = if (formula.isEmpty()) "0" else formula
            }

            "plus_minus" -> {
                if (currentNumber.startsWith("-")) {
                    currentNumber = currentNumber.substring(1)
                } else {
                    if (currentNumber.isNotEmpty() && currentNumber != "0") {
                        currentNumber = "-$currentNumber"
                    }
                }
                binding.resultTextView.text = String.format("%s%s", formula, currentNumber)
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
                binding.resultTextView.text = String.format("%s%s", formula, currentNumber)
            }
        }
    }

    private fun attachOperator(tag: String) {
        if (tag == "plus" || tag == "minus" || tag == "multiply" || tag == "divide") {
            if (currentNumber.isNotEmpty()) {
                formula += currentNumber
                currentNumber = ""
            }

            if (!formula.last().isDigit() || formula.last() == '.') {
                formula = formula.dropLast(1)
            }

            formula += tag
            formatFormula()
            binding.resultTextView.text = formula
        }

        // TODO: Need to add repeating formula if possible
        if (tag == "equals" && formula.isNotEmpty()) {
            if (!formula.last().isDigit() || formula.last() == '.') {
                formula = formula.dropLast(1)
            }

            if (currentNumber.isNotEmpty()) {
                formula += currentNumber
                currentNumber = ""
            }

            performCalculation()
        }
        Log.i("currentFormula", "!$formula") // TODO: Remove this
    }

    private fun formatFormula() {
        operatorMap.forEach { (tag, buttonText) ->
            formula = formula.replace(tag, " $buttonText ") // Fix extra space
        }
    }

    private fun performCalculation() {
        Log.i("currentFormula", formula) // TODO: Remove this
        binding.formulaTextView.text = formula
        binding.resultTextView.text = "0" // this should show the answer

        // TODO: REFACTOR REPEATING CODE
        formula = ""
        currentNumber = "0"
    }

    private fun clearScreen() {
        // TODO: REFACTOR REPEATING CODE
        formula = ""
        currentNumber = "0"

        binding.formulaTextView.text = ""
        binding.resultTextView.text = "0"
    }
}
