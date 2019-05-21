package co.tddl.mylga.networking


import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/"

val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface MapApiService {
    @GET("json")
    fun getMatch(@Query("input") input: String, @Query("key") key: String, @Query("location") location: String):
            Deferred<JsonObject>

}

object MapApi {
    val retrofitService : MapApiService by lazy { retrofit.create(MapApiService::class.java) }
}