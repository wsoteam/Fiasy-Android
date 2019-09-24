package com.wsoteam.diet.common.Analytics;

import android.util.Log;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Revenue;
import com.android.billingclient.api.BillingClient;
import com.wsoteam.diet.BranchOfAnalyzer.POJOFoodSQL.Food;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.intercom.android.sdk.Intercom;

public class Events {


    //ONBOARD
    public static final String ONBOARING_NEXT = "onboarding_next";
    public static final String ONBOARING_SKIP = "onboarding_skip";
    public static final String REGISTRATION_SUCCESS = "registration_success";
    public static final String REGISTRATION_ERROR = "registration_error";
    public static final String ENTER_SUCCESS = "enter_success";
    public static final String ENTER_ERROR = "enter_error";
    public static final String REGISRTATION_PRIVACY = "registration_privacy";
    public static final String RESEND_SUCCESS = "resend_success";
    public static final String RESEND_ERROR = "resend_error";
    public static final String QUESTION_NEXT = "question_next";
    public static final String ONBOARDING_SUCCESS = "onboarding_success";
    public static final String REGISTRATION_NEXT = "registration_next";


    //PURCHASE
    public static final String TRIAL_SUCCES = "trial_success";

    public static final String PURCHASE_ERROR = "purchase_error";
    public static final String REVENUE = "revenue";

    //FOOD
    public static final String ADD_FOOD_SUCCESS = "add_food_success";
    public static final String EDIT_FOOD = "edit_food";
    public static final String DELETE_FOOD = "delete_food";
    public static final String FOOD_SEARCH = "food_search";
    public static final String VIEW_PRODUCT_PAGE = "view_product_page";
    public static final String PRODUCT_PAGE_FAVORITES = "product_page_favorites";
    public static final String PRODUCT_PAGE_SHARE = "product_page_share";
    public static final String PRODUCT_PAGE_BUGSEND = "product_page_bugsend";
    public static final String PRODUCT_PAGE_MICRO = "product_page_micro";
    public static final String CUSTOM_PRODUCT_SUCCESS = "custom_product_success";
    public static final String CUSTOM_RECIPE_SUCCESS = "custom_recipe_success";
    public static final String CUSTOM_TEMPLATE_SUCCESS = "custom_template_success";


    //RECIPE
    public static final String VIEW_RECIPE = "view_recipe";
    public static final String RECIPE_CATEGORY = "recipe_category";
    public static final String RECIPE_FAVORITES = "recipe_favorites";
    public static final String RECIPE_ADD_SUCCES = "recipe_add_success";


    //PROFILE
    public static final String VIEW_PROFILE = "view_profile";
    public static final String PROFILE_LOGOUT = "profile_logout";
    public static final String VIEW_SETTINGS = "view_settings";


    //INTERCOM
    public static final String INTERCOM_CHAT = "intercom_chat";


    //ARTICLES
    public static final String CHOOSE_ARTICLES = "choose_articles";
    public static final String VIEW_ARTICLES = "view_articles";
    public static final String SELECT_DIET = "select_diet";
    public static final String SELECT_TRAINING = "select_training";

    //Purchase
    public static final String TRIAL_SUCCESS = "trial_success";
    public static final String PURCHASE_SUCCESS = "purchase_success";
    public static final String PREMIUM_NEXT = "premium_next";

    public static final String TRIAL_ERROR = "trial_error";

    //Purchase
    public static final String DIARY_NEXT = "diary_next";
    public static final int BREAKFAST = 0;
    public static final int LUNCH = 1;
    public static final int DINNER = 2;
    public static final int SNACK = 3;

    //Search
    public static final String SEARCH_SUCCESS = "search_success";

    public static final String ADD_TEMPLATE_SUCCESS = "add_template_success";
    public static final String ADD_CUSTOM_SUCCESS = "add_custom_success";
    public static final String ADD_CUSTOM_RECIPE = "add_custom_recipe";
    public static final String CHANGE_GOAL = "change_goal";

