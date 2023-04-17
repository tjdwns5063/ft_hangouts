package com.example.ft_hangouts.error

import android.content.Context
import android.widget.Toast

interface DatabaseHandler {
    fun handle(context: Context)

    fun isSuccess(): Boolean
}

interface DatabaseErrorHandler: DatabaseHandler {
//    fun handleError(context: Context)
}

class DatabaseCreateErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseReadErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseUpdateErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, "데이터 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun isSuccess(): Boolean {
        return false
    }
}

class DatabaseDeleteErrorHandler: DatabaseErrorHandler {
    override fun handle(context: Context) {
        Toast.makeText(context, "데이터 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
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