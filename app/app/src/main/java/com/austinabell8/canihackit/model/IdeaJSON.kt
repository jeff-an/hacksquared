package com.austinabell8.canihackit.model

import com.google.gson.annotations.SerializedName

/**
 * Created by austi on 2018-02-03.
 */
class IdeaJSON(@SerializedName("idea") val results:ArrayList<ResultItem>)