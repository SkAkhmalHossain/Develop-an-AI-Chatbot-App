package com.example.calculator

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CalculatorEngine {

    var display: String = "0"
        private set

    var expression: String = ""
        private set

    private var firstOperand: BigDecimal? = null
    private var pendingOperator: String? = null
    private var isNewInput: Boolean = true
    private var isCalculated: Boolean = false

    fun onDigit(digit: String) {
        if ((display == ERROR_MESSAGE) || isCalculated) {
            display = digit
            expression = ""
            firstOperand = null
            pendingOperator = null
            isCalculated = false
            isNewInput = false
            return
        }

        if (isNewInput) {
            display = digit
            isNewInput = false
        } else {
            if (display == "0") {
                display = if (digit == "0") "0" else digit
            } else if (display == "-0") {
                display = if (digit == "0") "-0" else "-$digit"
            } else {
                // Limit maximum digits to avoid display overflow
                val digitCount = display.filter { it.isDigit() }.length
                if (digitCount < MAX_DIGITS) {
                    display += digit
                }
            }
        }
    }

    fun onDecimal() {
        if ((display == ERROR_MESSAGE) || isCalculated) {
            display = "0."
            expression = ""
            firstOperand = null
            pendingOperator = null
            isCalculated = false
            isNewInput = false
            return
        }

        if (isNewInput) {
            display = "0."
            isNewInput = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun onOperator(op: String) {
        if (display == ERROR_MESSAGE) return

        val currentVal = parseDisplay(display) ?: return

        if (firstOperand == null) {
            firstOperand = currentVal
            pendingOperator = op
            expression = "${formatNumber(currentVal)} $op"
            isNewInput = true
            isCalculated = false
        } else if ((pendingOperator != null) && isNewInput && (!isCalculated)) {
            // User changes operator before typing next number
            pendingOperator = op
            expression = "${formatNumber(firstOperand!!)} $op"
        } else if (pendingOperator != null) {
            // Chain calculation
            val secondVal = currentVal
            val result = evaluate(firstOperand!!, pendingOperator!!, secondVal)
            if (result == null) {
                setErrorState()
            } else {
                firstOperand = result
                pendingOperator = op
                display = formatNumber(result)
                expression = "${formatNumber(result)} $op"
                isNewInput = true
                isCalculated = false
            }
        } else if (isCalculated) {
            // Continuation after equals
            firstOperand = currentVal
            pendingOperator = op
            expression = "${formatNumber(currentVal)} $op"
            isNewInput = true
            isCalculated = false
        }
    }

    fun onEquals() {
        if (display == ERROR_MESSAGE) return

        if ((firstOperand != null) && (pendingOperator != null)) {
            val secondVal = parseDisplay(display) ?: return
            val op = pendingOperator!!
            val first = firstOperand!!

            if ((op == "÷") && (secondVal.compareTo(BigDecimal.ZERO) == 0)) {
                expression = "${formatNumber(first)} $op ${formatNumber(secondVal)} ="
                setErrorState()
            } else {
                val result = evaluate(first, op, secondVal)
                if (result == null) {
                    expression = "${formatNumber(first)} $op ${formatNumber(secondVal)} ="
                    setErrorState()
                } else {
                    expression = "${formatNumber(first)} $op ${formatNumber(secondVal)} ="
                    display = formatNumber(result)
                    firstOperand = result
                    pendingOperator = null
                    isNewInput = true
                    isCalculated = true
                }
            }
        } else if (!isCalculated) {
            val currentVal = parseDisplay(display) ?: return
            expression = "${formatNumber(currentVal)} ="
            display = formatNumber(currentVal)
            isNewInput = true
            isCalculated = true
        }
    }

    fun onPlusMinus() {
        if (display == ERROR_MESSAGE) return

        if (display.startsWith("-")) {
            display = display.substring(1)
        } else if (display != "0") {
            display = "-$display"
        }

        if (isCalculated) {
            isCalculated = false
        }
    }

    fun onPercent() {
        if (display == ERROR_MESSAGE) return

        val currentVal = parseDisplay(display) ?: return
        val percentVal = currentVal.divide(BigDecimal("100"), MathContext.DECIMAL64)
        display = formatNumber(percentVal)
        if (isCalculated) {
            isCalculated = false
        }
    }

    fun onBackspace() {
        if ((display == ERROR_MESSAGE) || isCalculated) {
            display = "0"
            expression = ""
            isNewInput = true
            isCalculated = false
            return
        }

        if (!isNewInput) {
            display = display.dropLast(1)
            if (display.isEmpty() || (display == "-") || (display == "-0")) {
                display = "0"
                isNewInput = true
            }
        }
    }

    fun onAllClear() {
        display = "0"
        expression = ""
        firstOperand = null
        pendingOperator = null
        isNewInput = true
        isCalculated = false
    }

    private fun setErrorState() {
        display = ERROR_MESSAGE
        firstOperand = null
        pendingOperator = null
        isNewInput = true
        isCalculated = true
    }

    private fun parseDisplay(value: String): BigDecimal? {
        return try {
            BigDecimal(value)
        } catch (_: Exception) {
            null
        }
    }

    private fun evaluate(first: BigDecimal, op: String, second: BigDecimal): BigDecimal? {
        return try {
            when (op) {
                "+" -> first.add(second)
                "-" -> first.subtract(second)
                "×" -> first.multiply(second)
                "÷" -> {
                    if (second.compareTo(BigDecimal.ZERO) == 0) null
                    else first.divide(second, MathContext.DECIMAL64)
                }
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        const val ERROR_MESSAGE = "Cannot divide by 0"
        private const val MAX_DIGITS = 15

        fun formatNumber(number: BigDecimal): String {
            if (number.compareTo(BigDecimal.ZERO) == 0) {
                return "0"
            }

            val stripped = number.stripTrailingZeros()
            val plain = stripped.toPlainString()

            if (plain.length <= 15) {
                return plain
            }

            val absNum = number.abs()
            if ((absNum >= BigDecimal("1E12")) || (absNum < BigDecimal("1E-6"))) {
                val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
                return df.format(number)
            }

            val rounded = number.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
            val roundedPlain = rounded.toPlainString()
            if (roundedPlain.length <= 15) {
                return roundedPlain
            }

            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(number)
        }
    }
}
