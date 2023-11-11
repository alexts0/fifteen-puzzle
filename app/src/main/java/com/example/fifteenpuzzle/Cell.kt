package com.example.fifteenpuzzle

import android.graphics.Rect

class Cell(cellNumber: Int, private val size: Int, private val multiplier: Int) {
    var srcRect: Rect = Rect()
    var dstRect: Rect = Rect()

    private val src = IntArray(3)
    private var dst = IntArray(3)
    private val srcCellSize: Int = SRC_IMAGE_SIZE / multiplier

    init {
        setSrc(cellNumber)
        setDst(cellNumber)
    }

    fun setSrc(cellNumber: Int) {
        internalSet(src, cellNumber, srcCellSize, srcRect)
    }

    fun setDst(cellNumber: Int) {
        internalSet(dst, cellNumber, size, dstRect)
    }

    fun animate(way: Int, delta: Int, target: Int): Boolean {
        dst[way] = dst[way] + delta
        if (delta > 0) {
            if (dst[way] > target) {
                dst[way] = target
            }
        } else {
            if (dst[way] < target) {
                dst[way] = target
            }
        }
        dstRect = Rect(dst[x], dst[y], dst[x] + size, dst[y] + size)
        return dst[way] == target
    }

    val srcCellNumber: Int
        get() = src[num]
    val dstCellNumber: Int
        get() = dst[num]
    val isOnPlace: Boolean
        get() = src[num] == dst[num]

    fun getTarget(way: Int): Int {
        return dst[way]
    }

    private fun internalSet(target: IntArray, cellNumber: Int, size: Int, rect: Rect) {
        val index = (if (cellNumber == 0) multiplier * multiplier else cellNumber) - 1
        target[x] = size * (index % multiplier)
        target[y] = size * (index / multiplier)
        target[num] = cellNumber

        rect.set(target[x], target[y], target[x] + size, target[y] + size)
    }

    companion object {
        @JvmStatic
        private val SRC_IMAGE_SIZE = 600

        @JvmStatic
        private val x = 0

        @JvmStatic
        private val y = 1

        @JvmStatic
        private val num = 2
    }
}
