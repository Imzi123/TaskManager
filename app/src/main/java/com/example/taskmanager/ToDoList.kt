package com.example.taskmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.database.ToDoDatabase
import com.example.taskmanager.database.repository.ToDoRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoList<ToDoRepo, ToDo> : AppCompatActivity() {
    private lateinit var adapter:TaskAdapter<Any?, Any?>
    private lateinit var viewModel:ToDoViewModel<Any?>
    private var repository: ToDoRepo = TODO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_to_do_list)
        repository = ToDoRepo(ToDoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(R.id.rvToDoList)
        viewModel = ViewModelProvider(this)[ToDoViewModel::class.java] as ToDoViewModel<Any?>

        adapter = TaskAdapter(mutableListOf(), repository, viewModel, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.data.observe(this) {
            adapter.updateData(it)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val data = (repository as Any).getAllTodoItems()
            runOnUiThread {
                viewModel.setData(data)
            }
        }
        val btnAddItem: Button = findViewById(R.id.btnAddToDo)
        btnAddItem.setOnClickListener {
            displayDialog(repository)
        }
    }

    private fun displayDialog(repository: ToDoRepo) {
        val builder = AlertDialog.Builder(this)
        // Set the alert dialog title and message
        builder.setTitle("Enter New Todo item:")
        builder.setMessage("Enter the todo item below:")
        // Create an EditText input field
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val item = input.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(ToDo(item))
                val data = repository.getAllTodoItems()
                runOnUiThread {
                    viewModel.setData(data)
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    fun showEditDialog(todo: ToDo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Todo item:")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(todo.item)
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val updatedItem = input.text.toString()
            todo.item = updatedItem
            CoroutineScope(Dispatchers.IO).launch {
                repository.update(todo)
                val data = repository.getAllTodoItems()
                runOnUiThread {
                    viewModel.setData(data)
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

}