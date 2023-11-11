package com.example.fifteenpuzzle

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.window.layout.WindowMetricsCalculator
import kotlin.system.exitProcess


class MainActivity : Activity() {
    private lateinit var data: Data
    //buttons
    private lateinit var bestScoresBtn: Button
    private lateinit var exitBtn: Button
    private lateinit var newGameBtn: Button
    private lateinit var resumeBtn: Button

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onResume() {
        showHideResumeButton()
        super.onResume()
    }

    private fun init() {
        data = Data(this)
        if (data.screenWidth == 0) {
            val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
            data.screenWidth = metrics.bounds.width()
        }
        bestScoresBtn = findViewById(R.id.best_scores)
        exitBtn = findViewById(R.id.exit)
        newGameBtn = findViewById(R.id.new_game)
        resumeBtn = findViewById(R.id.resume_game)
        setHandlers()
    }

    private fun showHideResumeButton() {
        if (this.data.hasNotFinishedGame()) {
            this.resumeBtn.visibility = View.VISIBLE
        } else {
            this.resumeBtn.visibility = View.GONE
        }
    }

    private fun setHandlers() {
        resumeBtn.setOnClickListener {
            val intent = Intent(this@MainActivity.applicationContext, GameActivity::class.java)
            intent.putExtra(Data.NOT_FINISHED, true)
            this@MainActivity.startActivity(intent)
        }
        newGameBtn.setOnClickListener {
            val intent = Intent(this@MainActivity.applicationContext, GameActivity::class.java)
            intent.putExtra(Data.NOT_FINISHED, false)
            this@MainActivity.startActivity(intent)
        }
        bestScoresBtn.setOnClickListener {
            this@MainActivity.startActivity(
                Intent(
                    this@MainActivity.applicationContext,
                    BestScoreActivity::class.java
                )
            )
        }
        exitBtn.setOnClickListener {
            finish()
            exitProcess(0)
        }
    }
}