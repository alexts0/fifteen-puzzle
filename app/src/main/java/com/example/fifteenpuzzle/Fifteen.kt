package com.example.fifteenpuzzle

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import kotlin.math.abs

class Fifteen {
    private val directions = arrayOf(intArrayOf(0, 1), intArrayOf(2, 3))
    private val multipliers = intArrayOf(3, 4, 5, 6)
    private val width: Int
    private var cellSize = 0
    private var clickable = true
    private var delta = 0
    private var emptyCell = IntArray(2)
    var hasAnimation = false
    var moves = 0
        private set
    private var multiplier = 4
    private var selectedCell = IntArray(2)
    private val stackEmpty = ArrayList<IntArray>()
    private val stackSelected = ArrayList<IntArray>()
    private var target = 0
    private var wayXY = 0
    private lateinit var defaultField: Array<Array<Cell>>
    private lateinit var currentField: Array<Array<Cell>>

    constructor(width: Int, m: Int) {
        this.width = width
        setMultiplier(m)
    }

    constructor(width: Int, m: Int, arr: IntArray, moves: Int) {
        this.width = width
        multiplier = multipliers[m]
        init(arr, moves)
    }

    val multiplierIndex: Int
        get() {
            var result = -1
            for (i in multipliers.indices) {
                if (multiplier == multipliers[i]) {
                    result = i
                    break
                }
            }
            return result
        }

    fun setMultiplier(n: Int) {
        multiplier = multipliers[n]
        init()
    }

//    private fun findNearestDivisor(dividend: Int, divisor: Int): Int {
//        if (divisor > dividend) {
//            throw Error("Illegal arguments: divisor can't be more than dividend")
//        }
//        var divisor1 = divisor
//        while (dividend % divisor1 != 0) {
//            ++divisor1
//        }
//        var divisor2 = divisor
//        while (dividend % divisor2 != 0) {
//            --divisor2
//        }
//        return if (divisor1 - divisor > divisor - divisor2) divisor2 else divisor1
//    }

    private fun getDelta(): Int {
//        return findNearestDivisor(cellSize, cellSize / 5)
        return cellSize / 5
    }

    private fun getField(): Array<Array<Cell>> {
        val arr = Array(multiplier) { Array(multiplier) { Cell(1, 100, 4) }  }
        var cellNumber = 1
        for (i in 0 until multiplier) {
            for (j in 0 until multiplier) {
                if (cellNumber == multiplier * multiplier) {
                    cellNumber = 0
                }
                arr[i][j] = Cell(cellNumber, cellSize, multiplier)
                cellNumber++
            }
        }
        return arr
    }

    private fun isEven(a: Int): Boolean {
        return a and 1 == 0
    }

    private fun isSolvable(a: IntArray): Boolean {
        var sum = 0
        val len = multiplier * multiplier
        for (i in 0 until len) {
            if (a[i] != 0) {
                for (j in 0 until i) {
                    if (a[j] > a[i]) {
                        sum++
                    }
                }
            } else if (isEven(multiplier)) {
                sum += i / multiplier + 1
            }
        }
        return isEven(sum)
    }

    private fun shuffle(a: IntArray) {
        for (i in a.size - 1 downTo 1) {
            val j = (Math.random() * (i + 1).toDouble()).toInt()
            val temp = a[i]
            a[i] = a[j]
            a[j] = temp
        }
    }

    private fun mixField(): Array<Array<Cell>> {
        val len = multiplier * multiplier
        val arr = IntArray(len)
        for (i in 0 until len) {
            arr[i] = i
        }
        do {
            shuffle(arr)
        } while (!isSolvable(arr))
        return createField(arr)
    }

    private fun createField(arr: IntArray): Array<Array<Cell>> {
        val len = multiplier * multiplier
        defaultField = getField()
        val out = getField()
        for (k in 0 until len) {
            for (i in 0 until multiplier) {
                for (j in 0 until multiplier) {
                    if (arr[k] == defaultField[i][j].srcCellNumber) {
                        out[k / multiplier][k % multiplier].setSrc(defaultField[i][j].srcCellNumber)
                    }
                }
            }
        }
        return out
    }

    private fun getEmptyCell(): IntArray {
        for (y in 0 until multiplier) {
            for (x in 0 until multiplier) {
                if (currentField[y][x].srcCellNumber == 0) {
                    return intArrayOf(y, x)
                }
            }
        }
        return intArrayOf(0, 0)
    }

    private fun getSelectedCell(pos: IntArray): IntArray {
        return intArrayOf(pos[1] / cellSize, pos[0] / cellSize)
    }

