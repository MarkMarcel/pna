package com.marcel.pna.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp

internal fun hasAnyCustomActionLabel(label: String): SemanticsMatcher {
    return SemanticsMatcher("Has custom action label: $label") { semanticsNode ->
        semanticsNode.config.getOrNull(SemanticsActions.CustomActions)
            ?.any { it.label == label } == true
    }
}

internal fun SemanticsNodeInteraction.swipeLeft() {
    performTouchInput {
        //swipeLeft(startX = 20.dp.toPx(), endX = 0f) // seem to results in multiple calls of the drag modifier
        down(center)
        advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
        moveBy(Offset(x = 20.dp.toPx().times(-1), y = 0f), 0)
        advanceEventTime(100)
        up()
    }
}

internal fun SemanticsNodeInteraction.swipeRight() {
    performTouchInput {
        //swipeLeft(startX = 20.dp.toPx(), endX = 0f) // seems to result in multiple calls of the drag modifier
        down(center)
        advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
        moveBy(Offset(x = 20.dp.toPx(), y = 0f), 0)
        advanceEventTime(100)
        up()
    }
}