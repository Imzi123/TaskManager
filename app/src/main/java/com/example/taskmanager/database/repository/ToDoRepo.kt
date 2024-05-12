package com.example.taskmanager.database.repository

import com.example.taskmanager.database.ToDoDatabase


class ToDoRepo(private val db: ToDoDatabase) {
    suspend fun insert(todo: ToDo) = db.getTodoDao().insertTodo(todo)
    suspend fun delete(todo:ToDo) = db.getTodoDao().deleteTodo(todo)
    fun getAllTodoItems(): List<com.example.assignment4.database.entities.ToDo> = db.getTodoDao().getAllTodoItems()

    suspend fun update(todo:ToDo) = db.getTodoDao().updateTodo(todo)
}