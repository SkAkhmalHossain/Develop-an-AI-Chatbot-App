package com.example.calculator

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    @Test
    fun testInitialState() {
        assertEquals("0", engine.display)
        assertEquals("", engine.expression)
    }

    @Test
    fun testDigitInput() {
        engine.onDigit("1")
        engine.onDigit("2")
        engine.onDigit("3")
        assertEquals("123", engine.display)
    }

    @Test
    fun testDecimalInput() {
        engine.onDigit("5")
        engine.onDecimal()
        engine.onDigit("2")
        assertEquals("5.2", engine.display)

        // Multiple decimal points ignored
        engine.onDecimal()
        assertEquals("5.2", engine.display)
    }

    @Test
    fun testAddition() {
        engine.onDigit("1")
        engine.onDigit("2")
        engine.onOperator("+")
        assertEquals("12 +", engine.expression)
        engine.onDigit("5")
        engine.onEquals()
        assertEquals("17", engine.display)
        assertEquals("12 + 5 =", engine.expression)
    }

    @Test
    fun testSubtraction() {
        engine.onDigit("2")
        engine.onDigit("0")
        engine.onOperator("-")
        engine.onDigit("8")
        engine.onEquals()
        assertEquals("12", engine.display)
        assertEquals("20 - 8 =", engine.expression)
    }

    @Test
    fun testMultiplication() {
        engine.onDigit("6")
        engine.onOperator("×")
        engine.onDigit("7")
        engine.onEquals()
        assertEquals("42", engine.display)
        assertEquals("6 × 7 =", engine.expression)
    }

    @Test
    fun testDivision() {
        engine.onDigit("2")
        engine.onDigit("0")
        engine.onOperator("÷")
        engine.onDigit("4")
        engine.onEquals()
        assertEquals("5", engine.display)
        assertEquals("20 ÷ 4 =", engine.expression)
    }

    @Test
    fun testDivisionByZero() {
        engine.onDigit("9")
        engine.onOperator("÷")
        engine.onDigit("0")
        engine.onEquals()
        assertEquals("Cannot divide by 0", engine.display)
        assertEquals("9 ÷ 0 =", engine.expression)
    }

    @Test
    fun testChainedOperations() {
        engine.onDigit("5")
        engine.onOperator("+")
        engine.onDigit("3")
        engine.onOperator("×")
        assertEquals("8", engine.display)
        assertEquals("8 ×", engine.expression)
        engine.onDigit("2")
        engine.onEquals()
        assertEquals("16", engine.display)
        assertEquals("8 × 2 =", engine.expression)
    }

    @Test
    fun testOperatorChange() {
        engine.onDigit("5")
        engine.onOperator("+")
        engine.onOperator("×")
        assertEquals("5 ×", engine.expression)
        engine.onDigit("4")
        engine.onEquals()
        assertEquals("20", engine.display)
    }

    @Test
    fun testAllClear() {
        engine.onDigit("5")
        engine.onOperator("+")
        engine.onDigit("3")
        engine.onAllClear()
        assertEquals("0", engine.display)
        assertEquals("", engine.expression)
    }

    @Test
    fun testBackspace() {
        engine.onDigit("1")
        engine.onDigit("2")
        engine.onDigit("3")
        engine.onBackspace()
        assertEquals("12", engine.display)
        engine.onBackspace()
        assertEquals("1", engine.display)
        engine.onBackspace()
        assertEquals("0", engine.display)
    }

    @Test
    fun testPlusMinus() {
        engine.onDigit("5")
        engine.onPlusMinus()
        assertEquals("-5", engine.display)
        engine.onPlusMinus()
        assertEquals("5", engine.display)
    }

    @Test
    fun testPercent() {
        engine.onDigit("5")
        engine.onDigit("0")
        engine.onPercent()
        assertEquals("0.5", engine.display)
    }
}
