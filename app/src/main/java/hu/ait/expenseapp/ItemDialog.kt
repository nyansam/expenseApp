package hu.ait.expenseapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import hu.ait.expenseapp.data.Item
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.android.synthetic.main.item_dialog.view.*
import kotlinx.android.synthetic.main.item_dialog.view.etItemPrice

class ItemDialog(category: String) : DialogFragment() {

    var categoryName = category

    interface ItemHandler{
        fun itemCreated(item: Item)
    }

    lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ItemHandler){
            itemHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.exception1))
        }
    }

    lateinit var etItemName: EditText
    lateinit var etItemPrice: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle(getString(R.string.new2))
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.item_dialog, null
        )

        etItemName = dialogView.etItemName
        etItemPrice = dialogView.etItemPrice

        var itemAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        itemAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
        }


        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemName.text.isEmpty()) {
                etItemName.error = getString(R.string.hint2)
            } else if (etItemPrice.text.isEmpty()) {
                etItemPrice.error = getString(R.string.hint2)
            } else {
                handleItemCreate()
                dialog!!.dismiss()
            }
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(
            Item(
                null,
                etItemName.text.toString(),
                etItemPrice.text.toString(),
                categoryName
            )
        )
    }

}