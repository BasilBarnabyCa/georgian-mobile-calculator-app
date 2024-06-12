package ca.georgiancollege.calculator

import ca.georgiancollege.calculator.databinding.ActivityMainBinding

class Calculator(dataBinding: ActivityMainBinding){

    private var binding: ActivityMainBinding = dataBinding
    private var result: String

    init {
        result = "0"
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

        operandButtons.forEach { it.setOnClickListener { operandPressHandler(it.tag as String) } }

        binding.clearButton.setOnClickListener {
            clearScreen()
        }
    }

    private fun operandPressHandler(tag: String) {
        when(tag)
        {
            "." -> {
                if(!binding.formulaTextView.text.contains("."))
                {
                    result += if(result.isEmpty()) "0." else "."
                    binding.formulaTextView.text = result
                }
            }
            "delete" -> {
                result = result.dropLast(1)
                binding.formulaTextView.text = if(result.isEmpty() || result == "-") "0" else result
                result = if(result.isEmpty()) "0" else result
            }
            "plus_minus" -> {
                if(result.startsWith("-"))
                {
                    result = result.substring(1)
                }
                else
                {
                    if(result.isNotEmpty() && result != "0")
                    {
                        result = "-".plus(result)
                    }
                }
                binding.formulaTextView.text = result
            }
            "clear" -> {
                clearScreen()
            }
            else -> {
                if(binding.formulaTextView.text == "0")
                {
                    result = tag
                    binding.formulaTextView.text = result
                }
                else
                {
                    result += tag
                    binding.formulaTextView.text = result
                }
            }
        }
    }

    private fun clearScreen()
    {
        result = "0"
        binding.formulaTextView.text = "0"
    }
}