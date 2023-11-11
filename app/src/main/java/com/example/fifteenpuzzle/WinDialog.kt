package com.example.fifteenpuzzle

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class WinDialog : DialogFragment() {
    private lateinit var winDialogListener: WinDialogListener
    private var moves = 0

    interface WinDialogListener {
        fun onDialogNegativeClick(dialogFragment: DialogFragment?)
        fun onDialogPositiveClick(dialogFragment: DialogFragment?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            winDialogListener = context as WinDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement WinDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val layout: View =
            requireActivity().layoutInflater.inflate(R.layout.windialog, null as ViewGroup?)
        val winTextView = layout.findViewById<TextView>(R.id.win_text)
        if (winTextView != null) {
            winTextView.text = getString(R.string.congratulation, moves)
        }
        builder.setView(layout)
            .setPositiveButton(
                R.string.reset
            ) { _, _ -> winDialogListener.onDialogPositiveClick(this@WinDialog) }
            .setNegativeButton(
                R.string.exit
            ) { _, _ ->
                winDialogListener.onDialogNegativeClick(
                    this@WinDialog
                )
            }
        return builder.create()
    }

    fun setMoves(moves2: Int) {
        moves = moves2
    }
}