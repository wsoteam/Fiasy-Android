package com.wsoteam.diet.DietPlans.POJO;

import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class DietPlansHolder {

    private static DietModule dietModule;
    private static Set<Observer> observersList = new LinkedHashSet<>();

    public static void bind(DietModule dietModule){
        DietPlansHolder.dietModule = dietModule;
        update();
    }

    public static DietModule get(){
        return dietModule;
    }

    public static void subscribe(Observer observer) {
        observersList.add(observer);
    }

    public static void unsubscribe(Observer observer) {
        observersList.remove(observer);
    }

    private static void update() {
        for (Observer observer :
                observersList) {
            observer.update(new Observable(), null);
        }
    }
}
