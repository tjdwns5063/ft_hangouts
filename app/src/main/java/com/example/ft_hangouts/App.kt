package com.example.ft_hangouts

import android.app.Application
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class App : Application() {

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        lateinit var INSTANCE: App
    }
}

object BackgroundHelper {
    private val executor: ExecutorService = Executors.newFixedThreadPool(8)
    fun <T> execute(callable: Callable<T>): T {
        val future = executor.submit(callable)
        return future.get()
    }
}