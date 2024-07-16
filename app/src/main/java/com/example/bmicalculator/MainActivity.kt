package com.example.bmicalculator

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var editTextHeight: EditText
    lateinit var editTextWeight: EditText
    lateinit var spinnerUnits: Spinner
    lateinit var buttonCalculate: Button
    lateinit var textViewResult: TextView
    lateinit var textViewBMIDetails: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        spinnerUnits = findViewById(R.id.spinnerUnits)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewResult = findViewById(R.id.textViewResult)
        textViewBMIDetails = findViewById(R.id.textViewBMIDetails)

        val unitOptions = resources.getStringArray(R.array.unit_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unitOptions)
        spinnerUnits.adapter = adapter

        spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update hints based on selected unit system (metric/imperial)
                // 0 for Metric, 1 for Imperial
                if (position == 0) {
                    editTextHeight.hint = "Enter height in meter"
                    editTextWeight.hint = "Enter weight in kg"

                } else {
                    editTextHeight.hint = "Enter height in inche"
                    editTextWeight.hint = "Enter weight in lb"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        buttonCalculate.setOnClickListener {
            // Clear previous output
            textViewResult.text = ""
            textViewBMIDetails.text = ""
            calculateBMI()
        }
    }

    fun calculateBMI() {
        val heightStr = editTextHeight.text.toString().trim()
        val weightStr = editTextWeight.text.toString().trim()

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter height and weight", Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toFloat()
        val weight = weightStr.toFloat()

        if (height == 0f) {
            Toast.makeText(this, "Height can't be zero", Toast.LENGTH_SHORT).show()
            return
        }

        if (weight == 0f) {
            Toast.makeText(this, "Weight can't be zero", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedUnit = spinnerUnits.selectedItemPosition // 0 for Metric, 1 for Imperial
        val bmi = if (selectedUnit == 0) {
            calculateBMIMetric(height, weight)
        } else {
            calculateBMIImperial(height, weight)
        }


        displayBMIResult(bmi)
    }

    fun calculateBMIMetric(height: Float, weight: Float): Float {
        // BMI calculation for metric units: weight (kg) / height (m)^2
        return String.format("%.1f", weight / (height * height)).toFloat()
    }

    fun calculateBMIImperial(height: Float, weight: Float): Float {
        // BMI calculation for imperial units: weight (lb) / height (in)^2 * 703
        return String.format("%.1f", (weight / (height * height)) * 703).toFloat()
    }

    fun displayBMIResult(bmi: Float) {
        val bmiCategory = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal weight"
            bmi in 25.0..29.9 -> "Overweight"
            bmi in 30.0..34.9 -> "Obesity (Class I)"
            bmi in 35.0..39.9 -> "Obesity (Class II)"
            bmi > 39.9 -> "Obesity (Class III)"
            else -> "Invalid BMI"
        }

        val bmiCategoryColor = when (bmiCategory) {
            "Normal weight" -> Color.parseColor("#29e014")
            "Underweight" -> Color.parseColor("#3866d9")
            else -> Color.parseColor("#de1c12")
        }

        val bmiText = "BMI: $bmi\n"
        val categoryLabelText = "Category: "
        val spannableString = SpannableString(bmiText + categoryLabelText + bmiCategory)

        spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, bmiText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), bmiText.length + categoryLabelText.length, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(RelativeSizeSpan(1.25f), 0, bmiText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(ForegroundColorSpan(bmiCategoryColor), bmiText.length + categoryLabelText.length, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textViewResult.text = spannableString
        textViewResult.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        val bmiDetails = SpannableStringBuilder("""
            Underweight: < 18.5
            Normal weight: 18.5 - 24.9
            Overweight: 25.0 - 29.9
            Obesity (Class I): 30.0 - 34.9
            Obesity (Class II): 35.0 - 39.9
            Obesity (Class III): > 39.9
        """.trimIndent())
        bmiDetails.apply {setSpan(StyleSpan(android.graphics.Typeface.BOLD),0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }

        textViewBMIDetails.text = bmiDetails
        textViewBMIDetails.gravity = Gravity.CENTER

    }
}
