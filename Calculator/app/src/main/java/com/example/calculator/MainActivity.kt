package com.example.calculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val calculator = CalculatorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButtons()
        updateDisplay()
    }

    private fun setupButtons() {
        // Digit buttons
        binding.btn0.setOnClickListener { onDigitPressed("0") }
        binding.btn1.setOnClickListener { onDigitPressed("1") }
        binding.btn2.setOnClickListener { onDigitPressed("2") }
        binding.btn3.setOnClickListener { onDigitPressed("3") }
        binding.btn4.setOnClickListener { onDigitPressed("4") }
        binding.btn5.setOnClickListener { onDigitPressed("5") }
        binding.btn6.setOnClickListener { onDigitPressed("6") }
        binding.btn7.setOnClickListener { onDigitPressed("7") }
        binding.btn8.setOnClickListener { onDigitPressed("8") }
        binding.btn9.setOnClickListener { onDigitPressed("9") }

        // Decimal button
        binding.btnDot.setOnClickListener {
            calculator.onDecimal()
            updateDisplay()
        }

        // Operation buttons
        binding.btnAdd.setOnClickListener { onOperatorPressed("+") }
        binding.btnSubtract.setOnClickListener { onOperatorPressed("-") }
        binding.btnMultiply.setOnClickListener { onOperatorPressed("×") }
        binding.btnDivide.setOnClickListener { onOperatorPressed("÷") }

        // Action buttons
        binding.btnEquals.setOnClickListener {
            calculator.onEquals()
            updateDisplay()
        }

        binding.btnAC.setOnClickListener {
            calculator.onAllClear()
            updateDisplay()
        }

        binding.btnBackspace.setOnClickListener {
            calculator.onBackspace()
            updateDisplay()
        }

        binding.btnPlusMinus.setOnClickListener {
            calculator.onPlusMinus()
            updateDisplay()
        }

        binding.btnPercent.setOnClickListener {
            calculator.onPercent()
            updateDisplay()
        }
    }

    private fun onDigitPressed(digit: String) {
        calculator.onDigit(digit)
        updateDisplay()
    }

    private fun onOperatorPressed(operator: String) {
        calculator.onOperator(operator)
        updateDisplay()
    }

    private fun updateDisplay() {
        binding.tvDisplay.text = calculator.display
        binding.tvExpression.text = calculator.expression
    }
}
