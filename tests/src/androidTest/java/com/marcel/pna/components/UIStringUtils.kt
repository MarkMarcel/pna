package com.marcel.pna.components

import androidx.test.platform.app.InstrumentationRegistry

internal fun getStringResource(id: Int): String {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    return context.getString(id)
}