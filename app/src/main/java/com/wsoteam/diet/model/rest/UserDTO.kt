package com.wsoteam.diet.model.rest

import com.google.gson.annotations.SerializedName

data class UserDTO(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("username") var username: String = "",
        @SerializedName("first_name") var firstName: String = "",
        @SerializedName("last_name") var lastName: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("profile") var profile: ProfileDTO? = null,

        @SerializedName("meals") var meals: MutableList<Any>? = null,
        @SerializedName("water_drinking") var waterDrinking: MutableList<Any>? = null,
        @SerializedName("article_series") var articleSeries: MutableList<Any>? = null,
        @SerializedName("diet_plans") var dietPlans: MutableList<Any>? = null,
        @SerializedName("body_measurements") var bodyMeasurements: MutableList<Any>? = null,
        @SerializedName("intermittent_fasting") var intermittentFasting: MutableList<Any>? = null,
        @SerializedName("favorite_products") var favoriteProducts: MutableList<Any>? = null,
        @SerializedName("activities") var activities: MutableList<Any>? = null,
        @SerializedName("activity_time") var activityTime: MutableList<Any>? = null,
        @SerializedName("custom_activities") var customActivities: MutableList<Any>? = null
)