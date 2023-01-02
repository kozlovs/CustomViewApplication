package ru.netology.customviewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.customviewapplication.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<StatsView>(R.id.statsView).data = listOf(
            0.25F, 0.25F, 0.25F, 0.25F,
        )
    }
}