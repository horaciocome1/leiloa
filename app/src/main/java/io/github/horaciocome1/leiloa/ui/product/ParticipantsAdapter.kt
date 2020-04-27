package io.github.horaciocome1.leiloa.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.horaciocome1.leiloa.data.participants.Participant
import io.github.horaciocome1.leiloa.databinding.ItemParticipantBinding

class ParticipantsAdapter : RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder>() {

    var participants: List<Participant> = listOf()
        set(value) {
            val diffUtilCallback = ParticipantsDiffUtilCallback(field, value)
            val result = DiffUtil.calculateDiff(diffUtilCallback)
            field = value
            result.dispatchUpdatesTo(this)
        }

    private lateinit var binding: ItemParticipantBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemParticipantBinding
            .inflate(inflater, parent, false)
        return MyViewHolder(view = binding.root)
    }

    override fun getItemCount(): Int = participants.size

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        binding.participant = participants[position]
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}