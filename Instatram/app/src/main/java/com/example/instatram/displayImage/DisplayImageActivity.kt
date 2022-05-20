package com.example.instatram.displayImage

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.instatram.gridDisplay.IMAGE
import com.example.instatram.gridDisplay.PhotosActivity
import com.example.instatram.HomeActivity
import com.example.instatram.R
import com.example.instatram.TAB
import com.example.instatram.data.MyPreferences
import com.example.instatram.data.Photo
import com.example.instatram.ui.home.STATION_ID
import com.example.instatram.ui.home.STATION_NAME
import kotlinx.android.synthetic.main.activity_display_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val CREATED = "com.example.instatram.CREATED"


class DisplayImageActivity : AppCompatActivity() {
    private lateinit var imageFilePath:String
    private lateinit var viewModel: DisplayImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)


        val stationId = intent.getIntExtra(STATION_ID, -1)

        val bmp = BitmapFactory.decodeFile(intent.getStringExtra(IMAGE))

        val iv = findViewById<View>(R.id.Thumbnail) as ImageView
        iv.setImageBitmap(bmp)

        // Onclick of saving button
        button_save.setOnClickListener {

            if(editTitle.text.toString().isBlank()){
                Toast.makeText(applicationContext, R.string.title_empty_message, Toast.LENGTH_LONG)
                    .show()
            }else if (editTitle.text.toString().length > 25) {
                Toast.makeText(applicationContext, R.string.title_long_message, Toast.LENGTH_LONG)
                    .show()
            }else {
                val timeStamp: String = SimpleDateFormat("ddMMyyyyHHmmss", Locale.FRANCE).format(Date())
                val imageFile = createImageFile(timeStamp)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        FileOutputStream(imageFile).use { out ->
                            bmp!!.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                out
                            )
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                val title = editTitle.text.toString()
                val photo = Photo(0, title, timeStamp, stationId, imageFile.toURI().toString())

                viewModel = ViewModelProvider(this).get(DisplayImageViewModel::class.java)

                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.insertPhoto(photo)
                }

                val intent = Intent(this, PhotosActivity::class.java).apply {
                    putExtra(CREATED, "true")
                    putExtra(STATION_ID, stationId)
                    putExtra(STATION_NAME, intent.getStringExtra(STATION_NAME))
                    putExtra("title", editTitle.text.toString())
                }
                this.startActivity(intent)
                finish()
            }
        }

        //Onclick of cancel button
        button_cancel.setOnClickListener {
            val fdelete = File(intent.getStringExtra(IMAGE))
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    val intent = Intent(this, PhotosActivity::class.java).apply {
                        putExtra(CREATED, "false")
                        putExtra(STATION_ID, stationId)
                        putExtra(STATION_NAME, intent.getStringExtra(STATION_NAME))
                        putExtra("title", editTitle.text.toString())
                    }
                    this.startActivity(intent)
                    finish()
                }
            }
        }

        //Theme checker
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

    override fun onDestroy() {
        super.onDestroy()
        val fdelete = File(intent.getStringExtra(IMAGE))
        if (fdelete.exists()) {
            fdelete.delete()
        }
    }

    @Throws(IOException::class)
    fun createImageFile(timeStamp: String): File {

        val imageFileName: String = "PNG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(!storageDir?.exists()!!) storageDir.mkdirs()
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = imageFile.absolutePath
        return imageFile
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