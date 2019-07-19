package uk.co.divisiblebyzero.doclue

import android.content.Context
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.content.Intent



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

    fun getPersistedState(item: String): Int {
        val prefs = this.getPreferences(Context.MODE_PRIVATE) ?: return 0
        return prefs.getInt(item, 0)
    }

    fun persistState(item: String) {
        val prefs = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (prefs.edit()) {
            putInt(item, suspicionsMap[item]!!.state)
            commit()
        }
    }

    fun addNewTextViewUnderThisOne(
        layout: ConstraintLayout,
        aboveTextView: TextView,
        text: String,
        size: Float,
        isHeader: Boolean = true
    ): TextView {


        var state: Int = 0
        if (text in suspicionsMap) {
            state = suspicionsMap.get(text)!!.state
        }


        val newTextView = TextView(this)
        newTextView.text = text
        newTextView.textSize = size
        newTextView.id = View.generateViewId()
        if (state and 1 == 1) newTextView.paintFlags = newTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        newTextView.setOnClickListener { v ->
            if (v is TextView) {
                v.paintFlags = (v.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG)
                if (text in suspicionsMap) suspicionsMap[text]!!.state = suspicionsMap[text]!!.state xor 1
                persistState(text)
                //Toast.makeText(this, suspicionsMap[text].toString(), Toast.LENGTH_SHORT).show()
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
                if (text in suspicionsMap)
                    newCheckBox.isChecked = ((suspicionsMap[text]!!.state shr i) and 1 == 1)
                newCheckBox.text = ""
                newCheckBox.id = View.generateViewId()
                newCheckBox.setOnClickListener { v ->
                    if (v is CheckBox) {
                        if (text in suspicionsMap) {
                            suspicionsMap[text]!!.state = suspicionsMap[text]!!.state xor (1 shl i)
                            persistState(text)
                        }
                        //Toast.makeText(this, "Clicked: ${i} ${text}: ${suspicionsMap[text]}", Toast.LENGTH_SHORT).show()
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
            suspicionsMap.put(item, Suspicion(item, getPersistedState(item)))
            currentLast = addNewTextViewUnderThisOne(layout, currentLast, item, 14f, false)
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

    fun reset(view: View) {
        for (item in suspicionsMap.keys) {
            suspicionsMap[item]!!.state = 0
            persistState(item)
        }
        finish()

        startActivity(Intent(this, SuspicionsActivity::class.java))
    }
}
