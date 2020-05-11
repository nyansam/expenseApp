package hu.ait.expenseapp


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import hu.ait.expenseapp.adapter.CategoryAdapter
import hu.ait.expenseapp.adapter.ItemAdapter
import hu.ait.expenseapp.data.AppDatabase
import hu.ait.expenseapp.data.Category
import hu.ait.expenseapp.data.Item
import hu.ait.expenseapp.data.ItemDatabase
import hu.ait.expenseapp.touch.TodoReyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.activity_scrolling.app_bar
import kotlinx.android.synthetic.main.activity_scrolling.toolbar
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*


class ScrollingActivity : AppCompatActivity(), CategoryDialog.CategoryHandler, ItemDialog.ItemHandler {

    lateinit var categoryAdapter: CategoryAdapter
    lateinit var itemAdapter: ItemAdapter

    companion object {

        const val KEY_DATA = "KEY_DATA"
        const val PREF_NAME = "PREF_NAME"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_LAST_USED = "KEY_LAST_USED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)
        initRecyclerView()

    }

    fun saveStartInfo() {
        var sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.putString(KEY_LAST_USED, Date(System.currentTimeMillis()).toString())
        editor.apply()
    }

    fun wasStartedBefore(): Boolean {
        var sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var lastTime = sharedPref.getString(KEY_LAST_USED, getString(R.string.welcome))
        Toast.makeText(this, lastTime, Toast.LENGTH_LONG).show()

        return sharedPref.getBoolean(KEY_STARTED, false)
    }

    private fun initRecyclerView() {
        Thread {
            var categoryList = AppDatabase.getInstance(this).categoryDao().getAllCategories()

            runOnUiThread {
                categoryAdapter = CategoryAdapter(this, categoryList)
                recyclerCategory.adapter = categoryAdapter

                val touchCallbakList = TodoReyclerTouchCallback(categoryAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                itemTouchHelper.attachToRecyclerView(recyclerCategory)
            }
        }.start()

        Thread {
            var itemList = ItemDatabase.getInstance(this).itemDao().getAllItems()

            runOnUiThread {
                itemAdapter = ItemAdapter(this, itemList)
                //recyclerItem.adapter = itemAdapter
            }
        }.start()
    }

    private fun showAddCategoryDialog() {
        CategoryDialog().show(supportFragmentManager, getString(R.string.dialog))
    }

    fun showAddItemDialog(categoryName: String) {
        ItemDialog(categoryName).show(supportFragmentManager, getString(R.string.dialog))
    }

    var editIndex: Int = -1



    fun saveCategory(category: Category) {
        Thread {
            category.categoryId = AppDatabase.getInstance(this).categoryDao().insertCategory(category)

            runOnUiThread {
                categoryAdapter.addCategory(category)
            }
        }.start()
    }

    fun saveItem(item: Item) {
        Thread {
            item.itemId = ItemDatabase.getInstance(this).itemDao().insertItem(item)

            runOnUiThread {
                itemAdapter.addItem(item)
            }
        }.start()
        updateCategories()
    }

    public fun deleteCategoryItems(categoryName: String){
        Thread {
            ItemDatabase.getInstance(this).itemDao().deleteAllInCategory(categoryName)
        }
    }

    override fun categoryCreated(category: Category) {
        saveCategory(category)
    }

    public fun updateCategories() {
        Thread {
            var categories = AppDatabase.getInstance(this).categoryDao().getAllCategories()

            for (category in categories){
                Thread {
                    var allItems = ItemDatabase.getInstance(this).itemDao()
                        .getAllInCategory(category.categoryName)
                    var total = 0
                    for (item in allItems){
                        total += item.price.toInt()
                    }
                    category.numItems = allItems.size
                    category.totalAmount = total
                }.start()
            }
            runOnUiThread {
                categoryAdapter.updateCategory(categories)
            }
        }.start()

    }

    override fun itemCreated(item: Item) {
        saveItem(item)
    }

    //MENU

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)

        Handler().post {
            if (!wasStartedBefore()) {
                MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.action_item)
                    .setPrimaryText(getString(R.string.hint1))
                    .setSecondaryText(getString(R.string.hint3))
                    .show()
            }

            saveStartInfo()



        }
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_item){
            showAddCategoryDialog()
        }
        else if (item.itemId == R.id.action_delete){
            var sendAnim = AnimationUtils.loadAnimation(this, R.anim.send_anim)

            sendAnim.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {
                }
                override fun onAnimationEnd(animation: Animation?) {

                }
                override fun onAnimationStart(animation: Animation?) {
                    Thread {
                        AppDatabase.getInstance(this@ScrollingActivity).categoryDao().deleteAll()
                        runOnUiThread {
                            categoryAdapter.deleteAll()
                        }
                    }.start()

                    Thread {
                        ItemDatabase.getInstance(this@ScrollingActivity).itemDao().deleteAll()
                        runOnUiThread {
                            itemAdapter.deleteAll()
                        }
                    }.start()
                }
            })

            app_bar.startAnimation(sendAnim)




        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Toast.makeText(this,
            "You can not exit",
            Toast.LENGTH_LONG).show()
    }

    fun showCategoryDetails(categoryName: String) {
        val intentDetails = Intent(this, DetailsActivity::class.java)
        intentDetails.putExtra(KEY_DATA, categoryName)
        startActivity(intentDetails)
    }

}
