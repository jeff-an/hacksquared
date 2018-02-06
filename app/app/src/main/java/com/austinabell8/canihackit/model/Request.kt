package com.austinabell8.canihackit.model

import com.google.gson.annotations.SerializedName

/**
 * Created by austi on 2018-02-03.
 */
data class Request(@SerializedName("name") val name: String?,
                   @SerializedName("description") val description: String?,
                   @SerializedName("sites") val sites: Sites)
