package `in`.alfageeks.rangeslider

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RangeSlider(
    modifier: Modifier = Modifier,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    trackActiveColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    trackInactiveColor: Color = Color.LightGray,
    min: Int = 0,
    max: Int = 100,
    rangeStart: Int = 0,
    rangeEnd: Int = 100,
    thumbSize: Dp = 20.dp,
    trackHeight: Dp = 20.dp,
    onError: ((code: String, msg: String) -> Unit)? = null,
    onRangeChanged: (Int, Int) -> Unit,
) {
    if (max <= min) {
        onError?.invoke(invalidMinMaxError.first, invalidMinMaxError.second)
        return
    }
    if (rangeEnd <= rangeStart) {
        onError?.invoke(invalidRangeStartEndError.first, invalidRangeStartEndError.second)
        return
    }
    if (thumbSize < trackHeight) {
        onError?.invoke(invalidTrackHeightError.first, invalidTrackHeightError.second)
        return
    }

    if (rangeStart !in min..max) {
        onError?.invoke(invalidRangeStartError.first, invalidRangeStartError.second)
        return
    }
    if (rangeEnd !in min..max) {
        onError?.invoke(invalidRangeEndError.first, invalidRangeEndError.second)
        return
    }

    val thumbRadius = with(LocalDensity.current) { thumbSize.toPx() / 2 }
    val thumbStartX = remember { mutableStateOf(0f) }
    val thumbEndX = remember { mutableStateOf(0f) }
    val size = remember { mutableStateOf(IntSize.Zero) }

    val trackWidth = (max - min).toFloat()
    val rangeStartPercentage = (rangeStart - min) / trackWidth
    val rangeEndPercentage = (rangeEnd - min) / trackWidth

    var activeThumb by remember { mutableStateOf(ThumbType.NONE) }

    val thumbMargin = thumbSize / 2
    val thumbMarginPx = with(LocalDensity.current) { thumbMargin.toPx() }

    val isStartThumbPressed = remember { mutableStateOf(false) }
    val isEndThumbPressed = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize)
            .onSizeChanged { newSize ->
                size.value = newSize
                val newTrackWidth = newSize.width - thumbRadius * 2
                thumbStartX.value = newTrackWidth * rangeStartPercentage
                thumbEndX.value = newTrackWidth * rangeEndPercentage
            }
            .pointerInteropFilter { event ->
                val eventX = event.x
                val xPos = eventX - thumbRadius

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        activeThumb = when {
                            thumbStartX.value in xPos - thumbRadius..xPos + thumbRadius -> {
                                isStartThumbPressed.value = true
                                isEndThumbPressed.value = false
                                ThumbType.START
                            }

                            thumbEndX.value in xPos - thumbRadius..xPos + thumbRadius -> {
                                isEndThumbPressed.value = true
                                isStartThumbPressed.value = false
                                ThumbType.END
                            }

                            else -> {
                                isStartThumbPressed.value = false
                                isEndThumbPressed.value = false
                                ThumbType.NONE
                            }
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (activeThumb != ThumbType.NONE) {
                            if (activeThumb == ThumbType.START) {
                                thumbStartX.value =
                                    xPos.coerceIn(0f, thumbEndX.value - thumbRadius - thumbMarginPx)
                            } else if (activeThumb == ThumbType.END) {
                                thumbEndX.value =
                                    xPos.coerceIn(
                                        thumbStartX.value + thumbRadius + thumbMarginPx,
                                        size.value.width - thumbRadius - thumbMarginPx
                                    )
                            }

                            val newRangeStart =
                                (thumbStartX.value / (size.value.width - thumbRadius * 2)) * trackWidth + min
                            val newRangeEnd =
                                (thumbEndX.value / (size.value.width - thumbRadius * 2)) * trackWidth + min
                            onRangeChanged(newRangeStart.toInt(), newRangeEnd.toInt())
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        activeThumb = ThumbType.NONE
                        isStartThumbPressed.value = false
                        isEndThumbPressed.value = false
                    }
                }
                true
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .height(trackHeight)
                .background(
                    color = trackActiveColor,
                    shape = RoundedCornerShape(trackHeight)
                )
                .drawBehind {
                    val thumbStart = thumbStartX.value + thumbRadius
                    val thumbEnd = thumbEndX.value + thumbRadius

                    val trackLeft = thumbStart.coerceAtMost(thumbEnd)
                    val trackRight = thumbStart.coerceAtLeast(thumbEnd)

                    val trackRect = Rect(
                        left = 0f,
                        top = 0f,
                        right = size.value.width.toFloat(),
                        bottom = trackHeight.toPx()
                    )

                    val leftClipPath = Path().apply {
                        addRoundRect(

                            RoundRect(
                                left = trackRect.left,
                                top = trackRect.top,
                                right = trackLeft,
                                bottom = trackRect.bottom,
                                bottomLeftCornerRadius = CornerRadius(trackHeight.toPx()),
                                topLeftCornerRadius = CornerRadius(trackHeight.toPx()),
                            ),
                        )
                    }

                    val rightClipPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                left = trackRight,
                                top = trackRect.top,
                                right = trackRect.right,
                                bottom = trackRect.bottom,
                                bottomRightCornerRadius = CornerRadius(trackHeight.toPx()),
                                topRightCornerRadius = CornerRadius(trackHeight.toPx()),
                            ),
                        )
                    }

                    clipPath(leftClipPath) {
                        drawRect(
                            color = trackInactiveColor,
                            topLeft = Offset(trackRect.left, trackRect.top),
                            size = Size(trackLeft, trackRect.height)
                        )
                    }

                    clipPath(rightClipPath) {
                        drawRect(
                            color = trackInactiveColor,
                            topLeft = Offset(trackRight, trackRect.top),
                            size = Size(trackRect.right - trackRight, trackRect.height)
                        )
                    }
                }
        )
        Thumb(
            xPos = thumbStartX.value,
            thumbSize = thumbSize,
            thumbColor = thumbColor,
            isEndThumbPressed = isStartThumbPressed.value
        )

        Thumb(
            xPos = thumbEndX.value,
            thumbSize = thumbSize,
            thumbColor = thumbColor,
            isEndThumbPressed = isEndThumbPressed.value
        )
    }
}


@Composable
private fun Thumb(xPos: Float, thumbSize: Dp, thumbColor: Color, isEndThumbPressed: Boolean) {
    val thumbScale by animateFloatAsState(if (isEndThumbPressed) 1.4f else 1f, label = "thumb_anim")
    Box(
        modifier = Modifier
            .offset { IntOffset(xPos.roundToInt(), 0) }
            .size(thumbSize)
            .graphicsLayer(
                scaleX = thumbScale,
                scaleY = thumbScale
            )
            .background(thumbColor, shape = CircleShape)
    )
}

private enum class ThumbType {
    START,
    END,
    NONE
}

@Preview
@Composable
private fun RangeSliderPreview() {
    var rangeStart by remember { mutableIntStateOf(10) }
    var rangeEnd by remember { mutableIntStateOf(50) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(vertical = 20.dp)) {
        RangeSlider(
            rangeStart = rangeStart,
            rangeEnd = rangeEnd,
        ) { newRangeStart, newRangeEnd ->
            rangeStart = newRangeStart
            rangeEnd = newRangeEnd
        }
        RangeSlider(
            rangeStart = rangeStart,
            rangeEnd = rangeEnd,
            trackHeight = 2.dp,
            thumbSize = 20.dp
        ) { newRangeStart, newRangeEnd ->
            rangeStart = newRangeStart
            rangeEnd = newRangeEnd
        }
    }
}