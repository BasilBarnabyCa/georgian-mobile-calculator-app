package ca.georgiancollege.calculator

import android.content.Context
import ca.georgiancollege.calculator.databinding.ActivityMainBinding
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Calculator class provides functionality for a basic calculator with support for various operations
 * including addition, subtraction, multiplication, division, percentage, square, square root, and cube; adhering to the BEDMAS rule.
 *
 * This class manages the user input, updates the display, and handles the calculation logic.
 *
 * @param dataBinding The data binding for the activity.
 * @param context The context of the application or activity.
 */
class Calculator(dataBinding: ActivityMainBinding, private val context: Context) {

    private var binding: ActivityMainBinding = dataBinding
    private var expression: String
    private var result: String
    private var currentNumber: String
    private var lastOperator: String
    private var errorDetected: Boolean
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
        errorDetected = false
        createButtons()
    }

    /**
     * Initializes and sets the click listeners for all the buttons on the calculator.
     * This includes operand buttons (0-9 and decimal), operator buttons (plus, minus, multiply, divide, etc.),
     * and action buttons (clear and delete, and memory buttons).
     *
     * @return void This method does not return any value.
     */
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
            binding.deleteButton
        )

        // Separated clear button to elevate it over the error detected check
        binding.clearButton.setOnClickListener {
            clearScreen()
        }

        operandButtons.forEach { it -> it.setOnClickListener { attachOperand(it.tag as String) } }
        operatorButtons.forEach { it -> it.setOnClickListener { attachOperator(it.tag as String) } }
        actionButtons.forEach { it -> it.setOnClickListener { useAction(it.tag as String) } }
    }

    /**
     * Appends an operand (digit or decimal point) to the current operand being entered.
     *
     * @param operand The operand to be appended to the current number (a digit or a decimal point).
     */
    private fun attachOperand(operand: String) {
        if (!limitInput() || errorDetected) {
            return
        }
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

    /**
     * Appends an operator to the current expression and handles specific operator actions.
     *
     * @param operator The operator to be appended or processed (e.g., "plus", "minus", "multiply", "divide", "equals", "plus_minus").
     */
    private fun attachOperator(operator: String) {
        if (errorDetected) {
            return
        }
        when (operator) {
            "equals" -> {
                if (currentNumber.isNotEmpty()) {
                    expression += currentNumber
                    currentNumber = ""
                }
                result = solve(formatExpression(expression).trim())
                if (errorDetected) {
                    binding.expressionTextView.text = ""
                    binding.resultTextView.text = context.getString(R.string.error_text)
                } else {
                    binding.expressionTextView.text = binding.resultTextView.text
                    binding.resultTextView.text = result
                }
                expression = ""
            }

            "plus_minus" -> {
                if (currentNumber.isNotEmpty() && currentNumber !="0") {
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
                updateResultWithExpression()
            }
        }
    }

    /**
     * Performs the specified calculator action.
     *
     * @param action The action to be performed.
     */
    private fun useAction(action: String) {
        if (errorDetected) {
            return
        }
        when (action) {
            "delete" -> {
                deleteCharacter()
            }
        }
    }

    /**
     * Updates the result display with the current expression and operand.
     */
    private fun updateResultWithOperand() {
        binding.resultTextView.text = formatResult(expression + currentNumber)
    }

    /**
     * Updates the result display with the current expression.
     */
    private fun updateResultWithExpression() {
        binding.resultTextView.text = formatResult(expression)
    }

    /**
     * Clears the calculator screen and resets the expression, result, and current number.
     */
    private fun clearScreen() {
        expression = ""
        result = "0"
        currentNumber = ""
        binding.resultTextView.text = "0"
        binding.expressionTextView.text = ""
        errorDetected = false
    }

    /**
     * Deletes the last character from the current number or expression.
     * Updates the result display accordingly and rebuilds the expression.
     * If the result display is empty, clears the calculator screen.
     */
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

    /**
     * Formats the given expression by replacing operator keywords with their respective button text.
     *
     * @param expression The expression to be formatted.
     * @return The formatted expression as a string.
     */
    private fun formatResult(expression: String): String {
        var formattedResult = expression
        operatorMap.forEach { (operator, buttonText) ->
            formattedResult = formattedResult.replace(operator, " $buttonText ")
        }
        return formattedResult.trim()
    }

    /**
     * Formats the given expression by adding spaces around operators.
     * This is done to ensure calculation methods can properly interpret the expression.
     *
     * @param expression The expression to be formatted.
     * @return The formatted expression as a string.
     */
    private fun formatExpression(expression: String): String {
        var formattedExpression = expression
        operatorMap.forEach { (operator) ->
            formattedExpression = if (operator == "squared" || operator == "square_root" || operator == "cubed") {
                formattedExpression.replace(operator, " $operator")
            } else {
                formattedExpression.replace(operator, " $operator ")
            }
        }
        return formattedExpression.trim()
    }

    /**
     * Rebuilds the expression from the current result view text.
     * If the result view text is empty, clears the calculator screen.
     */
    private fun rebuildExpression() {
        val resultViewText = binding.resultTextView.text.toString().trim()

        if (resultViewText.isEmpty()) {
            clearScreen()
            return
        }

        expression = ""

        // Filtering out parts of the result that contain empty strings
        val resultParts = resultViewText.split(" ").filter { it.isNotEmpty() }
        for (part in resultParts) {
            when {
                part.isNumber() -> expression += part
                part.isOperatorSymbol() -> expression += part.toOperatorText()
            }
        }
    }

    /**
     * Checks if the length of the result text is within the allowed maximum input length.
     *
     * @return True if the input length is within the limit, false otherwise.
     */
    private fun limitInput() : Boolean
    {
        return binding.resultTextView.text.toString().length <= context.getString(R.string.max_input_length).toInt()
    }

    /**
     * Solves the given mathematical expression formatted as a string using Postfix notation.
     *
     * Converts the infix expression to postfix notation, evaluates it, and formats the result.
     * Handles the display of decimals, formatting to 8 decimal places and removing trailing zeros.
     * If any errors occur, clears the calculator screen and displays an error message.
     *
     * @param formattedExpression The mathematical expression as a formatted string.
     * @return The result of the evaluated expression as a string.
     */
    private fun solve(formattedExpression: String): String {
        val expressionList = formattedExpression.split(" ")
        val postfix = convertToPostfix(expressionList)
        val solution = performCalculation(postfix)

        // Handles display of of decimals if present and formats to 8 decimal places
        return if (solution % 1 == 0.0) {
            solution.toString().dropLast(2)
        } else {
            // Format to 8 decimal places and use regex to drop trailing zeros
            String.format(context.getString(R.string.decimal_format), solution).replace(Regex("\\.?0*$"), "")
        }
    }

    /**
     * Converts the given infix expression to postfix notation.
     *
     * @param expressionList The list of tokens representing the infix expression.
     * @return The list of tokens representing the postfix expression.
     */
    private fun convertToPostfix(expressionList: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        // Map of order of precedence for operators. Higher numbers have higher precedence.
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
            when {
                element.isNumber() -> output.add(element)
                element.isOperator() -> {

                    // Check if the current operator has a higher precedence than the top operator on the stack
                    while (operators.isNotEmpty() && orderOfPrecedence[operators.peek()]!! >= orderOfPrecedence[element]!!) {
                        output.add(operators.pop())
                    }
                    operators.push(element)
                }
                element.isNotEmpty() -> {
                    errorDetected = true
                    expression = ""
                    currentNumber = ""
                    return emptyList()
                }
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }


    /**
     * Performs the calculation using the given postfix expression.
     *
     * @param postfixList The list of tokens representing the postfix expression.
     * @return The result of the calculation.
     * */
    private fun performCalculation(postfixList: List<String>): Double {
        val stack = Stack<Double>()
        var lastOperatorWasPercent = false // Flag used to track context of the percent operator

        for (element in postfixList) {
            when {
                element.isNumber() -> stack.push(element.toDouble())
                element.isOperator() -> {
                    // Handle unary operations
                    if (element in listOf("squared", "square_root", "cubed")) {
                        if (stack.isNotEmpty()) {
                            val num = stack.pop()
                            stack.push(calculate(element, num))
                        }
                    }

                    // Handle percent context: whether it is unary or being applied to the preceding part of the expression
                    else if (element == "percent") {
                        if (stack.isNotEmpty()) {
                            val num = stack.pop()

                            // if the last operator was a percent, apply it to the preceding part of the expression
                            if (stack.isNotEmpty() && !lastOperatorWasPercent) {
                                val previousNumber = stack.pop()
                                val result = previousNumber * (num / 100)
                                stack.push(previousNumber)
                                stack.push(result)
                            } else {
                                // if not, apply it to the current number which would be the only operand in the expression
                                stack.push(num / 100)
                            }
                        }
                        lastOperatorWasPercent = true
                    } else {

                        // Handle Binary operations
                        if (stack.size >= 2) {
                            val num2 = stack.pop()
                            val num1 = stack.pop()
                            stack.push(calculate(element, num1, num2))
                        }
                        lastOperatorWasPercent = false
                    }
                }

                else -> {
                    errorDetected = true
                    expression = ""
                    currentNumber = ""
                    return 0.0
                }
            }
        }

        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }


    /**
     * Performs a unary operation (squared, square_root, cubed, percent) on the given number.
     *
     * @param operator The unary operator to be applied.
     * @param num The operand to be evaluated.
     * @return The result of the unary operation as a double.
     */
    private fun calculate(operator: String, num: Double): Double {
        return when (operator) {
            "squared" -> num.pow(2)
            "square_root" -> sqrt(num)
            "cubed" -> num.pow(3)
            "percent" -> num / 100
            else -> {
                errorDetected = true
                expression = ""
                currentNumber = ""
                return 0.0
            }
        }
    }

    /**
     * Performs a binary operation (addition, subtraction, multiplication, division) on the given numbers.
     *
     * @param operator The binary operator to be applied.
     * @param num1 The first operand.
     * @param num2 The second operand.
     * @return The result of the binary operation as a double.
     */
    private fun calculate(operator: String, num1: Double, num2: Double): Double {
        return when (operator) {
            "plus" -> num1 + num2
            "minus" -> num1 - num2
            "multiply" -> num1 * num2
            "divide" -> num1 / num2
            else -> {
                errorDetected = true
                expression = ""
                currentNumber = ""
                return 0.0
            }
        }
    }

    /**
     * Checks if the string can be converted to a valid double number.
     *
     * @return True if the string is a valid number, false otherwise.
     */
    private fun String.isNumber() = this.toDoubleOrNull() != null

    /**
     * Checks if the string is one of the supported operators.
     *
     * @return True if the string is a supported operator, false otherwise.
     */
    private fun String.isOperator() = this in listOf(
        "plus", "minus", "multiply", "divide", "percent", "squared", "square_root", "cubed"
    )

    /**
     * Checks if the string is one of the supported operator symbols.
     *
     * @return True if the string is a supported operator symbol, false otherwise.
     */
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

    /**
     * Converts an operator symbol to its corresponding operator text.
     *
     * @return The corresponding operator text for the symbol, or an empty string if the symbol is not supported.
     */
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
