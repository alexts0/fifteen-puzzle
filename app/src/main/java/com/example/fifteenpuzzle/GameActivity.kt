package com.example.fifteenpuzzle

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlin.math.roundToInt

class GameActivity : FragmentActivity(), ListDialog.ListDialogInterface,
    WinDialog.WinDialogListener {
    private lateinit var multipliers: Array<String>
    private lateinit var skins: Array<String>
    private lateinit var background: FrameLayout
    private lateinit var backgrounds: IntArray
    private lateinit var data: Data
    private lateinit var drawView: DrawView

    //buttons
    private lateinit var multiplierBtn: ImageButton
    private lateinit var resetBtn: ImageButton
    private lateinit var skinBtn: ImageButton
    private lateinit var soundBtn: ImageButton

    //images
    private lateinit var skinsImages: IntArray
    private lateinit var multipliersImages: IntArray
    private lateinit var soundImages: IntArray

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_game)
        volumeControlStream = 3
        init()
    }

    private fun init() {
        //buttons
        multiplierBtn = findViewById(R.id.multiplier)
        resetBtn = findViewById(R.id.reset)
        skinBtn = findViewById(R.id.skin)
        soundBtn = findViewById(R.id.sound)

        //images
        skinsImages = intArrayOf(R.drawable.cell_wood, R.drawable.cell_metal)
        multipliersImages = intArrayOf(
            R.drawable.nine,
            R.drawable.fifteen,
            R.drawable.twentyfour,
            R.drawable.thirtyfive
        )
        soundImages = intArrayOf(R.drawable.sound_off, R.drawable.sound_on)

        multipliers = resources.getStringArray(R.array.Multipliers)
        skins = resources.getStringArray(R.array.Skins)
        background = findViewById(R.id.background)
        backgrounds = intArrayOf(R.drawable.back_wood, R.drawable.back_metal)
        drawView = findViewById(R.id.drawView)
        data = Data(this)

        setGameBoardWidth()
        setButtons()
        drawView.init(
            data,
            findViewById(R.id.moves_counter),
            intent.extras!!.getBoolean(Data.NOT_FINISHED)
        )
    }

    private fun setButtons() {
        val m: Int = data.savedMultiplier
        val s: Int = data.savedSkin
        isSoundOn = data.soundSettings
        val a = if (isSoundOn) 1 else 0
        multiplierBtn.setImageResource(multipliersImages[m])
        skinBtn.setImageResource(skinsImages[s])
        soundBtn.setImageResource(soundImages[a])
        background.setBackgroundResource(backgrounds[s])
        setHandlers()
    }

    private fun toggleSound() {
        isSoundOn = !isSoundOn
        val a: Int = if (isSoundOn) 1 else 0
        soundBtn.setImageResource(soundImages[a])
    }

    private fun setHandlers() {
        multiplierBtn.setOnClickListener { showMultiplierDialog() }
        skinBtn.setOnClickListener { showSkinDialog() }
        resetBtn.setOnClickListener { drawView.reset() }
        soundBtn.setOnClickListener { toggleSound() }
    }

    private fun setGameBoardWidth() {
        val width = data.screenWidth
        val offset: Float = width / 600.00f
        val borderWidth = (15  * offset).roundToInt()
        data.gameWidth = width - borderWidth * 2
        background.layoutParams.width = width - borderWidth
        background.layoutParams.height = width - borderWidth
        drawView.layoutParams.width = data.gameWidth
        drawView.layoutParams.height = data.gameWidth
    }

    private fun showMultiplierDialog() {
        val title: CharSequence = resources.getString(R.string.multiplier_title)
        val dialog = ListDialog()
        dialog.setParameters(title, multipliers, R.id.multiplier)
        dialog.show(supportFragmentManager.beginTransaction(), "Multipliers")
    }

    private fun showSkinDialog() {
        val title: CharSequence = resources.getString(R.string.skin_title)
        val dialog = ListDialog()
        dialog.setParameters(title, skins, R.id.skin)
        dialog.show(supportFragmentManager.beginTransaction(), "Skins")
    }

    fun showWinDialog(moves: Int) {
        val winDialog = WinDialog()
        winDialog.setMoves(moves)
        winDialog.isCancelable = false
        winDialog.show(supportFragmentManager.beginTransaction(), "WinDialog")
    }

    override fun onItemClick(i: Int, i2: Int) {
        when (i2) {
            R.id.multiplier -> {
                multiplierBtn.setImageResource(multipliersImages[i])
                drawView.changeMultiplier(i)
                return
            }

            R.id.skin -> {
                skinBtn.setImageResource(skinsImages[i])
                background.setBackgroundResource(backgrounds[i])
                drawView.changeSkin(i)
                return
            }

            else -> {
                return
            }
        }
    }

    override fun onPause() {
        drawView.saveSettings()
        Log.d("info", "GameActivity paused")
        super.onPause()
    }

    override fun onDialogPositiveClick(dialogFragment: DialogFragment?) {
        drawView.reset()
    }

    override fun onDialogNegativeClick(dialogFragment: DialogFragment?) {
        finish()
    }

    companion object {
        var isSoundOn = true
    }
}