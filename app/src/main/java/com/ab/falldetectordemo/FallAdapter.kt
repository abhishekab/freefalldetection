package com.ab.falldetectordemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ab.falldetector.model.Fall
import com.ab.falldetectordemo.databinding.ItemFallBinding

class FallAdapter : RecyclerView.Adapter<FallAdapter.FallViewHolder>() {

    var fallList: List<Fall> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FallViewHolder {
        val binding = ItemFallBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return FallViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fallList.size
    }

    override fun onBindViewHolder(holder: FallViewHolder, position: Int) {
        holder.bind()
    }

    inner class FallViewHolder(val binding: ItemFallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.fall = fallList[adapterPosition]
        }
    }
}