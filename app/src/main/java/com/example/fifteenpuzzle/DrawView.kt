package com.example.fifteenpuzzle

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView


class DrawView : View {
    private val context: Context
    private val sound = SoundPool.Builder().setMaxStreams(10).build()
    private val audio: IntArray
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var anim: Runnable
    private lateinit var bitmap: Bitmap
    private lateinit var board: Fifteen
    private lateinit var data: Data
    private lateinit var movesCounter: TextView
    private lateinit var paint: Paint
    private val resArray = arrayOf(
        intArrayOf(
            R.drawable.eight_wood,
            R.drawable.fifteen_wood,
            R.drawable.twentyfour_wood,
            R.drawable.thirtyfive_wood,
            R.drawable.back_wood
        ),
        intArrayOf(
            R.drawable.eight_metal,
            R.drawable.fifteen_metal,
            R.drawable.twentyfour_metal,
            R.drawable.thirtyfive_metal,
            R.drawable.back_metal
        )
    )
    private var skin = 0
    private var width = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN ) {
            return false
        }
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (board.clickHandler(intArrayOf(x, y))) {
            invalidate()
            if (GameActivity.isSoundOn) {
                sound.play(audio[skin], 1.0f, 1.0f, 0, 0, 1.0f)
            }
        }
        return true
    }

    private fun updateMovesCounter() {
        movesCounter.text = board.moves.toString()
    }

    override fun onDraw(canvas: Canvas) {
        if (board.hasAnimation) {
            handler.postDelayed(anim, 17)
        } else {
            updateMovesCounter()
        }
        board.draw(canvas, bitmap, paint)
        if (board.isWin) {
            showWin(canvas)
        }
    }

    private fun showWin(canvas: Canvas) {
        canvas.drawARGB(127, 0, 0, 0)
        data.addHighScore(board.multiplierIndex, board.moves)
        (context as GameActivity).showWinDialog(board.moves)
    }

    private fun reinit() {
        setBitmap()
        invalidate()
    }

    fun reset() {
        board.init()
        invalidate()
    }

    fun changeMultiplier(n: Int) {
        if (board.multiplierIndex != n) {
            board.setMultiplier(n)
            reinit()
        }
    }

    fun changeSkin(which: Int) {
        if (skin != which) {
            skin = which
            reinit()
        }
    }

    private fun setBitmap() {
        val options = BitmapFactory.Options()
        options.inScaled = false
        bitmap = BitmapFactory.decodeResource(
            resources,
            resArray[skin][board.multiplierIndex],
            options
        )
        Log.w("Bitmap:", "bitmap.width = ${bitmap.width}, bitmap.height = ${bitmap.height}")
    }

    fun saveSettings() {
        data.saveGameConfig(skin, board.multiplierIndex)
        if (board.isWin || board.moves == 0) {
            data.saveGame()
        } else {
            data.saveGame(board.fieldToString(), board.moves)
        }
    }

    fun init(d: Data, textView: TextView, resumeGame: Boolean) {
        paint = Paint(1)
        data = d
        movesCounter = textView
        anim = Runnable {
            board.animate()
            this@DrawView.invalidate()
        }
        width = data.gameWidth
        skin = data.savedSkin
        val multiplier: Int = data.savedMultiplier
        Log.w("DrawView init:", "width = $width, skin = $skin, multiplier = $multiplier")
        board = if (resumeGame) {
            Fifteen(width, multiplier, data.savedField, data.savedMoves)
        } else {
            Fifteen(width, multiplier)
        }
        setBitmap()
    }

    constructor(context2: Context) : super(context2) {
        context = context2
        audio = intArrayOf(
            sound.load(context, R.raw.move, 1),
            sound.load(context, R.raw.move_metal, 1)
        )
    }

    constructor(context2: Context, attrs: AttributeSet?) : super(context2, attrs) {
        context = context2
        audio = intArrayOf(
            sound.load(context, R.raw.move, 1),
            sound.load(context, R.raw.move_metal, 1)
        )
    }

    constructor(context2: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context2,
        attrs,
        defStyle
    ) {
        context = context2
        audio = intArrayOf(
            sound.load(context, R.raw.move, 1),
            sound.load(context, R.raw.move_metal, 1)
        )
    }
}