package ca.georgiancollege.calculator

import android.content.Context
import android.util.Log
import ca.georgiancollege.calculator.databinding.ActivityMainBinding
import java.util.Stack

class Calculator(dataBinding: ActivityMainBinding, context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var expression: String
    private var answer: String
    private var currentNumber: String
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text),
        "percent" to context.getString(R.string.percent_text)
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
            binding.percentButton,
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
        Log.i("Operator Attached", operator)
        when (operator) {
            "equals" -> {
                if (currentNumber.isNotEmpty()) {
                    expression += currentNumber
                    currentNumber = ""
                }
                answer = solve(formatExpression(expression).trim())
                binding.expressionTextView.text = binding.resultTextView.text
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
                expression += operator
                Log.i("showExpression", "Updated Expression: $expression")  // Add this log
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
        binding.expressionTextView.text = ""
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
        Log.i("showExpression", "Formatted Expression: $formattedExpression")  // Add this log
        return formattedExpression
    }

    private fun rebuildExpression() {

    }

    private fun solve(formattedExpression: String): String {
        val expressionList = formattedExpression.split(" ")

        expressionList.forEach {
            Log.i("Expression", it)
        }

        val postfix = convertToPostfix(expressionList)
        val answer = performCalculation(postfix)

        // Handles display of of decimals if present
        return if (answer % 1 == 0.0) {
            answer.toInt().toString()
        } else {
            answer.toString()
        }
    }

    private fun convertToPostfix(expressionList: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()
        val orderOfPrecedence = mapOf(
            "plus" to 1,
            "minus" to 1,
            "multiply" to 2,
            "divide" to 2,
            "percent" to 3
        )

        for (element in expressionList) {
            Log.i("ConvertToPostfix", "Processing element: $element")
            when {
                element.isNumber() -> output.add(element)
                element.isOperator() -> {
                    while (operators.isNotEmpty() && orderOfPrecedence[operators.peek()]!! >= orderOfPrecedence[element]!!) {
                        output.add(operators.pop())
                    }
                    operators.push(element)
                }

                else -> throw IllegalArgumentException("Unknown element: $element")
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }

    private fun performCalculation(postfixList: List<String>): Double {
        val stack = Stack<Double>()

        for (element in postfixList) {
            when {
                element.isNumber() -> stack.push(element.toDouble())
                element.isOperator() -> {
                    if (element == "percent" && stack.size >= 1) {
                        val num = stack.pop()
                        val previousNumber = stack.pop() // Get the previous number
                        val result = previousNumber * (num / 100) // Apply percentage to previous number
                        stack.push(result)
                        stack.push(previousNumber) // Push back the previous number to the stack
                    } else {
                        val num2 = stack.pop()
                        val num1 = if (stack.isNotEmpty()) stack.pop() else 0.0
                        stack.push(calculate(element, num1, num2))
                    }
                }

                else -> throw IllegalArgumentException("Unknown element: $element")
            }
        }

        return stack.pop()
    }

    private fun calculate(operator: String, num1: Double, num2: Double): Double {
        return when (operator) {
            "plus" -> num1 + num2
            "minus" -> num1 - num2
            "multiply" -> num1 * num2
            "divide" -> num1 / num2
            "percent" -> num1 * (num2 / 100)
            else -> throw UnsupportedOperationException("Operator not supported: $operator")
        }
    }

    // Helpers
    private fun String.isNumber() = this.toDoubleOrNull() != null

    private fun String.isOperator() = this in listOf("plus", "minus", "multiply", "divide", "percent")
}
