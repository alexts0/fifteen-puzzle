package com.example.fifteenpuzzle

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class BestScoreActivity : FragmentActivity(), ListDialog.ListDialogInterface {
    private lateinit var data: Data
    private lateinit var multipliers: Array<String>

    private lateinit var multiplierBtn: ImageButton
    private lateinit var multipliersImages: IntArray
    private lateinit var tableLayout: TableLayout

    private var multiplier = 0

    /* access modifiers changed from: protected */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_best_score)
        init()
    }

    private fun init() {
        data = Data(this)
        multipliers = resources.getStringArray(R.array.Multipliers)
        multiplierBtn = findViewById(R.id.multiplier)
        multipliersImages = intArrayOf(
            R.drawable.nine,
            R.drawable.fifteen,
            R.drawable.twentyfour,
            R.drawable.thirtyfive
        )
        tableLayout = findViewById(R.id.high_score_table)

        multiplier = data.savedBestScoreMultiplier
        multiplierBtn.setOnClickListener { showMultiplierDialog() }
        multiplierBtn.setImageResource(multipliersImages[multiplier])
        showHighScores()
    }

    private fun showMultiplierDialog() {
        val title: CharSequence = resources.getString(R.string.multiplier_title)
        val dialog = ListDialog()
        dialog.setParameters(title, multipliers, R.id.multiplier)
        dialog.show(supportFragmentManager.beginTransaction(), "Multipliers")
    }

    private fun showHighScores() {
        tableLayout.removeAllViews()
        val highScores = data.getHighScores(multiplier)
        val rows = highScores.size
        val tableTitles: Array<String> = resources.getStringArray(R.array.table_titles)
        tableLayout.addView(createRow(tableTitles[0], tableTitles[1]), 0)
        for (r in 0 until rows) {
            tableLayout.addView(
                createRow(
                    (r + 1).toString(), highScores[r].toString()
                ), r + 1
            )
        }
    }

    private fun createRow(firstText: String, secondText: String): TableRow {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(-1, -2)
        tableRow.addView(createTxtField(firstText))
        tableRow.addView(createTxtField(secondText))
        return tableRow
    }

    private fun createTxtField(txt: String): TextView {
        val params: ViewGroup.LayoutParams = TableRow.LayoutParams(0, -2, 1.0f)
        val text = TextView(this)
        text.text = txt
        text.setTextColor(Color.WHITE)
        text.gravity = 1
        text.setTextSize(0, resources.getDimension(R.dimen.table_text_view))
        text.setBackgroundResource(R.drawable.cellborder)
        text.layoutParams = params
        return text
    }

    override fun onItemClick(i: Int, i2: Int) {
        if (multiplier != i) {
            multiplier = i
            multiplierBtn.setImageResource(multipliersImages[i])
            showHighScores()
        }
    }

    /* access modifiers changed from: protected */
    override fun onPause() {
        data.saveBestScoreMultiplier(multiplier)
        super.onPause()
    }
}