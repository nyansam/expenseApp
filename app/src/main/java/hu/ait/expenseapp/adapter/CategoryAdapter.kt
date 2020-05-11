package hu.ait.expenseapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.ait.expenseapp.R
import hu.ait.expenseapp.ScrollingActivity
import hu.ait.expenseapp.data.AppDatabase
import hu.ait.expenseapp.data.Category
import hu.ait.expenseapp.data.ItemDatabase
import hu.ait.expenseapp.touch.TodoTouchHelperCallback
import kotlinx.android.synthetic.main.todo_row.view.*
import java.util.*

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>,
    TodoTouchHelperCallback {

    var categoryItems = mutableListOf<Category>()
    val context: Context

    constructor(context: Context, listCategories: List<Category>) {
        this.context = context
        categoryItems.addAll(listCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.todo_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory = itemView.tvCategory
        val tvPrice = itemView.tvPrice
        val tvNumItems = itemView.tvNumItems

        val btnAdd = itemView.btnAdd
        val btnDelete = itemView.btnDelete
        val btnDetails = itemView.btnDetails
        val ivIcon = itemView.ivIcon

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = categoryItems[position]

        holder.tvCategory.text = currentItem.categoryName

        Thread {
            var allItems = ItemDatabase.getInstance(context).itemDao()
                .getAllInCategory(currentItem.categoryName)
            holder.tvNumItems.text = "Num items: " + allItems.size.toString()
            var total = 0
            for (item in allItems){
                total += item.price.toInt()
            }
            holder.tvPrice.text = "Total price: " + total.toString()
        }.start()


        holder.btnDelete.setOnClickListener {
            deleteCategory(holder.adapterPosition)
            (context as ScrollingActivity).deleteCategoryItems(currentItem.categoryName)
        }

        holder.btnAdd.setOnClickListener {
            (context as ScrollingActivity).showAddItemDialog(currentItem.categoryName)
        }

        holder.btnDetails.setOnClickListener {
            (context as ScrollingActivity).showCategoryDetails(currentItem.categoryName)
        }


        if(categoryItems[holder.adapterPosition].type == 0) {
            holder.ivIcon.setImageResource(R.drawable.decrease)
        } else if(categoryItems[holder.adapterPosition].type == 1) {
            holder.ivIcon.setImageResource(R.drawable.increase)
        }

    }


    fun deleteAll() {
        categoryItems.clear()
        notifyDataSetChanged()
    }

    private fun deleteCategory(position: Int) {
        Thread {
            AppDatabase.getInstance(context).categoryDao().deleteCategory(
                categoryItems.get(position))

            (context as ScrollingActivity).runOnUiThread {
                categoryItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }


    public fun addCategory(category: Category) {
        categoryItems.add(category)

        notifyItemInserted(categoryItems.lastIndex)
    }

    public fun updateCategory(categories: MutableList<Category>) {
        categoryItems = categories
        notifyDataSetChanged()
    }

    override fun onDismissed(position: Int) {
        deleteCategory(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(categoryItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

}