    val isWin: Boolean
        get() {
            for (i in 0 until multiplier) {
                for (j in 0 until multiplier) {
                    if (!this.currentField[i][j].isOnPlace) {
                        return false
                    }
                }
            }
            clickable = false
            return true
        }

    fun draw(canvas: Canvas, img: Bitmap, p: Paint?) {
        for (i in 0 until multiplier) {
            for (j in 0 until multiplier) {
                val cell = currentField[i][j]
                canvas.drawBitmap(img, cell.srcRect, cell.dstRect, p)
            }
        }
    }

    private fun fillStacks() {
        stackSelected.add(intArrayOf(selectedCell[0], selectedCell[1]))
        stackEmpty.add(intArrayOf(emptyCell[0], emptyCell[1]))
    }

    private fun clearStacks() {
        stackSelected.clear()
        stackEmpty.clear()
    }

    private fun makeMove() {
        hasAnimation = false
        for (i in stackSelected.indices) {
            val e = stackEmpty[i]
            val s = stackSelected[i]
            currentField[e[0]][e[1]].setDst(defaultField[e[0]][e[1]].dstCellNumber)
            currentField[s[0]][s[1]].setDst(defaultField[s[0]][s[1]].dstCellNumber)
            val tE: Int = currentField[e[0]][e[1]].srcCellNumber
            currentField[e[0]][e[1]].setSrc(currentField[s[0]][s[1]].srcCellNumber)
            currentField[s[0]][s[1]].setSrc(tE)
        }
        clearStacks()
        moves++
        clickable = true
    }

    private fun prepareAnimation(direction: Int) {
        delta = abs(delta)
        when (direction) {
            0 -> wayXY = 1
            1 -> {
                delta *= -1
                wayXY = 1
            }

            2 -> wayXY = 0
            3 -> {
                delta *= -1
                wayXY = 0
            }
        }
        target = currentField[emptyCell[0]][emptyCell[1]].getTarget(wayXY)
        hasAnimation = true
    }

    fun animate() {
        for (i in stackSelected.indices) {
            val tmp = stackSelected[i]
            if (currentField[tmp[0]][tmp[1]].animate(wayXY, delta, target)) {
                makeMove()
            }
        }
    }

    private fun prepareMove(direction: Int, flag: Boolean) {
        fillStacks()
        if (flag) {
            prepareAnimation(direction)
        }
    }

    private val way: Int
        get() {
            if (selectedCell[1] == emptyCell[1] && selectedCell[0] != emptyCell[0]) {
                return 0
            }
            return if (selectedCell[0] != emptyCell[0] || selectedCell[1] == emptyCell[1]) {
                -1
            } else 1
        }

    fun clickHandler(pos: IntArray): Boolean {
        if (!clickable) {
            return false
        }
        emptyCell = getEmptyCell()
        selectedCell = getSelectedCell(pos)
        val way = way
        if (way < 0) {
            return false
        }
        startMove(way)
        return true
    }

    private fun startMove(way: Int) {
        var z: Boolean
        var z2: Boolean
        clickable = false
        if (selectedCell[way] < emptyCell[way]) {
            while (selectedCell[way] < emptyCell[way]) {
                val i = directions[way][0]
                z2 = selectedCell[way] + 1 == emptyCell[way]
                prepareMove(i, z2)
                val iArr = selectedCell
                iArr[way] = iArr[way] + 1
            }
            return
        }
        while (selectedCell[way] > emptyCell[way]) {
            val i2 = directions[way][1]
            z = selectedCell[way] - 1 == emptyCell[way]
            prepareMove(i2, z)
            val iArr2 = selectedCell
            iArr2[way] = iArr2[way] - 1
        }
    }

    fun init() {
        cellSize = width / multiplier
        delta = getDelta()
        Log.d("ANIM DELTA:", "delta = $delta, cellSize = $cellSize /5 = ${cellSize / 5}")
        currentField = mixField()
        moves = 0
        hasAnimation = false
        clickable = true
    }

    private fun init(arr: IntArray, moves: Int) {
        cellSize = width / multiplier
        delta = getDelta()
        Log.d("ANIM DELTA:", "delta = $delta, cellSize = $cellSize /5 = ${cellSize / 5}")
        currentField = createField(arr)
        this.moves = moves
        hasAnimation = false
        clickable = true
    }

    fun fieldToString(): String {
        val sb = StringBuilder()
        for (i in 0 until multiplier) {
            for (j in 0 until multiplier) {
                sb.append(currentField[i][j].srcCellNumber)
                sb.append(",")
            }
        }
        sb.setLength(sb.length - 1)
        return sb.toString()
    }
}