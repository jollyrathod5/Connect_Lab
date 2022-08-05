package com.pinmedia.connectlab

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pinmedia.connectlab.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), PodcastClickListener {

    private lateinit var binding: ActivityMainBinding
    private var podcastList = ArrayList<PodcastModel>()
    lateinit var mediaPlayer: MediaPlayer

    companion object {
        private const val SECOND_MILLIS = 1000
        const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        const val DAY_MILLIS = 24 * HOUR_MILLIS
        const val WEEKS_MILLIS = 7 * DAY_MILLIS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaPlayer = MediaPlayer()

        binding.recPodcast.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val podcastAdapter = PodcastAdapter(podcastList, this)
        binding.recPodcast.adapter = podcastAdapter

        binding.btnFile.setOnClickListener {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            openActivityForResult(intent)
        }

    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                var returnCursor: Cursor? = null
                val uri = result.data?.data

                try {
                    var displayName = "Audio"

                    returnCursor = contentResolver.query(uri!!, null, null, null, null)

                    val nameIndex: Int =
                        returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)!!
                    returnCursor.moveToFirst()
                    displayName = returnCursor.getString(nameIndex)

                    val duration = getDuration(uri)

                    podcastList.add(PodcastModel(displayName, Date(), duration, uri))
                    binding.recPodcast.adapter?.notifyDataSetChanged()

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    returnCursor?.close()
                }

            }
        }

    private fun openActivityForResult(mIntent: Intent) {
        resultLauncher.launch(mIntent)
    }

    override fun onPlayClick(itemData: PodcastModel) {

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        try {
            mediaPlayer.setDataSource(this, itemData.audioUri!!)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDuration(uri: Uri): String {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, uri)
        val durationStr =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return formatMilliSecond(durationStr!!.toLong())
    }

    private fun formatMilliSecond(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        return finalTimerString
    }


    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }
}