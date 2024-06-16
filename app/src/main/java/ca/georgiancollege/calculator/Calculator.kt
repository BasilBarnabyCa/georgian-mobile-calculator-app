package ca.georgiancollege.calculator

import android.content.Context
import android.util.Log
import ca.georgiancollege.calculator.databinding.ActivityMainBinding
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

class Calculator(dataBinding: ActivityMainBinding, private val context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var expression: String
    private var result: String
    private var currentNumber: String
    private var lastOperator: String
    private val operatorMap = mapOf(
        "plus" to context.getString(R.string.plus_text),
        "minus" to context.getString(R.string.minus_text),
        "multiply" to context.getString(R.string.multiply_text),
        "divide" to context.getString(R.string.divide_text),
        "percent" to context.getString(R.string.percent_text),
        "squared" to "^2",
        "square_root" to "sqrt",
        "cubed" to "^3"
    )

    init {
        expression = ""
        result = "0"
        currentNumber = ""
        lastOperator = ""
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
            binding.sqrButton,
            binding.sqrRootButton,
            binding.cubeButton,
            binding.equalsButton
        )

        val actionButtons = arrayOf(
            binding.clearButton, binding.deleteButton
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
                result = solve(formatExpression(expression).trim())
                binding.expressionTextView.text = binding.resultTextView.text
                binding.resultTextView.text = result
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
        result = "0"
        currentNumber = ""
        binding.resultTextView.text = "0"
        binding.expressionTextView.text = ""
    }

    private fun deleteCharacter() {
        val resultText = binding.resultTextView.text.toString()

        if (resultText.isNotEmpty()) {
            binding.resultTextView.text = resultText.dropLast(1).trimEnd()

            if (currentNumber.isNotEmpty()) {
                currentNumber = currentNumber.dropLast(1)
            } else if (expression.isNotEmpty()) {
                expression = expression.dropLast(1).trimEnd()
                if (expression.isNotEmpty() && expression.last().toString().isOperatorSymbol()) {
                    expression = expression.dropLast(1).trimEnd()
                }
            }

            rebuildExpression()
        } else {
            clearScreen()
        }
    }

    private fun formatResult(expression: String): String {
        var formattedResult = expression
        operatorMap.forEach { (operator, buttonText) ->
            formattedResult = formattedResult.replace(operator, " $buttonText ")
        }
        return formattedResult.trim()
    }

    // Used to format expression before solving
    private fun formatExpression(expression: String): String {
        var formattedExpression = expression
        operatorMap.forEach { (operator) ->
            if (operator == "squared" || operator == "square_root" || operator == "cubed") {
                formattedExpression = formattedExpression.replace(operator, " $operator")
            } else {
                formattedExpression = formattedExpression.replace(operator, " $operator ")
            }
        }
        Log.i("showExpression", "Formatted Expression: $formattedExpression")  // Add this log
        return formattedExpression.trim()
    }

    private fun rebuildExpression() {
        val resultViewText = binding.resultTextView.text.toString().trim()

        if (resultViewText.isEmpty()) {
            clearScreen()
            return
        }

        Log.i("rebuiltExpression", "Formatted Result: $resultViewText")

        expression = ""

        // Filtering out parts with empty strings
        val resultParts = resultViewText.split(" ").filter { it.isNotEmpty() }
        for (part in resultParts) {
            when {
                part.isNumber() -> expression += part
                part.isOperatorSymbol() -> expression += part.toOperatorText()
            }
        }

        Log.i("rebuiltExpression", "New Expression: $expression")
    }

    private fun solve(formattedExpression: String): String {
        val expressionList = formattedExpression.split(" ")
        val postfix = convertToPostfix(expressionList)
        val solution = performCalculation(postfix)

        // Handles display of of decimals if present
        return if (solution % 1 == 0.0) {
            solution.toInt().toString()
        } else {
            solution.toString()
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
            "percent" to 3,
            "squared" to 4,
            "square_root" to 4,
            "cubed" to 4
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
                    // Handling Unary vs Binary Operations
                    if (element in listOf("squared", "square_root", "cubed", "percent")) {
                        val num = stack.pop()
                        stack.push(calculate(element, num))
                    } else {
                        val num2 = stack.pop()
                        val num1 = stack.pop()
                        stack.push(calculate(element, num1, num2))
                    }
                }

                else -> throw IllegalArgumentException("Unknown element: $element")
            }
        }

        return stack.pop()
    }

    private fun calculate(operator: String, num: Double): Double {
        return when (operator) {
            "squared" -> num.pow(2)
            "square_root" -> sqrt(num)
            "cubed" -> num.pow(3)
            "percent" -> num / 100
            else -> throw UnsupportedOperationException("Operator not supported: $operator")
        }
    }

    private fun calculate(operator: String, num1: Double, num2: Double): Double {
        return when (operator) {
            "plus" -> num1 + num2
            "minus" -> num1 - num2
            "multiply" -> num1 * num2
            "divide" -> num1 / num2
            else -> throw UnsupportedOperationException("Operator not supported: $operator")
        }
    }

    // Helpers
    private fun String.isNumber() = this.toDoubleOrNull() != null

    private fun String.isOperator() = this in listOf(
        "plus", "minus", "multiply", "divide", "percent", "squared", "square_root", "cubed"
    )

    private fun String.isOperatorSymbol() = this in listOf(
        context.getString(R.string.plus_text),
        context.getString(R.string.minus_text),
        context.getString(R.string.multiply_text),
        context.getString(R.string.divide_text),
        context.getString(R.string.percent_text),
        "^2",
        "sqrt",
        "^3"
    )

    private fun String.toOperatorText(): String {
        return when (this) {
            context.getString(R.string.plus_text) -> "plus"
            context.getString(R.string.minus_text) -> "minus"
            context.getString(R.string.multiply_text) -> "multiply"
            context.getString(R.string.divide_text) -> "divide"
            context.getString(R.string.percent_text) -> "percent"
            "^2" -> "squared"
            "sqrt" -> "square_root"
            "^3" -> "cubed"
            else -> ""
        }
    }
}
