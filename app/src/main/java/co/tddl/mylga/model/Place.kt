package co.tddl.mylga.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    val id: String,
    val place_id: String,
    val description: String,
    val reference: String
    ) : Parcelable