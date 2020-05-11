package hu.ait.expenseapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.ait.expenseapp.adapter.ItemAdapter
import hu.ait.expenseapp.data.ItemDatabase
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val categoryName: String? = intent.getStringExtra(ScrollingActivity.KEY_DATA)

        initRecyclerView(categoryName)
    }

    private fun initRecyclerView(categoryName: String?) {
        Thread {
            var categoryList = ItemDatabase.getInstance(this).itemDao().getAllInCategory(categoryName)

            runOnUiThread {
                itemAdapter = ItemAdapter(this, categoryList)
                recyclerItem.adapter = itemAdapter
            }
        }.start()
    }


    override fun onBackPressed() {
        finish()
    }
}