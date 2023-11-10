package com.example.fifteenpuzzle

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ListDialog : DialogFragment() {
    private var arr: Array<String>  = Array(1) { String() }

    /* access modifiers changed from: private */ /* renamed from: id */
    var f8id = 0
    var mListener: ListDialogInterface? = null
    private var title: CharSequence? = null

    interface ListDialogInterface {
        fun onItemClick(i: Int, i2: Int)
    }

    fun setParameters(title2: CharSequence?, arr2: Array<String>, id: Int) {
        arr = arr2
        title = title2
        f8id = id
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as ListDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement ListDialogInterface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(title).setItems(
            arr
        ) { dialog, which -> mListener!!.onItemClick(which, f8id) }
        return builder.create()
    }
}