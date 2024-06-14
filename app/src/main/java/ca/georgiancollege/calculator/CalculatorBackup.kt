package ca.georgiancollege.calculator

import android.content.Context
import android.util.Log
import ca.georgiancollege.calculator.databinding.ActivityMainBinding

class CalculatorBackup(dataBinding: ActivityMainBinding, context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var expression: String
    private var formattedExpression: String
    private var currentNumber: String
    private var isCurrentNumber: Boolean
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text)
    )

    init {
        expression = ""
        formattedExpression = ""
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
                binding.resultTextView.text = String.format("%s%s", formattedExpression, currentNumber)
            }

            "delete" -> {
                if (currentNumber.isNotEmpty()) {
                    currentNumber = currentNumber.dropLast(1)
                } else if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    formattedExpression = formattedExpression.dropLast(1)
                }
                binding.resultTextView.text = if (formattedExpression.isEmpty()) "0" else formattedExpression
            }

            "plus_minus" -> {
                if (currentNumber.startsWith("-")) {
                    currentNumber = currentNumber.substring(1)
                } else {
                    if (currentNumber.isNotEmpty() && currentNumber != "0") {
                        currentNumber = "-$currentNumber"
                    }
                }
                binding.resultTextView.text = String.format("%s%s", formattedExpression, currentNumber)
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
                binding.resultTextView.text = String.format("%s%s", formattedExpression, currentNumber)
            }
        }
    }

    private fun attachOperator(tag: String) {
        if (tag == "plus" || tag == "minus" || tag == "multiply" || tag == "divide") {
            if (currentNumber.isNotEmpty()) {
                expression += currentNumber
                formattedExpression += "$currentNumber "
                currentNumber = ""
            }

            if (!expression.last().isDigit() || expression.last() == '.') {
                expression = expression.dropLast(1)
                formattedExpression = formattedExpression.dropLast(2)
            }

            expression += tag
            formattedExpression += "${operatorMap[tag]} "
            binding.resultTextView.text = formattedExpression
        }

        // Handle equals
        if (tag == "equals" && expression.isNotEmpty()) {
            if (currentNumber.isNotEmpty()) {
                expression += currentNumber
                formattedExpression += currentNumber
                currentNumber = ""
            } else if (!expression.last().isDigit()) {
                expression = expression.dropLast(1)
                formattedExpression = formattedExpression.dropLast(2)
            }

//            formatExpression()
            Log.i("currentFormula", "!$expression") // TODO: Remove this
            performCalculation()
        }

    }

    private fun formatExpression() {

        // Add white spaces and unicode if there are any operators
        formattedExpression = ""
        expression.forEach { character ->
            val operator = character.toString()
            formattedExpression += if (operatorMap.containsKey(operator)) {
                " ${operatorMap[operator]} "
            } else {
                operator
            }
        }

        // Add unicode for operators only
        operatorMap.forEach { (tag, buttonText) ->
            expression = expression.replace(tag, buttonText) // Fix extra space
        }
    }

    private fun performCalculation() {
        Log.i("currentFormula", expression) // TODO: Remove this
        binding.formulaTextView.text = formattedExpression
        binding.resultTextView.text = "0" // this should show the answer

        // TODO: REFACTOR REPEATING CODE
        expression = ""
        currentNumber = "0"
    }

    private fun clearScreen() {
        // TODO: REFACTOR REPEATING CODE
        expression = ""
        currentNumber = "0"
        formattedExpression = ""
        binding.formulaTextView.text = ""
        binding.resultTextView.text = "0"
    }
}
