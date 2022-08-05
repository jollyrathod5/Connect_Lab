package com.pinmedia.connectlab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pinmedia.connectlab.databinding.ItemPodcastBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PodcastAdapter(
    private val podcastList: List<PodcastModel>,
    private val podcastClickListener: PodcastClickListener
) : RecyclerView.Adapter<PodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPodcastBinding.inflate(inflater, parent, false)
        return PodcastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val model = podcastList[position]
        holder.binding.textTitle.text = model.title
        holder.binding.textTimeAgo.text = getTimeAgo(model.timeAgo!!)
        holder.binding.textTime.text = model.duration

        holder.binding.btnPlay.setOnClickListener {
            podcastClickListener.onPlayClick(model)
        }
    }

    private fun getTimeAgo(startDate: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        val now = Date()
        val sDateTimeNow = simpleDateFormat.format(now)
        val dateTimeNow = simpleDateFormat.parse(sDateTimeNow)

        val endDate = dateTimeNow!!

        val different = endDate.time - startDate.time
        val day = TimeUnit.MILLISECONDS.toDays(different)
        var message = ""

        if (different < MainActivity.MINUTE_MILLIS) {
            message = "Just now"
        } else if (different < 2 * MainActivity.MINUTE_MILLIS) {
            message = "1 minute ago"
        } else if (different < 50 * MainActivity.MINUTE_MILLIS) {
            val time = different / MainActivity.MINUTE_MILLIS
            message = "$time minutes ago"
        } else if (different < 90 * MainActivity.MINUTE_MILLIS) {
            message = "1 hour ago"
        } else if (different < 24 * MainActivity.HOUR_MILLIS) {
            message = timeFormat.format(startDate)
        } else if (different < 48 * MainActivity.HOUR_MILLIS) {
            message = "Yesterday"
        } else if (different < 7 * MainActivity.DAY_MILLIS) {
            val time = different / MainActivity.DAY_MILLIS
            message = "$time days ago"
        } else if (different < 2 * MainActivity.WEEKS_MILLIS) {
            val time = different / MainActivity.WEEKS_MILLIS
            message = "$time week ago"
        } else if (different < 3.5 * MainActivity.WEEKS_MILLIS) {
            val time = different / MainActivity.WEEKS_MILLIS
            message = "$time weeks ago"
        } else if (day > 30) {
            val time = day / 360
            message = "$time month ago"
        } else {
            message = dateFormat.format(startDate)
        }

        return message
    }

    override fun getItemCount(): Int {
        return podcastList.size
    }
}

class PodcastViewHolder(val binding: ItemPodcastBinding) :
    RecyclerView.ViewHolder(binding.root)