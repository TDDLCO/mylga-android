package co.tddl.mylga.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {

    val PREF_NAME = "MYLGA_INSTALLED"

    private var ctx: Context
    lateinit var sharedPref: SharedPreferences

    constructor(ctx: Context){
        this.ctx = ctx
    }

    private fun getSharedPref(){
        sharedPref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun writeSharedPref(){
        var editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(PREF_NAME, "yes")
        editor.apply()
    }

    fun checkSharedPref() : Boolean{
        var status: Boolean = false

        if( ! sharedPref.getString(PREF_NAME, "null").equals("null")){
            status = true
        }

        return status
    }

    fun clearSharedPref(){
        sharedPref.edit().clear().commit()
    }
}