package com.example.ft_hangouts.error

import android.content.Context
import android.widget.Toast
import com.example.ft_hangouts.R

interface DatabaseHandler {
    fun handle(context: Context)

    fun isSuccess(): Boolean
}

interface DatabaseErrorHandler: DatabaseHandler {
//    fun handleError(context: Context)
}

class DatabaseCreateErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_create_error), Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseReadErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_read_error), Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseUpdateErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_update_error), Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseDeleteErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, context.getString(R.string.database_delete_error), Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseSuccessHandler: DatabaseHandler {
    override fun handle(context: Context) {}

    override fun isSuccess(): Boolean {
        return true
    }
}