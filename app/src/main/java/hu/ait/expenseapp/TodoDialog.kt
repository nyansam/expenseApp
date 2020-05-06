package hu.ait.expenseapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import hu.ait.expenseapp.data.Todo
import kotlinx.android.synthetic.main.todo_dialog.view.*
import kotlinx.android.synthetic.main.todo_row.*
import java.util.*


class TodoDialog : DialogFragment() {

    interface TodoHandler{
        fun todoCreated(todo: Todo)
        fun todoUpdated(todo: Todo)
    }

    lateinit var todoHandler: TodoHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is TodoHandler){
            todoHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.exception1))
        }
    }

    lateinit var etTodoText: EditText
    lateinit var spinnerCategory: Spinner
    lateinit var etTodoPrice: EditText
    lateinit var etTodoDesc: EditText
    lateinit var cbTodoDone: CheckBox



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())


        dialogBuilder.setTitle(getString(R.string.new1))



        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.todo_dialog, null
        )


        etTodoText = dialogView.etTodoText
        spinnerCategory = dialogView.spinnerCategory
        etTodoPrice = dialogView.etTodoPrice
        etTodoDesc = dialogView.etTodoDesc
        cbTodoDone = dialogView.cbTodoDone

        var categoryAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCategory.adapter = categoryAdapter
        //spinnerCategory.setSelection(1)


        dialogBuilder.setView(dialogView)

        val arguments = this.arguments
        // if we are in EDIT mode


        if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
            val todoItem = arguments.getSerializable(ScrollingActivity.KEY_EDIT) as Todo

            etTodoText.setText(todoItem.todoText)
            etTodoDesc.setText(todoItem.todoDesc)
            etTodoPrice.setText(todoItem.todoPrice.substring(1))
            cbTodoDone.isChecked = todoItem.done
            spinnerCategory.setSelection(todoItem.category)

            dialogBuilder.setTitle(getString(R.string.edit_item))
        }

        dialogBuilder.setPositiveButton(getString(R.string.ok)) {
                dialog, which ->
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) {
                dialog, which ->
        }


        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etTodoText.text.isNotEmpty()) {
                val arguments = this.arguments
                // IF EDIT MODE
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
                    handleTodoEdit()
                } else {
                    handleTodoCreate()
                }

                dialog!!.dismiss()
            } else {
                etTodoText.error = getString(R.string.hint2)
            }
        }


    }

    private fun handleTodoCreate() {


        todoHandler.todoCreated(
            Todo(
                null,
                false,
                etTodoText.text.toString(),
                spinnerCategory.selectedItemPosition,
                (getString(R.string.DOLLAR)+ etTodoPrice.text.toString()),
                etTodoDesc.text.toString()
            )
        )
    }

    private fun handleTodoEdit() {
        val todoToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_EDIT
        ) as Todo
        todoToEdit.todoText = etTodoText.text.toString()
        todoToEdit.done = cbTodoDone.isChecked
        todoToEdit.todoDesc = etTodoDesc.text.toString()
        todoToEdit.todoPrice = (getString(R.string.DOLLAR)+ etTodoPrice.text.toString())
        todoToEdit.category = spinnerCategory.selectedItemPosition

        todoHandler.todoUpdated(todoToEdit)
    }


}