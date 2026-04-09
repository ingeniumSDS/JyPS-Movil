package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws four golden corner brackets positioned at each corner of the scanner viewbox.
 * Rendered as independent [Box] elements to avoid Canvas complexity.
 *
 * @param cornerColor Color of the corner brackets (defaults to metallic gold).
 * @param stroke Width of each bracket line.
 * @param size Length of each bracket arm.
 * @param modifier Modifier controlling the overall size and position of the bracket group.
 */
@Composable
fun GoldenCornerOverlay(
    cornerColor: Color = Color(0xFFD4AF37),
    stroke: Dp = 3.5.dp,
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Top-Left
        Box(modifier = Modifier.align(Alignment.TopStart).size(size)) {
            Box(Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.TopStart))
            Box(Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.TopStart))
        }
        // Top-Right
        Box(modifier = Modifier.align(Alignment.TopEnd).size(size)) {
            Box(Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.TopEnd))
            Box(Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.TopEnd))
        }
        // Bottom-Left
        Box(modifier = Modifier.align(Alignment.BottomStart).size(size)) {
            Box(Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.BottomStart))
            Box(Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.BottomStart))
        }
        // Bottom-Right
        Box(modifier = Modifier.align(Alignment.BottomEnd).size(size)) {
            Box(Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.BottomEnd))
            Box(Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.BottomEnd))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoldenCornerOverlayPreview() {
    GoldenCornerOverlay()
}
