package com.example.instatram

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.instatram.displayImage.DisplayImageViewModel
import com.example.instatram.gridDisplay.PHOTO_ID
import com.example.instatram.gridDisplay.PhotosActivity
import com.example.instatram.data.MyPreferences
import com.example.instatram.data.Photo
import com.example.instatram.ui.home.STATION_ID
import com.example.instatram.ui.home.STATION_NAME
import kotlinx.android.synthetic.main.activity_display_image.*
import kotlinx.android.synthetic.main.activity_view_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


const val DELETED = "com.example.instatram.DELETED"

class ViewImageActivity : AppCompatActivity() {
    private lateinit var viewModel: DisplayImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        viewModel = ViewModelProvider(this).get(DisplayImageViewModel::class.java)
        val listener: MutableLiveData<Photo?> = MutableLiveData<Photo?>()
        listener.setValue(null)

        CoroutineScope(Dispatchers.IO).launch{
            listener.postValue(viewModel.getPhoto(
                intent.getIntExtra(
                    PHOTO_ID,
                    -1
                )
            ))
        }

        listener.observe(this, {
            if (it != null) {
                imageView.setImageBitmap(
                    BitmapFactory.decodeFile(
                        it.imageUri.toUri().path
                    )
                )
                title_image.text = it.name
            }
        })


        button_delete.setOnClickListener {
            if (!viewModel.wasClicked) {
                viewModel.wasClicked = true
                CoroutineScope(Dispatchers.IO).launch {
                    val imageUri =
                        Uri.parse(viewModel.getPhoto(intent.getIntExtra(PHOTO_ID, -1)).imageUri)
                    val fdelete = File(imageUri.path)
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            viewModel.deletePhoto(intent.getIntExtra(PHOTO_ID, -1))
                            val intent =
                                Intent(this@ViewImageActivity, PhotosActivity::class.java).apply {
                                    putExtra(DELETED, "true")
                                    putExtra(STATION_ID, intent.getIntExtra(STATION_ID, -1))
                                    putExtra(STATION_NAME, intent.getStringExtra(STATION_NAME))
                                }
                            this@ViewImageActivity.startActivity(intent)
                            finish()
                        } else {
                            val intent =
                                Intent(this@ViewImageActivity, PhotosActivity::class.java).apply {
                                    putExtra(DELETED, "false")
                                    putExtra(STATION_ID, intent.getIntExtra(STATION_ID, -1))
                                    putExtra(STATION_NAME, intent.getStringExtra(STATION_NAME))
                                }
                            this@ViewImageActivity.startActivity(intent)
                            finish()
                        }
                    }
                    viewModel.wasClicked = false
                }
            }
        }
        //Theme
        fun checkTheme() {
            when (MyPreferences(this).darkMode) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    delegate.applyDayNight()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                }

            }
        }

        checkTheme()
    }

    override fun onResume() {
        super.onResume()
        viewModel.wasClicked = false
    }

    // Action bar menu stuff
    // Theme
    private fun chooseThemeDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choice_theme))
        val styles = arrayOf("Light", "Dark")
        val checkedItem = MyPreferences(this).darkMode

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->

            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    MyPreferences(this).darkMode = 0
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    MyPreferences(this).darkMode = 1
                    delegate.applyDayNight()
                    dialog.dismiss()
                }

            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    //Language

    private fun showChangeLang() {

        val listItmes = arrayOf("Spanish", "English")

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.choice_language))
        mBuilder.setSingleChoiceItems(listItmes, -1) { dialog, which ->
            if (which == 0) {
                setLocate("es")
                recreate()
            } else if (which == 1) {

                setLocate("en")
                recreate()
            }

            dialog.dismiss()
        }
        val mDialog = mBuilder.create()

        mDialog.show()

    }

    private fun setLocate(Lang: String) {

        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    // Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(TAB, "home")
                startActivity(intent)
                finish()
                return true
            }
            R.id.map ->{
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(TAB, "map")
                startActivity(intent)
                finish()
                return true
            }
            R.id.settings ->{
                return true
            }
            R.id.language -> {
                showChangeLang()
                return true
            }
            R.id.theme -> {
                chooseThemeDialog()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}