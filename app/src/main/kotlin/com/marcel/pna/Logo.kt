package com.marcel.pna

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.marcel.pna.theme.PNAMTheme

@Composable
fun PNAMLogo(
    modifier: Modifier = Modifier,
    requiredHeight: Dp = 48.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    // Matches the image resource dimensions
    val aspectRatio = 950f / 427f
    Image(
        painter = painterResource(id = R.mipmap.logo),
        colorFilter = ColorFilter.tint(tint),
        contentDescription = stringResource(R.string.app_logo_content_description),
        contentScale = ContentScale.Fit,
        modifier = modifier
            .height(requiredHeight)
            .aspectRatio(aspectRatio),
    )
}

@Preview
@Composable
fun PNAMLogoPreview() {
    PNAMTheme {
        PNAMLogo()
    }
}