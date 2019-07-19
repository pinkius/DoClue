package uk.co.divisiblebyzero.doclue

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class SuspicionsActivity : AppCompatActivity() {
    val TAG = "SuspicionsActivity"

    val people =
        arrayOf("Irene Adler", "Sherlock Holmes", "Mycroft Holmes", "Mrs Hudson", "Inspector Lastrade", "John Watson")
    val weapons = arrayOf("Lead Piping", "Dagger", "Candlestick", "Rope", "Revolver", "Wrench")
    val locations = arrayOf(
        "Baskerville",
        "Mrs Hudson's Kitchen",
        "Swimming Pool",
        "Tower of London",
        "The Lab",
        "221b Baker Street",
        "Irene's flat",
        "Dartmoor"
    )

    val suspicionsMap: MutableMap<String, Suspicion> = mutableMapOf()

    fun addNewTextViewUnderThisOne(
        layout: ConstraintLayout,
        aboveTextView: TextView,
        text: String,
        size: Float,
        isHeader: Boolean = true
    ): TextView {

        val newTextView = TextView(this)
        newTextView.text = text
        newTextView.textSize = size
        newTextView.id = View.generateViewId()

        newTextView.setOnClickListener { v ->
            if (v is TextView) {
                v.paintFlags = (v.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG)
                if (v.text in suspicionsMap) suspicionsMap[v.text]!!.state = suspicionsMap[v.text]!!.state xor 1
                Toast.makeText(this, suspicionsMap[v.text].toString(), Toast.LENGTH_SHORT).show()
            }
        }

        layout.addView(newTextView)
        val set = ConstraintSet()
        set.clone(layout)
        set.connect(newTextView.id, ConstraintSet.TOP, aboveTextView.id, ConstraintSet.BOTTOM, 32)
        set.connect(newTextView.id, ConstraintSet.LEFT, layout.id, ConstraintSet.LEFT, 16)
        set.applyTo(layout)



        if (! isHeader) {

            var rightAnchor: View = layout
            for (i: Int in 1..5) {
                val newCheckBox = CheckBox(this)
                //newCheckBox.isChecked = false
                newCheckBox.text = ""
                newCheckBox.id = View.generateViewId()
                newCheckBox.setOnClickListener { v ->
                    if (v is CheckBox) {
                        if (text in suspicionsMap) {
                            suspicionsMap[text]!!.state = suspicionsMap[text]!!.state xor (1 shl i)
                        }
                        Toast.makeText(this, "Clicked: ${i} ${text}: ${suspicionsMap[text]}", Toast.LENGTH_SHORT).show()
                    }
                }
                layout.addView(newCheckBox)
                var checkBoxSet = ConstraintSet()
                checkBoxSet.clone(layout)
                checkBoxSet.connect(newCheckBox.id, ConstraintSet.TOP, aboveTextView.id, ConstraintSet.BOTTOM, 32)
                checkBoxSet.connect(newCheckBox.id, ConstraintSet.RIGHT, rightAnchor.id, ConstraintSet.RIGHT, 64)
                checkBoxSet.applyTo(layout)
                rightAnchor = newCheckBox
            }
        }




        return newTextView
    }

    fun addTextGroup(layout: ConstraintLayout, lastTextView: TextView, title: String, items: Array<String>): TextView {
        var currentLast = addNewTextViewUnderThisOne(layout, lastTextView, title, 20f)
        for (item in items) {
            currentLast = addNewTextViewUnderThisOne(layout, currentLast, item, 14f, false)
            suspicionsMap.put(item, Suspicion(item))
        }
        return currentLast
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suspicions)

        var lastTextView = findViewById<TextView>(R.id.textViewSuspicionsTitle)

        val layout = findViewById<ConstraintLayout>(R.id.constraintLayoutSuspicions)

        lastTextView = addTextGroup(layout, lastTextView, "People", people)
        lastTextView = addTextGroup(layout, lastTextView, "Weapons", weapons)
        lastTextView = addTextGroup(layout, lastTextView, "Locations", locations)
    }

    fun doSomething(view: View) {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
    }
}
