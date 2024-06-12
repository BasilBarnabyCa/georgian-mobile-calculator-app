package ca.georgiancollege.calculator

import android.content.Context
import ca.georgiancollege.calculator.databinding.ActivityMainBinding

class Calculator(dataBinding: ActivityMainBinding, context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var formula: String
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text)
    )

    init {
        formula = "0"
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
//            binding.clearButton,
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
                // found a way to track decimals within each number

                if (!binding.formulaTextView.text.contains(".")) {
                    formula += if (formula.isEmpty() || !formula.last().isDigit()) "0." else "."
                    binding.formulaTextView.text = formula
                }
            }

            "delete" -> {
                formula = formula.dropLast(1)
                binding.formulaTextView.text =
                    if (formula.isEmpty() || formula == "-") "0" else formula
                formula = if (formula.isEmpty()) "0" else formula
            }

            "plus_minus" -> {
                if (formula.startsWith("-")) {
                    formula = formula.substring(1)
                } else {
                    if (formula.isNotEmpty() && formula != "0") {
                        formula = "-".plus(formula)
                    }
                }
                binding.formulaTextView.text = formula
            }

            "clear" -> {
                clearScreen()
            }

            else -> {
                if (binding.formulaTextView.text == "0") {
                    formula = tag
                    binding.formulaTextView.text = formula
                } else {
                    formula += tag
                    binding.formulaTextView.text = formula
                }
            }
        }
    }

    private fun attachOperator(tag: String) {
        if (tag == "plus" || tag == "minus" || tag == "multiply" || tag == "divide") {
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
        formula = "0"
        formula = ""
        binding.formulaTextView.text = "0"
    }
}