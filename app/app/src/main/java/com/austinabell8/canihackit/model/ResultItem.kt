package com.austinabell8.canihackit.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ResultItem(@SerializedName("title")val name: String? = "",
                 @SerializedName("origin") val location: String? = "",
                 @SerializedName("tagline") val description: String? = "",
                 @SerializedName("tags") val tags: MutableList<String>?,
                 @SerializedName("ideascore") val ideascore:Double = 0.0,
                 @SerializedName("image_url") val imageUrl: String? = "",
                 @SerializedName("url") val url :String? = "") : Parcelable {

    constructor(parcel: Parcel?) : this(
            parcel?.readString(),
            parcel?.readString(),
            parcel?.readString(),
            mutableListOf<String>().apply {
                parcel?.readList(this, String::class.java.classLoader)
            },
            parcel?.readDouble() as Double,
            parcel?.readString(),
            parcel?.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(description)
        parcel.writeList(tags)
        parcel.writeDouble(ideascore)
        parcel.writeString(imageUrl)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultItem> {
        override fun createFromParcel(parcel: Parcel): ResultItem {
            return ResultItem(parcel)
        }

        override fun newArray(size: Int): Array<ResultItem?> {
            return arrayOfNulls(size)
        }
    }
}

