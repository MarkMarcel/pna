package com.marcel.pna

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PNAMLogo(
    modifier: Modifier = Modifier,
    requiredHeight: Dp = 48.dp,
    tint: Color = LocalContentColor.current,
) {
    // Matches the image resource dimensions
    val aspectRatio = 950f / 427f
    Box(
        modifier = modifier
            .height(requiredHeight)
            .aspectRatio(aspectRatio)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.logo),
            contentDescription = stringResource(R.string.app_logo_content_description),
            modifier = Modifier
                .matchParentSize(),
            contentScale = ContentScale.Fit,
        )
    }
}