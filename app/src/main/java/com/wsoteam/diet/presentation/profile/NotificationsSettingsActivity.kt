package com.wsoteam.diet.presentation.profile

import android.content.SharedPreferences
import android.os.Bundle

import com.onesignal.OneSignal
import com.wsoteam.diet.R
import com.wsoteam.diet.utils.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class NotificationsSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_settings)
        setTitle(R.string.settings_notifications_title)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        attachOnce(NotificationsSettingsFragment(), "notifications_settings", R.id.container)
    }

    class NotificationsSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val tags = arrayOf("water_pushes_enabled", "nutrition_pushes_enabled", "weight_pushes_enabled")

        override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
            if(key in tags) {
                OneSignal.sendTag(key, preferences.getBoolean(key, true).toString())
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.notification_preferences, rootKey)

            tags.forEach {
                findPreference<SwitchPreference>(it)?.isChecked =
                        preferenceManager.sharedPreferences.getBoolean(it, true)
            }

            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onDestroy() {
            super.onDestroy()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }
    }
}
