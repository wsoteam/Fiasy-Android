
package com.wsoteam.diet.common.networking.food.POJO;

import com.wsoteam.diet.common.networking.food.ISearchResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodResult  {

    private Integer count;
    private String next;
    private Object previous;
    private List<Result> results = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "FoodResult{" +
                "count=" + count +
                ", next='" + next + '\'' +
                ", previous=" + previous +
                ", results=" + results +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
