package co.tddl.mylga.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(val context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(SharedPreferenceContract.PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Checks if user has installed the application before.
     * @return
     */
    fun isFirstInstallation(): Boolean {
        return ! (sharedPref.contains(SharedPreferenceContract.PREF_INSTALLED)
                && sharedPref.getBoolean(SharedPreferenceContract.PREF_INSTALLED, false))
    }

    /**
     * Sets the value of installed to true.
     */
    fun setFirstInstall() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(SharedPreferenceContract.PREF_INSTALLED, true)
        editor.apply()
    }
}