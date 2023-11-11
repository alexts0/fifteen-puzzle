package com.example.fifteenpuzzle

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ListDialog : DialogFragment() {
    private var arr: Array<String>  = Array(1) { String() }
    private var multiplierId = 0
    private var mListener: ListDialogInterface? = null
    private var title: CharSequence? = null

    interface ListDialogInterface {
        fun onItemClick(i: Int, i2: Int)
    }

    fun setParameters(title2: CharSequence?, arr2: Array<String>, id: Int) {
        arr = arr2
        title = title2
        multiplierId = id
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as ListDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ListDialogInterface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title).setItems(
            arr
        ) { _, which -> mListener!!.onItemClick(which, multiplierId) }
        return builder.create()
    }
}