package hu.ait.expenseapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.ait.expenseapp.R
import hu.ait.expenseapp.ScrollingActivity
import hu.ait.expenseapp.data.AppDatabase
import hu.ait.expenseapp.data.Todo
import hu.ait.expenseapp.touch.TodoTouchHelperCallback
import kotlinx.android.synthetic.main.todo_row.view.*
import java.lang.Appendable
import java.util.*

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>,
    TodoTouchHelperCallback {

    var todoItems = mutableListOf<Todo>()
    val context: Context

    constructor(context: Context, listTodos: List<Todo>) {
        this.context = context
        todoItems.addAll(listTodos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.todo_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTodo = itemView.tvTodo
        val tvPrice = itemView.tvPrice
        val tvDesc = itemView.tvDesc


        val cbDone = itemView.cbDone
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val ivIcon = itemView.ivIcon


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTodo = todoItems[position]

        holder.tvTodo.text = currentTodo.todoText
        holder.tvPrice.text = currentTodo.todoPrice
        holder.tvDesc.text = currentTodo.todoDesc



        holder.cbDone.text = context.getString(R.string.purchased)
        holder.cbDone.isChecked = currentTodo.done

        holder.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditTodoDialog(
                todoItems[holder.adapterPosition], holder.adapterPosition
            )
        }

        holder.cbDone.setOnClickListener {
            todoItems[holder.adapterPosition].done = holder.cbDone.isChecked
            Thread{
                AppDatabase.getInstance(context).todoDao().updateTodo(todoItems[holder.adapterPosition])
            }.start()
        }

        if(todoItems[holder.adapterPosition].category == 0) {
            holder.ivIcon.setImageResource(R.drawable.food)
        } else if(todoItems[holder.adapterPosition].category == 1) {
            holder.ivIcon.setImageResource(R.drawable.clothes)
        } else if (todoItems[holder.adapterPosition].category == 2) {
            holder.ivIcon.setImageResource(R.drawable.beauty)
        }


    }


    fun deleteAll() {
        todoItems.clear()
        notifyDataSetChanged()
    }

    private fun deleteTodo(position: Int) {
        Thread {
            AppDatabase.getInstance(context).todoDao().deleteTodo(
                todoItems.get(position))

            (context as ScrollingActivity).runOnUiThread {
                todoItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }

    public fun addTodo(todo: Todo) {
        todoItems.add(todo)

        //notifyDataSetChanged() // this refreshes the whole list
        notifyItemInserted(todoItems.lastIndex)
    }

    public fun updateTodo(todo: Todo, editIndex: Int) {
        todoItems.set(editIndex, todo)
        notifyItemChanged(editIndex)
    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(todoItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

}
