package com.example.noteslist.presentation

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.noteslist.R
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private var lastBackPressedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navHost = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                val navController = navHost?.navController
                val currentDestinationId = navController?.currentDestination?.id

                if (currentDestinationId != R.id.noteListFragment) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                    return
                }

                val now = SystemClock.elapsedRealtime()
                val isSecondBackClick = now - lastBackPressedAt <= BACK_PRESS_WINDOW_MS
                lastBackPressedAt = now

                if (isSecondBackClick) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Выход")
                        .setMessage("Закрыть приложение?")
                        .setPositiveButton("Да") { _, _ -> finish() }
                        .setNegativeButton("Нет", null)
                        .show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Нажмите назад ещё раз для выхода",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    companion object {
        private const val BACK_PRESS_WINDOW_MS = 2000L
    }
}
