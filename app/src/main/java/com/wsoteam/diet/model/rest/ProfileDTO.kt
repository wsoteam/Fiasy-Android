package com.wsoteam.diet.model.rest

import com.google.gson.annotations.SerializedName

data class ProfileDTO(
        @SerializedName("id") var id : Int?,
        @SerializedName("uid") var uid : String?,
        @SerializedName("image") var image : String = "",
        @SerializedName("gender") var gender : String = "",
        @SerializedName("activity") var activity : String = "",
        @SerializedName("goals") var goals : String = "",
        @SerializedName("age") var age : Int?,
        @SerializedName("height") var  height : Double?,
        @SerializedName("weight") var weight : Double?,
        @SerializedName("birth_date") var birthDate : Double?,
        @SerializedName("max_carbo") var maxCarbo : Double?,
        @SerializedName("max_fats") var maxFats : Double?,
        @SerializedName("max_calories") var maxCalories : Double?,
        @SerializedName("max_proteins") var maxProteins : Double?,
        @SerializedName("water_count") var waterCount : Double?,
        @SerializedName("user") var user : String?
)