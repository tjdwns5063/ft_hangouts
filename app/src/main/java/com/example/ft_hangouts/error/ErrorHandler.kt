package com.example.ft_hangouts.error

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ft_hangouts.R

abstract class ErrorHandler {
    protected var isTerminated: Boolean = false

    abstract fun handle(context: Context)

    fun checkTerminated(): Boolean {
        return isTerminated
    }

    fun updateTerminated(isTerminated: Boolean) {
        this.isTerminated = isTerminated
    }
}

class DatabaseCreateErrorHandler: ErrorHandler() {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_create_error), Toast.LENGTH_SHORT).show()
    }

}

class DatabaseReadErrorHandler: ErrorHandler() {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_read_error), Toast.LENGTH_SHORT).show()
    }

}

class DatabaseUpdateErrorHandler: ErrorHandler() {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_update_error), Toast.LENGTH_SHORT).show()
    }

}

class DatabaseDeleteErrorHandler: ErrorHandler() {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_delete_error), Toast.LENGTH_SHORT).show()
    }

}

class DatabaseSuccessHandler: ErrorHandler() {
    override fun handle(context: Context) {}
}

class SmsSystemErrorHandler: ErrorHandler() {
    override fun handle(context: Context) {
        Log.i("error", "call handleSmsSystemError")
        Toast.makeText(context, context.getString(R.string.cannot_use_sms_feature), Toast.LENGTH_SHORT).show()
    }

}

class CallSystemErrorHandler: ErrorHandler() {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.cannot_use_call_feature), Toast.LENGTH_SHORT).show()
    }
}