package co.tddl.mylga.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LocationHelper(context: Context, activity: Activity){

    private var _context: Context? = null
    private var _activity: Activity? = null

    init{
        this._context = context
        this._activity = activity
    }

    fun permissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(this._context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(this._activity!!, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
    }
}