package com.pinmedia.connectlab

import android.net.Uri
import java.util.*

data class PodcastModel(
    val title: String?,
    val timeAgo: Date?,
    val duration: String?,
    val audioUri: Uri?,
    val isPlay: Boolean? = false
)
