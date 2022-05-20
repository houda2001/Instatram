package com.example.instatram

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class Splash : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        setContentView(R.layout.activity_splash)


        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                val mainIntent = Intent(this@Splash, HomeActivity::class.java)
                this@Splash.startActivity(mainIntent)
                finish()
            }
        }
    }
}