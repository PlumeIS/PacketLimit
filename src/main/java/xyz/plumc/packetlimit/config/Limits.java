package xyz.plumc.packetlimit.config;

import java.util.*;

public class Limits {
    public static Limits INSTANCE;
    private Map<String, LimitType> limits = new HashMap<>();
    public Limits(){
        INSTANCE = this;
    }
    public LimitType getLimit(String name){
        return limits.get(name);
    }
    public Map<String, LimitType> getLimits(){
        return limits;
    }
    public List<String> getLimitNames(){
        return new ArrayList<>(limits.keySet());
    }
    public void setLimit(String name, LimitType limit){
        limits.put(name, limit);
    }
    public void setLimits(Map<String, LimitType> limits){
        this.limits = limits;
    }
}
