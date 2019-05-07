package co.tddl.mylga.networking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.tddl.mylga.BuildConfig
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class MapApiStatus{ LOADING, ERROR, DONE }

class FetchPlaces {
    val _status = MutableLiveData<MapApiStatus>()

    val status: LiveData<MapApiStatus>
        get() = _status

    val _properties = MutableLiveData<JsonObject>()

    val properties: LiveData<JsonObject>
        get() = _properties

    val _navigateToSelectedProperty = MutableLiveData<MapProperty>()

    val navigateToSelectedProperty: LiveData<MapProperty>
        get() = _navigateToSelectedProperty


    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)



    fun getMapLocationSuggestions(input: String, key: String) {
         Log.d("CRT", "Launched")
        coroutineScope.launch {
            Log.d("CRT", "Launched in coroutine")
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MapApi.retrofitService.getMatch(input, key)
            try {
                _status.value = MapApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                val listResult = getPropertiesDeferred.await()
                _status.value = MapApiStatus.DONE
                _properties.value = listResult
                Log.d("RESULTS", "DONE")
                Log.d("RESULTS_data", listResult.toString())
            } catch (e: Exception) {
                _status.value = MapApiStatus.ERROR
                _properties.value = JsonObject()
                Log.d("RETURN ERR", e.toString())
            }

        }
    }

    fun parseJson(jsonObject: JsonObject){

    }
}
