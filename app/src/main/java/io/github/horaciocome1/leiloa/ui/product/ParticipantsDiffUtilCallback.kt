package io.github.horaciocome1.leiloa.ui.product

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import io.github.horaciocome1.leiloa.data.participants.Participant

/**
 * returns the difference between two list of participants
 */
class ParticipantsDiffUtilCallback(
    private val oldList: List<Participant>,
    private val newList: List<Participant>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.name == newItem.name &&
                oldItem.price == newItem.price
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? =
        super.getChangePayload(oldItemPosition, newItemPosition)

}