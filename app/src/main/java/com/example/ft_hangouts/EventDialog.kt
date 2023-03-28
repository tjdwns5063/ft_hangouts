package com.example.ft_hangouts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class EventDialog(private val message: String, private val onClick: OnClickListener): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setPositiveButton("확인", onClick)
                .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun showEventDialog(fragmentManager: FragmentManager, message: String ,onClick: OnClickListener) {
            val newDialog = EventDialog(message, onClick)
            newDialog.show(fragmentManager, "event_dialog")
        }
    }
}