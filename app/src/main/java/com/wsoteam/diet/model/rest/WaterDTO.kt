package com.wsoteam.diet.model.rest

import com.google.gson.annotations.SerializedName

data class WaterDTO(@SerializedName("id") var id: Int? = null,
                    @SerializedName("amount") var amount: Int = 0,
                    @SerializedName("timestamp") var timestamp: String? = null
)