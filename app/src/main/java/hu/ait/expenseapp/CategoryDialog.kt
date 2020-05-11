package hu.ait.expenseapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import hu.ait.expenseapp.data.Category
import kotlinx.android.synthetic.main.todo_dialog.view.*


class CategoryDialog : DialogFragment() {

    interface CategoryHandler{
        fun categoryCreated(category: Category)
        fun categoryUpdated(category: Category)
    }

    lateinit var categoryHandler: CategoryHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)


        if (context is CategoryHandler){
            categoryHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.exception1))
        }
    }

    lateinit var etCategoryText: EditText
    lateinit var spinnerCategory: Spinner



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())


        dialogBuilder.setTitle(getString(R.string.new1))

        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.todo_dialog, null
        )


        etCategoryText = dialogView.etCategoryText
        spinnerCategory = dialogView.spinnerCategory

        var categoryAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCategory.adapter = categoryAdapter

        dialogBuilder.setView(dialogView)


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
            if (etCategoryText.text.isNotEmpty()) {
                handleCategoryCreate()
                dialog!!.dismiss()
            } else {
                etCategoryText.error = getString(R.string.hint2)
            }
        }
    }

    private fun handleCategoryCreate() {
        categoryHandler.categoryCreated(
            Category(
                null,
                etCategoryText.text.toString(),
                spinnerCategory.selectedItemPosition,
                0,
                0)
        )
    }
}