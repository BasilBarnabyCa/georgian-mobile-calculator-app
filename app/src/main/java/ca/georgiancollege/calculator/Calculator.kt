package ca.georgiancollege.calculator

import android.content.Context
import android.util.Log
import ca.georgiancollege.calculator.databinding.ActivityMainBinding

class Calculator(dataBinding: ActivityMainBinding, context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var expression: String
    private var result: String
    private var currentNumber: String
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text)
    )

    init {
        expression = ""
        result = "0"
        currentNumber = ""
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
            binding.decimalButton
        )

        val operatorButtons = arrayOf(
            binding.plusMinusButton,
            binding.plusButton,
            binding.minusButton,
            binding.multiplyButton,
            binding.divideButton,
            binding.equalsButton
        )

        val actionButtons = arrayOf(
            binding.clearButton,
            binding.deleteButton
        )

        operandButtons.forEach { it.setOnClickListener { attachOperand(it.tag as String) } }
        operatorButtons.forEach { it.setOnClickListener { attachOperator(it.tag as String) } }
        actionButtons.forEach { it.setOnClickListener { useAction(it.tag as String) } }
    }

    private fun attachOperand(operand: String) {
        when (operand) {
            "." -> {
                if (!currentNumber.contains(".")) {
                    if (currentNumber.isEmpty()) {
                        currentNumber = "0."
                    } else {
                        currentNumber += "."
                    }
                }
            }

            else -> {
                if (currentNumber == "0" && operand == "0") {
                    return
                }
                currentNumber += operand
            }
        }
        updateResultWithOperand()
    }

    private fun attachOperator(operator: String) {
        when (operator) {
            "equals" -> {
                if (currentNumber.isNotEmpty()) {
                    expression += currentNumber
                    currentNumber = ""
                }
                val formattedExpression = formatExpression(expression)
                result = solve(formattedExpression)
            }

            "plus_minus" -> {
                if (currentNumber.isNotEmpty()) {
                    if (currentNumber.startsWith("-")) {
                        currentNumber = currentNumber.substring(1)
                    } else {
                        currentNumber = "-$currentNumber"
                    }
                    updateResultWithOperand()
                }
            }

            else -> {
                if (currentNumber.isNotEmpty()) {
                    expression += currentNumber
                    currentNumber = ""
                }
                expression += operator
                updateResultWithExpression()
            }
        }
    }

    private fun useAction(action: String) {
        when (action) {
            "clear" -> {
                clearScreen()
            }

            "delete" -> {
                // Implement delete logic here
            }
        }
    }

    private fun updateResultWithOperand() {
        result = String.format("%s%s", expression, currentNumber)
        binding.resultTextView.text = formatResult(result)
    }

    private fun updateResultWithExpression() {
        result = expression
        binding.resultTextView.text = formatResult(result)
    }

    private fun clearScreen() {
        expression = ""
        result = "0"
        currentNumber = ""
        binding.resultTextView.text = "0"
    }

    private fun solve(formattedExpression: String): String {
        Log.i("currentExpression", formattedExpression)
        return "10"
    }

    private fun formatResult(result: String): String {
        var formattedResult = result
        operatorMap.forEach { (operator, buttonText) ->
            formattedResult = formattedResult.replace(operator, " $buttonText ") // Fix extra space
        }
        Log.i("currentResult", formattedResult)
        return formattedResult
    }

    private fun formatExpression(expression: String): String {
        var formattedExpression = expression
        operatorMap.forEach { (operator) ->
            formattedExpression =
                formattedExpression.replace(operator, " $operator ") // Fix extra space
        }
        return formattedExpression
    }
}
