package io.github.horaciocome1.leiloa.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.horaciocome1.leiloa.data.participants.Participant
import io.github.horaciocome1.leiloa.databinding.ItemParticipantBinding
import io.github.horaciocome1.leiloa.util.DefaultRecyclerViewViewHolder

class ParticipantsAdapter : RecyclerView.Adapter<DefaultRecyclerViewViewHolder>() {

    var participants: List<Participant> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var binding: ItemParticipantBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DefaultRecyclerViewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemParticipantBinding
            .inflate(inflater, parent, false)
        return DefaultRecyclerViewViewHolder(view = binding.root)
    }

    override fun getItemCount(): Int = participants.size

    override fun onBindViewHolder(
        holder: DefaultRecyclerViewViewHolder,
        position: Int
    ) {
        binding.participant = participants[position]
    }


}