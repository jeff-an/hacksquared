package com.austinabell8.canihackit.model

import com.google.gson.annotations.SerializedName

/**
 * Created by austi on 2018-02-03.
 */
data class Sites(@SerializedName("devpost") val devpost: String,
                 @SerializedName("github") val github: String,
                 @SerializedName("producthunt") val productHunt: String,
                 @SerializedName("googleplay") val googlePlay: String)