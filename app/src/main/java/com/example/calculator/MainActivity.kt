package com.example.calculator

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.TextViewCompat
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    lateinit var calcField: TextView
    var lastNumeric: Boolean = false
    var stateError: Boolean = false
    var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calcField = findViewById(R.id.calcField)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDigit(view: View){
        if (stateError){
            calcField.text = (view as Button).text
            stateError = false
        }else{
            calcField.append((view as Button).text)
        }
        lastNumeric = true
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(30)
//        TextViewCompat.setAutoSizeTextTypeWithDefaults(calcField, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(calcField, TextView.AUTO_SIZE_TEXT_TYPE_NONE)

    }

    fun onDecimalPoint(view: View){
        if(lastNumeric && !stateError && !lastDot){
            calcField.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onOperator(view: View){
        if (lastNumeric && !stateError){
            calcField.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(20)
    }

    private fun backSpace(){
        if(calcField.text.isNotEmpty()){
            calcField.text = calcField.text.toString().substring(0, calcField.text.toString().length - 1)
        }
    }

    private fun init(){
        val btnDel = findViewById<TextView>(R.id.btnDel)
        btnDel.setOnLongClickListener {
            this.calcField.text = ""
            lastNumeric = false
            stateError = false
            lastDot = false
            true
        }
        btnDel.setOnClickListener{
            backSpace()
        }
    }

    fun onSqrt(view: View){
        if (calcField.text.isEmpty()){
            calcField.append("√")
        }
        if (lastNumeric && !stateError){
            lastNumeric = false
            lastDot = false
        }
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(20)
    }

    fun onEqual(view: View){
        if (calcField.text.contains("√")){
            val underRootExpr = calcField.text.toString().replace("√", "")
            calcField.text = sqrt(underRootExpr.toDouble()).toString()
            return
        }
        if (lastNumeric && !stateError){
            val txt = calcField.text.toString()
            val expression = ExpressionBuilder(txt).build()
            try {
                val result = expression.evaluate()
                calcField.text = result.toString()
                lastDot = true
            }catch (ex: ArithmeticException){
                calcField.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }
}