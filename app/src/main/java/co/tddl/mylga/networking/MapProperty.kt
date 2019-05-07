package co.tddl.mylga.networking

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapProperty(
    val userId: Int,
    // used to map img_src from the JSON to imgSrcUrl in our class
    //@Json(name = "img_src") val imgSrcUrl: String,
    val id: Int,
    val title: String) : Parcelable {
    /*val isRental
        get() = type == "rent" */
}