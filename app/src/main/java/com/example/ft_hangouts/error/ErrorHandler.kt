package com.example.ft_hangouts.error

import android.content.Context
import android.widget.Toast

interface DatabaseErrorHandler {
    fun handleError(context: Context)
}

class DatabaseCreateErrorHandler: DatabaseErrorHandler {
    override fun handleError(context: Context) {
        Toast.makeText(context, "연락처 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }
}

class DatabaseReadErrorHandler: DatabaseErrorHandler {
    override fun handleError(context: Context) {
        Toast.makeText(context, "연락처를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
    }
}