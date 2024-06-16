package ca.georgiancollege.calculator

import android.content.Context
import android.util.Log
import ca.georgiancollege.calculator.databinding.ActivityMainBinding

class Calculator(dataBinding: ActivityMainBinding, context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var expression: String
    private var answer: String
    private var currentNumber: String
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text)
    )

    init {
        expression = ""
        answer = "0"
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
                answer = solve(formatExpression(expression))
                binding.resultTextView.text = answer
                expression = ""
            }

            "plus_minus" -> {
                if (currentNumber.isNotEmpty()) {
                    Log.i("plusMinus", currentNumber)
                    currentNumber = if (currentNumber.startsWith("-")) {
                        currentNumber.substring(1)
                    } else {
                        "-$currentNumber"
                    }
                    updateResultWithOperand()
                }
            }

            else -> {
                if (currentNumber.isNotEmpty()) {
                    expression += currentNumber
                    currentNumber = ""
                }

//                val operatorSymbol = operatorMap[operator] ?: ""
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
                deleteCharacter()
            }
        }
    }

    private fun updateResultWithOperand() {
        binding.resultTextView.text = formatResult(expression + currentNumber)
    }

    private fun updateResultWithExpression() {
        binding.resultTextView.text = formatResult(expression)
    }

    private fun clearScreen() {
        expression = ""
        answer = "0"
        currentNumber = ""
        binding.resultTextView.text = "0"
    }

    private fun deleteCharacter() {
        if (binding.resultTextView.text.length > 1) {
            if (binding.resultTextView.text.last() == ' ') {
                binding.resultTextView.text =
                    binding.resultTextView.text.substring(0, binding.resultTextView.text.length - 2)
            } else {
                binding.resultTextView.text =
                    binding.resultTextView.text.substring(0, binding.resultTextView.text.length - 1)
            }
        } else {
            clearScreen()
        }
        // TODO: Need to make this function
        rebuildExpression()
    }

    private fun solve(formattedExpression: String): String {
        Log.i("resultTracker", "")
        Log.i("resultTracker", "Action: Equals")
        Log.i("resultTracker", "Expression: $expression")
        Log.i("resultTracker", "Formatted Expression: $formattedExpression")
        Log.i("resultTracker", "Current Result: $answer")
        Log.i("resultTracker", "Result Text View: ${binding.resultTextView.text}")
        Log.i("resultTracker", "End of Action: Equals")
        return "10" // Placeholder for actual result
    }

    private fun formatResult(expression: String): String {
        var formattedResult = expression
        operatorMap.forEach { (operator, buttonText) ->
            formattedResult = formattedResult.replace(operator, " $buttonText ") // Fix extra space
        }
        return formattedResult
    }


    // Used to format expression before solving
    private fun formatExpression(expression: String): String {
        var formattedExpression = expression
        operatorMap.forEach { (operator) ->
            formattedExpression = formattedExpression.replace(operator, " $operator ")
        }
        return formattedExpression
    }

    private fun rebuildExpression() {

    }
}
