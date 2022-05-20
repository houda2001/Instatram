package com.example.instatram.gridDisplay

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instatram.DELETED
import com.example.instatram.HomeActivity
import com.example.instatram.R
import com.example.instatram.TAB
import com.example.instatram.data.MyPreferences
import com.example.instatram.data.Photo
import com.example.instatram.displayImage.CREATED
import com.example.instatram.displayImage.DisplayImageActivity
import com.example.instatram.displayImage.DisplayImageViewModel
import com.example.instatram.ui.home.STATION_ID
import com.example.instatram.ui.home.STATION_NAME
import kotlinx.android.synthetic.main.activity_photos.*
import kotlinx.android.synthetic.main.activity_photos.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*


const val IMAGE = "com.example.instatram.IMAGE"

class PhotosActivity : AppCompatActivity() {
    private lateinit var viewModel: DisplayImageViewModel
    lateinit var imageFilePath:String
    val CAMERA_REQUEST_CODE =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        val wasDeleted = intent.getStringExtra(DELETED)

        if(wasDeleted != null){
            if (wasDeleted == "true"){
                Toast.makeText(applicationContext, R.string.delete_success, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, R.string.delete_failed, Toast.LENGTH_LONG).show()
            }
        }


        val title: View = findViewById<TextView>(R.id.textView)
        title.textView.text = intent.getStringExtra(STATION_NAME)
        actionBar?.title = intent.getStringExtra(STATION_NAME)

        viewModel = ViewModelProvider(this).get(DisplayImageViewModel::class.java)

        val cardImages: MutableList<String> = emptyList<String>().toMutableList()
        val cardTitles: MutableList<String> = emptyList<String>().toMutableList()
        val cardDates: MutableList<String> = emptyList<String>().toMutableList()
        val cardIds: MutableList<Int> = emptyList<Int>().toMutableList()

        val stationId = intent.getIntExtra(STATION_ID, -1)
        val stationName = intent.getStringExtra(STATION_NAME)
        var date: String

        val listener: MutableLiveData<List<Photo>?> = MutableLiveData<List<Photo>?>()
        listener.setValue(null)

        CoroutineScope(Dispatchers.IO).launch {
            listener.postValue(viewModel.getPhotos(intent.getIntExtra(STATION_ID, -1)))
        }

        listener.observe(this, {
            if (it != null) {
                for (photo in it){
                    cardTitles.add(photo.name)
                    cardImages.add(photo.imageUri)
                    cardIds.add(photo.id)
                    val dateRaw = photo.date
                    date = dateRaw.substring(0,2) + "/" + dateRaw.substring(2,4) + "/" + dateRaw.substring(4,8) + "\n"
                    date += dateRaw.substring(8,10) + ":" + dateRaw.substring(10,12) + ":" + dateRaw.substring(12,14)
                    cardDates.add(date)
                }
            }
            val adapter = GridItemAdapter(viewModel ,cardTitles, cardImages, cardDates, cardIds, stationId, stationName)
            val gridLayout = GridLayoutManager(this@PhotosActivity, 2)
            gridItems.layoutManager = gridLayout
            gridItems.adapter = adapter
        })


        if(intent.getStringExtra(CREATED) != null) {
            val success: String? = intent.getStringExtra(CREATED)
            if (success == "true") {
                Toast.makeText(applicationContext, R.string.created_success, Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(applicationContext, R.string.created_failed, Toast.LENGTH_LONG)
                    .show()
            }
            intent.removeExtra(CREATED)
        }



        button_camera.setOnClickListener {

            try {
                val imageFile = createTempFile()
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if(callCameraIntent.resolveActivity(packageManager) != null) {
                    val authorities = packageName + ".fileProvider"
                    val imageUri = FileProvider.getUriForFile(this, authorities, imageFile)
                    callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
                }
            } catch (e: IOException) {
                Toast.makeText(this, "Could not create file!", Toast.LENGTH_SHORT).show()
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




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intent = Intent(this, DisplayImageActivity::class.java).apply {
                        putExtra(STATION_ID, intent.getIntExtra(STATION_ID, -1))
                        putExtra(STATION_NAME, intent.getStringExtra(STATION_NAME))
                        putExtra(IMAGE, imageFilePath)
                    }
                    this.startActivity(intent)
                    this@PhotosActivity.finish()
                }
            }
        }
    }



    @Throws(IOException::class)
    fun createTempFile(): File {
        val imageFileName = "temp"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(!storageDir?.exists()!!) storageDir.mkdirs()
        val imageFile = File.createTempFile(imageFileName, ".png", storageDir)
        imageFilePath = imageFile.absolutePath
        return imageFile
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