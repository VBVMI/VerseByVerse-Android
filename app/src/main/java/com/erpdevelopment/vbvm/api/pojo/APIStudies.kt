package com.erpdevelopment.vbvm.api.pojo

import com.erpdevelopment.vbvm.room.RStudy
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class APIStudies(
        @SerializedName("studies")
        @Expose
        val studies: List<RStudy>
)