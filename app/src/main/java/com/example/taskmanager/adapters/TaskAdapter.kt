package com.example.taskmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter<ToDoRepo, ToDo>(
    items: MutableList<ToDo>, repository: ToDoRepo,
    viewModel: ToDoViewModel<Any?>,
    activity: ToDoList,
) : RecyclerView.Adapter<ToDoViewHolder>() {
    var context: Context? = null
    val items = items
    val repository = repository
    val viewModel = viewModel
    val activity = activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
        context = parent.context
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = items[position]
        holder.cbTodo.text = currentItem.item
        holder.cbTodo.setOnLongClickListener {
            activity.showEditDialog(items[position]) // Call showEditDialog from ToDoList instance
            true
        }
        holder.ivDelete.setOnClickListener {
            val isChecked = holder.cbTodo.isChecked
            if(isChecked){
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(items.get(position))
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main){
                        viewModel.setData(data)
                    }
                }
                Toast.makeText(context,"Item Deleted", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,"Select the item to delete", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun updateData(newItems: List<ToDo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return items.size
    }
}