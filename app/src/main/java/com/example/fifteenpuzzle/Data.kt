package com.example.fifteenpuzzle

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class Data(context: Context) {
    private val multiplierScores =
        arrayOf("eightScores", "fifteenScores", "twentyFourScores", "thirtyFiveScores")

    private val highScore: Array<ArrayList<Int>> =
        Array(multiplierScores.size) { ArrayList() }
    private val settings: SharedPreferences  = context.getSharedPreferences(MAIN_SETTINGS, 0)

    init {
        loadHighScore()
    }

    fun hasNotFinishedGame(): Boolean {
        return settings.getBoolean(NOT_FINISHED, false)
    }

    fun saveGameConfig(skin: Int, multiplier: Int) {
        val editor = settings.edit()
        editor.putInt(SKIN, skin)
        editor.putInt(MULTIPLIER, multiplier)
        editor.putBoolean(SOUND, GameActivity.isSoundOn)
        editor.apply()
    }

    fun saveBestScoreMultiplier(multiplier: Int) {
        settings.edit().putInt(BEST_SCORE_MULTIPLIER, multiplier).apply()
    }

    fun saveGame(field: String?, moves: Int) {
        val editor = settings.edit()
        editor.putBoolean(NOT_FINISHED, true)
        editor.putString(GAME_FIELD, field)
        editor.putInt(MOVES_COUNTER, moves)
        editor.apply()
        saveHighScore()
    }

    fun saveGame() {
        settings.edit().putBoolean(NOT_FINISHED, false).apply()
        saveHighScore()
    }

    var screenWidth: Int
        get() = settings.getInt(SCREEN_SIZE, 0)
        set(width) {
            val editor = settings.edit()
            editor.putInt(SCREEN_SIZE, width)
            editor.apply()
        }

    var gameWidth: Int
        get() = settings.getInt(GAME_BOARD_WIDTH, 0)
        set(width) {
            Log.d("gameWidth is:", "$width")
            val editor = settings.edit()
            editor.putInt(GAME_BOARD_WIDTH, width)
            editor.apply()
        }

    fun getHighScores(mult: Int): IntArray {
        val len = highScore[mult].size
        val ret = IntArray(len)
        for (i in 0 until len) {
            ret[i] = highScore[mult][i]
        }
        return ret
    }

    val savedField: IntArray
        get() = convertFromString(settings.getString(GAME_FIELD, null as String?))
    val soundSettings: Boolean
        get() = settings.getBoolean(SOUND, true)
    val savedSkin: Int
        get() = settings.getInt(SKIN, 0)
    val savedMoves: Int
        get() = settings.getInt(MOVES_COUNTER, 0)
    val savedMultiplier: Int
        get() = settings.getInt(MULTIPLIER, 0)

    fun addHighScore(multiplier: Int, moves: Int) {
        var alreadyHas = false
        var i = 0
        while (true) {
            if (i >= highScore[multiplier].size) {
                break
            } else if (highScore[multiplier][i] == moves) {
                alreadyHas = true
                break
            } else {
                i++
            }
        }
        if (!alreadyHas) {
            highScore[multiplier].add(Integer.valueOf(moves))
        }
    }

    private fun saveHighScore() {
        prepareHighScore()
        val editor = settings.edit()
        for (i in multiplierScores.indices) {
            val tmp = scoreToString(i)
            if (tmp.isNotEmpty()) {
                editor.putString(multiplierScores[i], tmp)
            }
        }
        editor.apply()
    }

    private fun prepareHighScore() {
        for (i in highScore.indices) {
            highScore[i].sort()
            val k = highScore[i].size
            if (k > 10) {
                highScore[i].subList(10, k).clear()
            }
        }
    }

    private fun scoreToString(multiplier: Int): String {
        val sb = StringBuilder()
        for (i in highScore[multiplier].indices) {
            sb.append(highScore[multiplier][i])
            sb.append(",")
        }
        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
        }
        return sb.toString()
    }

    private fun getSavedScore(multiplier: Int): IntArray {
        val str = multiplierScores[multiplier]
        Log.d(
            "LOADED_DATA",
            multiplierScores[multiplier] + " best: " + settings.getString(str, null as String?)
        )
        return convertFromString(settings.getString(str, null as String?))
    }

    private fun convertFromString(inp: String?): IntArray {
        if (inp.isNullOrEmpty() || "" == inp || "\u0000" == inp) {
            return IntArray(0)
        }
        val items = inp.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val result = IntArray(items.size)
        var i = 0
        while (i < items.size) {
            try {
                result[i] = items[i].toInt()
                i++
            } catch (e: NumberFormatException) {
                Log.d("ERROR", e.message!!)
                return result
            }
        }
        return result
    }

    private fun loadHighScore() {
        val tmp = arrayOfNulls<IntArray>(4)
        for (i in 0..3) {
            tmp[i] = getSavedScore(i)
            highScore[i] = ArrayList(tmp[i]!!.size)
            for (valueOf in tmp[i]!!) {
                highScore[i].add(Integer.valueOf(valueOf))
            }
        }
    }

    val savedBestScoreMultiplier: Int
        get() = settings.getInt(BEST_SCORE_MULTIPLIER, 0)

    companion object {
        const val BEST_SCORE_MULTIPLIER = "BestScoreMultiplier"
        const val GAME_FIELD = "GameField"
        const val MAIN_SETTINGS = "FifteenMainSettings"
        const val MOVES_COUNTER = "MovesCounter"
        const val MULTIPLIER = "Multiplier"
        const val NOT_FINISHED = "NotFinishedGame"
        const val GAME_BOARD_WIDTH = "GameBoardWidth"
        const val SCREEN_SIZE = "ScreenSize"
        const val SKIN = "Skin"
        const val SOUND = "Sound"
    }
}