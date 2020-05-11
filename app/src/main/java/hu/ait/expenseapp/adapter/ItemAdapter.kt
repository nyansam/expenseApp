package hu.ait.expenseapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.ait.expenseapp.DetailsActivity
import hu.ait.expenseapp.R
import hu.ait.expenseapp.ScrollingActivity
import hu.ait.expenseapp.data.Item
import hu.ait.expenseapp.data.ItemDatabase
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*
import java.util.List

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    var groceryItems = mutableListOf<Item>()
    val context: Context

    constructor(context: Context, listItems: List<Item>) {
        this.context = context
        groceryItems.addAll(listItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_row, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groceryItems.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = groceryItems[position]

        holder.tvName.text = currentItem.name
        holder.tvPrice.text = ("$" + currentItem.price)


        interactionListener(holder)
    }

    private fun interactionListener(holder: ViewHolder) {
        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

    }

    private fun deleteItem(position: Int) {
        Thread {
            ItemDatabase.getInstance(context).itemDao().deleteItem(
                groceryItems.get(position)
            )

            (context as DetailsActivity).runOnUiThread {
                groceryItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()

    }

    public fun addItem(item: Item) {
        groceryItems.add(item)
        notifyItemInserted(groceryItems.lastIndex)
    }

    public fun updateItem(item: Item, editIndex: Int) {
        groceryItems.set(editIndex, item)
        notifyItemChanged(editIndex)
    }

    public fun deleteAll() {
        groceryItems.clear()
        notifyDataSetChanged()
    }


    fun onDismissed(position: Int) {
        deleteItem(position)
    }

    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(groceryItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.tvName
        val tvPrice = itemView.tvPrice
        val btnDelete = itemView.btnDelete
    }
}