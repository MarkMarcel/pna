package com.marcel.pna.core

import android.util.Log

class Logger {
    fun logError(error: Throwable) {
        Log.e("ERROR", error.message ?: "Unknown error")
    }
}