    //Diets
    public static final String CHOOSE_PLAN = "choose_plan";
    public static final String CONNECT_PLAN_SUCCES = "connect_plan_success";
    public static final String WATCH_PLAN_RECIPIES = "watch_plan_recipies";
    public static final String ADD_PLAN_RECIPE_SUCCESS = "add_plan_recipe_success";
    public static final String PLAN_COMPLETE = "plan_complete";
    public static final String LEAVE_PLAN = "leave_plan";

    public static void logAddRecipeInDiaryPlan(String eating, String nameRecipe, String namePlan) {
        String convertedNamePlan = flagToAnalConst(namePlan);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_intake, eating);
            eventProperties.put(EventProperties.recipe_id_add, nameRecipe);
            eventProperties.put(EventProperties.diet_plans_choose, convertedNamePlan);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(RECIPE_ADD_SUCCES, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_intake, eating);
        eventData.put(EventProperties.recipe_id_add, nameRecipe);
        eventData.put(EventProperties.diet_plans_choose, convertedNamePlan);
        io.intercom.android.sdk.Intercom.client().logEvent(RECIPE_ADD_SUCCES, eventData);
    }


    public static void logViewRecipePlan(String name, String namePlan) {
        String convertedNamePlan = flagToAnalConst(namePlan);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_item, name);
            eventProperties.put(EventProperties.diet_plans_choose, convertedNamePlan);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(VIEW_RECIPE, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_item, name);
        eventData.put(EventProperties.diet_plans_choose, convertedNamePlan);
        io.intercom.android.sdk.Intercom.client().logEvent(VIEW_RECIPE, eventData);
    }


    public static void logPlanLeave(String namePlan, int activeDays) {
        String convertedNamePlan = flagToAnalConst(namePlan);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.diet_plans_choose, convertedNamePlan);
            eventProperties.put(EventProperties.active_day, activeDays);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(LEAVE_PLAN, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.diet_plans_choose, convertedNamePlan);
        eventData.put(EventProperties.active_day, activeDays);
        io.intercom.android.sdk.Intercom.client().logEvent(LEAVE_PLAN, eventData);
    }

    public static void logSetUserPropertyError(String message) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("message", message);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent("prop_error", eventProperties);
    }

    public static void logChangeGoal() {
        Amplitude.getInstance().logEvent(CHANGE_GOAL);
        io.intercom.android.sdk.Intercom.client().logEvent(CHANGE_GOAL);
    }


    public static void logPlanComplete(String namePlan, int countRecipes) {
        String convertedNamePlan = flagToAnalConst(namePlan);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.diet_plans_choose, convertedNamePlan);
            eventProperties.put(EventProperties.recipe_added, countRecipes);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(PLAN_COMPLETE, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.diet_plans_choose, convertedNamePlan);
        eventData.put(EventProperties.recipe_added, countRecipes);
        io.intercom.android.sdk.Intercom.client().logEvent(PLAN_COMPLETE, eventData);
    }

    public static void logAddPlanRecipe(String intake, String recipe, String from) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.plan_intake, intake);
            eventProperties.put(EventProperties.recipe_id_plan, recipe);
            eventProperties.put(EventProperties.plan_intake_from, from);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(ADD_PLAN_RECIPE_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.plan_intake, intake);
        eventData.put(EventProperties.recipe_id_plan, recipe);
        eventData.put(EventProperties.plan_intake_from, from);
        io.intercom.android.sdk.Intercom.client().logEvent(ADD_PLAN_RECIPE_SUCCESS, eventData);
    }

    public static void logConnectPlan(String namePlan, String watchPlan, int activeDays) {
        String convertedNamePlan = flagToAnalConst(namePlan);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.diet_plans_choose, convertedNamePlan);
            eventProperties.put(EventProperties.watch_plan, watchPlan);
            eventProperties.put(EventProperties.active_day, activeDays);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(CONNECT_PLAN_SUCCES, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.diet_plans_choose, convertedNamePlan);
        eventData.put(EventProperties.watch_plan, watchPlan);
        eventData.put(EventProperties.active_day, activeDays);
        io.intercom.android.sdk.Intercom.client().logEvent(CONNECT_PLAN_SUCCES, eventData);
    }


    public static String flagToAnalConst(String namePlan) {
        String convertedNamePlan = "";
        switch (namePlan) {
            case "keto":
                convertedNamePlan = EventProperties.diet_plans_keto;
                break;
            case "vegan":
                convertedNamePlan = EventProperties.diet_plans_vegan;
                break;
            case "3week":
                convertedNamePlan = EventProperties.diet_plans_21_day;
                break;
            case "prot":
                convertedNamePlan = EventProperties.diet_plans_high_protein;
                break;
            case "classic":
                convertedNamePlan = EventProperties.diet_plans_classic;
                break;
            case "clear":
                convertedNamePlan = EventProperties.diet_plans_clear;
                break;
            case "mediterranean":
                convertedNamePlan = EventProperties.diet_plans_mediterranean;
                break;
            case "scand":
                convertedNamePlan = EventProperties.diet_plans_scandinavian;
                break;
            case "highLCHF":
                convertedNamePlan = EventProperties.diet_plans_LCHF_strict;
                break;
            case "mediumLCHF":
                convertedNamePlan = EventProperties.diet_plans_LCHF_moderate;
                break;
            case "52":
                convertedNamePlan = EventProperties.diet_plans_5_2;
                break;
            case "lowLCHF":
                convertedNamePlan = EventProperties.diet_plans_LCHF_light;
                break;
            case "61":
                convertedNamePlan = EventProperties.diet_plans_6_1;
                break;
        }
        return convertedNamePlan;
    }


    public static void logViewPlan(String namePlan) {
        String convertedNamePlan = flagToAnalConst(namePlan);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.diet_plans_choose, convertedNamePlan);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(CHOOSE_PLAN, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.diet_plans_choose, convertedNamePlan);
        io.intercom.android.sdk.Intercom.client().logEvent(CHOOSE_PLAN, eventData);
    }

    public static void logViewArticlesDiet() {
        Amplitude.getInstance().logEvent(SELECT_DIET);
        io.intercom.android.sdk.Intercom.client().logEvent(SELECT_DIET);
    }

    public static void logViewArticlesTraining() {
        Amplitude.getInstance().logEvent(SELECT_TRAINING);
        io.intercom.android.sdk.Intercom.client().logEvent(SELECT_TRAINING);
    }

    public static void logAddCustomRecipe(String nameRecipe) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_id_add, nameRecipe);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(ADD_CUSTOM_RECIPE, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_id_add, nameRecipe);
        io.intercom.android.sdk.Intercom.client().logEvent(ADD_CUSTOM_RECIPE, eventData);
    }

    public static void logSearch(String nameFood) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.search_item, nameFood);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(SEARCH_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.search_item, nameFood);
        io.intercom.android.sdk.Intercom.client().logEvent(SEARCH_SUCCESS, eventData);
    }

    public static void logDiaryNext(int number) {
        String eating = "";
        switch (number) {
            case BREAKFAST:
                eating = EventProperties.add_intake_breakfast;
                break;
            case LUNCH:
                eating = EventProperties.add_intake_lunch;
                break;
            case DINNER:
                eating = EventProperties.add_intake_dinner;
                break;
            case SNACK:
                eating = EventProperties.add_intake_snack;
                break;
        }
        Log.e("LOL", eating);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.add_intake, eating);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(DIARY_NEXT, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.add_intake, eating);
        io.intercom.android.sdk.Intercom.client().logEvent(DIARY_NEXT, eventData);
    }

    public static void logTrackRevenue(double price) {
        Revenue revenue = new Revenue().setProductId("com.wild.diet").setPrice(price).setQuantity(1);
        Amplitude.getInstance().logRevenueV2(revenue);
    }

    public static void logPurchaseSuccess() {
        Amplitude.getInstance().logEvent(PURCHASE_SUCCESS);
        io.intercom.android.sdk.Intercom.client().logEvent(PURCHASE_SUCCESS);
    }

    public static void logBillingError(int responseCode) {
        String eventDecryption = "unknown";
        switch (responseCode) {
            case BillingClient.BillingResponse.USER_CANCELED:
                eventDecryption = EventProperties.trial_error_back_or_canceled;
                break;
            case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
                eventDecryption = EventProperties.trial_error_service_unvailable;
                break;
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
                eventDecryption = EventProperties.trial_error_billing_unvailable;
                break;
            case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
                eventDecryption = EventProperties.trial_error_item_unvailable;
                break;
            case BillingClient.BillingResponse.DEVELOPER_ERROR:
                eventDecryption = EventProperties.trial_error_dev_error;
                break;
            case BillingClient.BillingResponse.ERROR:
                eventDecryption = EventProperties.trial_error_error;
                break;
            case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
                eventDecryption = EventProperties.trial_error_already_owned;
                break;
            case BillingClient.BillingResponse.ITEM_NOT_OWNED:
                eventDecryption = EventProperties.trial_error_not_owned;
                break;
        }

        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.trial_error, eventDecryption);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(TRIAL_ERROR, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.trial_error, eventDecryption);
        io.intercom.android.sdk.Intercom.client().logEvent(TRIAL_ERROR, eventData);
    }

    public static void logViewSettings() {
        Amplitude.getInstance().logEvent(VIEW_SETTINGS);
        io.intercom.android.sdk.Intercom.client().logEvent(VIEW_SETTINGS);
    }

    public static void logBugSend() {
        Amplitude.getInstance().logEvent(Events.PRODUCT_PAGE_BUGSEND);
        Intercom.client().logEvent(Events.PRODUCT_PAGE_BUGSEND);
    }

    public static void logDeleteFood() {
        Amplitude.getInstance().logEvent(DELETE_FOOD);
        io.intercom.android.sdk.Intercom.client().logEvent(DELETE_FOOD);
    }

    public static void logEditFood() {
        Amplitude.getInstance().logEvent(EDIT_FOOD);
        io.intercom.android.sdk.Intercom.client().logEvent(EDIT_FOOD);
    }

    public static void logPushButtonReg(String whichButton) {
        Log.e("LOL", whichButton);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.enter_push_button, whichButton);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(REGISTRATION_NEXT, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.enter_push_button, whichButton);
        io.intercom.android.sdk.Intercom.client().logEvent(REGISTRATION_NEXT, eventData);
    }


    public static void logSuccessOnboarding(String how) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.onboarding_success_from, how);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(ONBOARDING_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.onboarding_success_from, how);
        io.intercom.android.sdk.Intercom.client().logEvent(ONBOARDING_SUCCESS, eventData);
    }

    public static void logPushButton(String whichButton, String from) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.push_button, whichButton);
            eventProperties.put(EventProperties.push_button_from, from);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(PREMIUM_NEXT, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.push_button, whichButton);
        eventData.put(EventProperties.push_button_from, from);
        io.intercom.android.sdk.Intercom.client().logEvent(PREMIUM_NEXT, eventData);
    }

    public static void logOpenChat() {
        Amplitude.getInstance().logEvent(INTERCOM_CHAT);
        io.intercom.android.sdk.Intercom.client().logEvent(INTERCOM_CHAT);
    }


    public static void logBuy(String from, String autoRenewing) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.trial_from, from);
            eventProperties.put(EventProperties.auto_renewal, autoRenewing);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(TRIAL_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.trial_from, from);
        eventData.put(EventProperties.auto_renewal, autoRenewing);
        io.intercom.android.sdk.Intercom.client().logEvent(TRIAL_SUCCESS, eventData);
    }

    public static void logSetBuyError(String message) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("error", message);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent("settings_error", eventProperties);
    }


    public static void logViewFood(String id) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.product_item, id);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(VIEW_PRODUCT_PAGE, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.product_item, id);
        io.intercom.android.sdk.Intercom.client().logEvent(VIEW_PRODUCT_PAGE, eventData);
    }


    public static void logAddFavorite(String id) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.favorites, id);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(PRODUCT_PAGE_FAVORITES, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.favorites, id);
        io.intercom.android.sdk.Intercom.client().logEvent(PRODUCT_PAGE_FAVORITES, eventData);
    }


    public static void logFoodSearch(int count) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.results, count);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(FOOD_SEARCH, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.results, count);
        io.intercom.android.sdk.Intercom.client().logEvent(FOOD_SEARCH, eventData);
    }

    public static void logCreateCustomFood(String from, String name) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.product_from, from);
            eventProperties.put(EventProperties.product_id, name);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(CUSTOM_PRODUCT_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.product_from, from);
        eventData.put(EventProperties.product_id, name);
        io.intercom.android.sdk.Intercom.client().logEvent(CUSTOM_PRODUCT_SUCCESS, eventData);
    }

    public static void logViewArticle(String name) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.articles_item, name);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(VIEW_ARTICLES, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.articles_item, name);
        io.intercom.android.sdk.Intercom.client().logEvent(VIEW_ARTICLES, eventData);
    }

    public static void logCreateRecipe(String from, String name) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_from, from);
            eventProperties.put(EventProperties.recipe_id, name);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(CUSTOM_RECIPE_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_from, from);
        eventData.put(EventProperties.recipe_id, name);
        io.intercom.android.sdk.Intercom.client().logEvent(CUSTOM_RECIPE_SUCCESS, eventData);
    }

    public static void logCreateTemplate(String from, String template_intake, List<Food> foodList) {
        String namesLine = "";
        for (int i = 0; i < foodList.size(); i++) {
            namesLine = namesLine + "  " + foodList.get(i).getName();
        }
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.template_from, from);
            eventProperties.put(EventProperties.template_intake, template_intake);
            eventProperties.put(EventProperties.product_inside, namesLine);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(CUSTOM_TEMPLATE_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.template_from, from);
        eventData.put(EventProperties.template_intake, template_intake);
        eventData.put(EventProperties.product_inside, namesLine);
        io.intercom.android.sdk.Intercom.client().logEvent(CUSTOM_TEMPLATE_SUCCESS, eventData);
    }

    public static void logAddFood(String food_intake, String food_category, String food_date, String food_item, int kcals, int weight) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.food_intake, food_intake);
            eventProperties.put(EventProperties.food_category, food_category);
            eventProperties.put(EventProperties.food_date, food_date);
            eventProperties.put(EventProperties.food_item, food_item);
            eventProperties.put(EventProperties.calorie_value, kcals);
            eventProperties.put(EventProperties.calorie_value, kcals);
            eventProperties.put(EventProperties.product_weight, weight);
        } catch (JSONException exception) {
        }

        Amplitude.getInstance().logEvent(ADD_FOOD_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.food_intake, food_intake);
        eventData.put(EventProperties.food_category, food_category);
        eventData.put(EventProperties.food_date, food_date);
        eventData.put(EventProperties.food_item, food_item);
        eventData.put(EventProperties.calorie_value, kcals);
        eventData.put(EventProperties.product_weight, weight);
        io.intercom.android.sdk.Intercom.client().logEvent(ADD_FOOD_SUCCESS, eventData);
    }




    public static void logViewRecipe(String name) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_item, name);
            eventProperties.put(EventProperties.diet_plans_choose, EventProperties.diet_plans_without);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(VIEW_RECIPE, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_item, name);
        eventData.put(EventProperties.diet_plans_choose, EventProperties.diet_plans_without);
        io.intercom.android.sdk.Intercom.client().logEvent(VIEW_RECIPE, eventData);
    }

    public static void logAddFavoriteRecipe(String name) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.favorites_recipe, name);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(RECIPE_FAVORITES, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.favorites_recipe, name);
        io.intercom.android.sdk.Intercom.client().logEvent(RECIPE_FAVORITES, eventData);
    }



    public static void logAddRecipeInDiary(String eating, String nameRecipe) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_intake, eating);
            eventProperties.put(EventProperties.recipe_id_add, nameRecipe);
            eventProperties.put(EventProperties.diet_plans_choose, EventProperties.diet_plans_without);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(RECIPE_ADD_SUCCES, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_intake, eating);
        eventData.put(EventProperties.recipe_id_add, nameRecipe);
        eventData.put(EventProperties.diet_plans_choose, EventProperties.diet_plans_without);
        io.intercom.android.sdk.Intercom.client().logEvent(RECIPE_ADD_SUCCES, eventData);
    }

    public static void logChoiseRecipeCategory(String categoryName) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.recipe_category, categoryName);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(RECIPE_CATEGORY, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.recipe_category, categoryName);
        io.intercom.android.sdk.Intercom.client().logEvent(RECIPE_CATEGORY, eventData);
    }

    public static void logMoveOnboard(int page) {
        String eventName = "";
        if (page != EventProperties.go_onboard_reg) {
            eventName = EventProperties.go_onboard_prename + String.valueOf(page);
        } else {
            eventName = EventProperties.go_onboard_reg_name;
        }
        Log.e("LOL", eventName);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.go_onboard, eventName);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(ONBOARING_NEXT, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.go_onboard, eventName);
        io.intercom.android.sdk.Intercom.client().logEvent(ONBOARING_NEXT, eventData);
    }

    public static void logSkipOnboard(int page) {
        String name = "";
        name = EventProperties.skip_onboard_prename + String.valueOf(page);
        Log.e("LOL", name);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.skip_onboard, name);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(ONBOARING_SKIP, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.skip_onboard, name);
        io.intercom.android.sdk.Intercom.client().logEvent(ONBOARING_SKIP, eventData);
    }

    public static void logOpenPolitic() {
        Amplitude.getInstance().logEvent(REGISRTATION_PRIVACY);
        io.intercom.android.sdk.Intercom.client().logEvent(REGISRTATION_PRIVACY);
    }

    public static void logRestorePassword() {
        Amplitude.getInstance().logEvent(RESEND_SUCCESS);
        io.intercom.android.sdk.Intercom.client().logEvent(RESEND_SUCCESS);
    }

    public static void logRegistrationError(String error_type) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.error_type, error_type);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(REGISTRATION_ERROR, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.error_type, error_type);
        io.intercom.android.sdk.Intercom.client().logEvent(REGISTRATION_ERROR, eventData);
    }

    public static void logRegistration(String typeRegistration) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.registration, typeRegistration);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(REGISTRATION_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.registration, typeRegistration);
        io.intercom.android.sdk.Intercom.client().logEvent(REGISTRATION_SUCCESS, eventData);
    }

    public static void logEnter(String typeEnter) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.registration, typeEnter);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(ENTER_SUCCESS, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.registration, typeEnter);
        io.intercom.android.sdk.Intercom.client().logEvent(ENTER_SUCCESS, eventData);
    }

    public static void logEnterError() {
        Amplitude.getInstance().logEvent(ENTER_ERROR);
        io.intercom.android.sdk.Intercom.client().logEvent(ENTER_ERROR);
    }

    public static void logMoveQuestions(String page) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(EventProperties.question, page);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent(QUESTION_NEXT, eventProperties);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventProperties.question, page);
        io.intercom.android.sdk.Intercom.client().logEvent(QUESTION_NEXT, eventData);
    }

    public static void logLogout() {
        Amplitude.getInstance().logEvent(PROFILE_LOGOUT);
        io.intercom.android.sdk.Intercom.client().logEvent(PROFILE_LOGOUT);
    }
}
