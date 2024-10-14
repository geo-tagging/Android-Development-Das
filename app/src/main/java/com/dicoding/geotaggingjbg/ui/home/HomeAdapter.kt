package com.dicoding.geotaggingjbg.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.databinding.ItemDataBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeAdapter : PagingDataAdapter<Entity, HomeAdapter.MyViewHolder>(Comparator()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position) as Entity
        holder.bind(user)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(user)
        }
    }

    class MyViewHolder(private val binding: ItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Entity) {
            binding.ivImage.setImageURI(data.image?.toUri())
            binding.tvId.text = data.id.toString()
            val jenTanId = data.jenTan
            val jenTanArray = binding.root.context.resources.getStringArray(R.array.array_jentan)
            var jenTanValue = ""
            for (item in jenTanArray) {
                val parts = item.split(",")
                if (parts[0].toInt() == jenTanId) {
                    jenTanValue = parts[1]
                    break
                }
            }
            binding.tvJentan.text = jenTanValue
            binding.tvTanggal.text = data.tanggal?.let { parseDate(it) }
        }

        private fun parseDate(date: String): String {
            var outputDate: String
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
            try {
                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                outputFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
                outputDate = outputFormat.format((inputFormat.parse(date) ?: Date()).time)
            } catch (e: ParseException) {
                e.printStackTrace()
                outputDate = date
            }
            return outputDate
        }
    }

    class Comparator : DiffUtil.ItemCallback<Entity>() {
        override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Entity)
    }
}