package com.marcel.pna.components.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcel.pna.components.app.theme.PNAComponentsTheme

@Composable
fun HeadlineCardSampleScreen(
    modifier: Modifier = Modifier,
){
    Box(
        Modifier.padding(16.dp)
    ) {
    }
}

@Preview
@Composable
fun HeadlineCardSampleScreenPreview(){
    PNAComponentsTheme {
        HeadlineCardSampleScreen(
            modifier = Modifier.safeContentPadding()
        )
    }
}