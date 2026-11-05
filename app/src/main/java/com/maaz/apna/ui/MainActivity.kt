package com.maaz.apna.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.maaz.apna.R
import com.maaz.apna.Settings
import com.maaz.apna.databinding.ActivityMainBinding

/** Home / Models / Settings, switched with the bottom bar. */
class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = Settings(this)
        if (!settings.onboarded) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)
        ui.root.padForSystemBars()
        // The root already keeps clear of the navigation bar, so the bottom bar mustn't add its own padding.
        ViewCompat.setOnApplyWindowInsetsListener(ui.nav) { _, insets -> insets }
        ui.nav.setOnItemSelectedListener { item ->
            show(
                when (item.itemId) {
                    R.id.nav_models -> ModelsFragment()
                    R.id.nav_settings -> SettingsFragment()
                    else -> HomeFragment()
                }
            )
            true
        }
        if (savedInstanceState == null) {
            ui.nav.selectedItemId = intent.getIntExtra(EXTRA_TAB, R.id.nav_home)
        }
    }

    fun goTo(tabId: Int) { ui.nav.selectedItemId = tabId }

    private fun show(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.pages, fragment).commit()
    }

    companion object {
        const val EXTRA_TAB = "tab"
    }
}
