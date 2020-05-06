package hu.ait.expenseapp


//import com.google.android.material.snackbar.Snackbar
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import hu.ait.expenseapp.adapter.TodoAdapter
import hu.ait.expenseapp.data.AppDatabase
import hu.ait.expenseapp.data.Todo
import hu.ait.expenseapp.touch.TodoReyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*


class ScrollingActivity : AppCompatActivity(), TodoDialog.TodoHandler {

    lateinit var todoAdapter: TodoAdapter

    companion object {
        const val KEY_EDIT = "KEY_EDIT"

        const val PREF_NAME = "PREFTODO"
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
            var todoList = AppDatabase.getInstance(this).todoDao().getAllTodos()

            runOnUiThread {
                todoAdapter = TodoAdapter(this, todoList)
                recyclerTodo.adapter = todoAdapter

                val touchCallbakList = TodoReyclerTouchCallback(todoAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                itemTouchHelper.attachToRecyclerView(recyclerTodo)
            }
        }.start()
    }

    fun showAddTodoDialog() {
        TodoDialog().show(supportFragmentManager, getString(R.string.dialog))
    }

    var editIndex: Int = -1

    public fun showEditTodoDialog(todoToEdit: Todo, index: Int) {
        editIndex = index

        val editItemDialog = TodoDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_EDIT, todoToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager, getString(R.string.edit))

    }

    fun saveTodo(todo: Todo) {
        Thread {
            todo.todoId = AppDatabase.getInstance(this).todoDao().insertTodo(todo)

            runOnUiThread {
                todoAdapter.addTodo(todo)
            }
        }.start()
    }

    override fun todoCreated(todo: Todo) {
        saveTodo(todo)
    }

    override fun todoUpdated(todo: Todo) {
        Thread {
            AppDatabase.getInstance(this).todoDao().updateTodo(todo)

            runOnUiThread {
                todoAdapter.updateTodo(todo, editIndex)
            }
        }.start()
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

            showAddTodoDialog()


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
                        AppDatabase.getInstance(this@ScrollingActivity).todoDao().deleteAll()
                        runOnUiThread {
                            todoAdapter.deleteAll()
                        }
                    }.start()
                }
            })

            app_bar.startAnimation(sendAnim)




        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        Toast.makeText(this,
            "You can not exit",
            Toast.LENGTH_LONG).show()
    }


}
