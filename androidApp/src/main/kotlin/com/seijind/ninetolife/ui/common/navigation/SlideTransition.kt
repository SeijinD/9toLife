package com.seijind.ninetolife.ui.common.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.ui.NavDisplay

private const val SlideMillis = 320
private const val ParallaxFraction = 3
private val SlideSpec = tween<IntOffset>(durationMillis = SlideMillis)

/** Entry metadata for an iOS-style push: slides in from the end, slides back out on back. */
internal fun slideTransition(): Map<String, Any> =
    NavDisplay.transitionSpec { pushTransform() } +
        NavDisplay.popTransitionSpec { popTransform() } +
        NavDisplay.predictivePopTransitionSpec { popTransform() }

private fun pushTransform(): ContentTransform =
    slideInHorizontally(animationSpec = SlideSpec) { width -> width } togetherWith
        slideOutHorizontally(animationSpec = SlideSpec) { width -> -width / ParallaxFraction }

private fun popTransform(): ContentTransform =
    slideInHorizontally(animationSpec = SlideSpec) { width -> -width / ParallaxFraction } togetherWith
        slideOutHorizontally(animationSpec = SlideSpec) { width -> width }
