package com.example.fifteenpuzzle

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class WinDialog : DialogFragment() {
    var mListener: WinDialogListener? = null
    private var moves = 0

    interface WinDialogListener {
        fun onDialogNegativeClick(dialogFragment: DialogFragment?)
        fun onDialogPositiveClick(dialogFragment: DialogFragment?)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as WinDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement WinDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val layout: View =
            requireActivity().getLayoutInflater().inflate(R.layout.windialog, null as ViewGroup?)
        val tv = layout.findViewById<View>(R.id.win_text) as TextView
        if (tv != null) {
            tv.text = moves.toString() + " " + getResources().getString(R.string.moves_made)
        }
        builder.setView(layout).setPositiveButton(R.string.reset,
            DialogInterface.OnClickListener { dialog, id -> mListener!!.onDialogPositiveClick(this@WinDialog) })
            .setNegativeButton(R.string.exit,
                DialogInterface.OnClickListener { dialog, id ->
                    mListener!!.onDialogNegativeClick(
                        this@WinDialog
                    )
                })
        return builder.create()
    }

    fun setMoves(moves2: Int) {
        moves = moves2
    }
}