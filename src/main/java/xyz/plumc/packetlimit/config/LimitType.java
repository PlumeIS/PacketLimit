package xyz.plumc.packetlimit.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

public class LimitType {

    public String name;
    public String message;
    public float waringWaiting;
    public float maxRate;
    public float scale;
    public float sensitive;
    public float liveTime;
    public int kickLimit;
    public int banLimit;
    public int minLimit;

    public LimitType(Map<String, ?> config){
        this.name = (String) config.get("name");
        this.message = (String) config.get("message");
        this.waringWaiting = (float)config.get("waringWaiting")*1000;
        this.maxRate = (float) config.get("maxRate");
        this.scale = (float) config.get("scale");
        this.sensitive = (float)config.get("sensitive")*1000;
        this.liveTime = (float)config.get("liveTime")*1000;
        this.kickLimit = (int)config.get("kickLimit");
        this.banLimit = config.get("banLimit") == null ? Integer.MAX_VALUE : (int)config.get("banLimit");
        this.minLimit = (int)config.get("minLimit");
    }

    public JsonObject toJson(){
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.add("name", gson.toJsonTree(this.name));
        json.add("message", gson.toJsonTree(this.message));
        json.add("waringWaiting", gson.toJsonTree(this.waringWaiting/1000));
        json.add("maxRate", gson.toJsonTree(this.maxRate));
        json.add("scale", gson.toJsonTree(this.scale));
        json.add("sensitive", gson.toJsonTree(this.sensitive/1000));
        json.add("liveTime", gson.toJsonTree(this.liveTime/1000));
        json.add("kickLimit", gson.toJsonTree(this.kickLimit));
        json.add("banLimit", gson.toJsonTree(this.banLimit==Integer.MAX_VALUE ? null : this.banLimit));
        return json;
    }
    public LimitType(String name, String message, float waringWaiting, float maxRate, float scale, float sensitive, float liveTime, int kickLimit, int banLimit){
        this.name = name;
        this.message = message;
        this.waringWaiting = (int) (waringWaiting*1000);
        this.maxRate = maxRate;
        this.scale = scale;
        this.sensitive = sensitive*1000;
        this.liveTime = liveTime*1000;
        this.kickLimit = kickLimit;
        this.banLimit = banLimit;
    }
}
