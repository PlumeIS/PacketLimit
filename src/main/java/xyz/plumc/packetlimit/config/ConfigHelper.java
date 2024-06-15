package xyz.plumc.packetlimit.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;
import xyz.plumc.packetlimit.Limiter;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigHelper {
    public static final File CONFIG_FILE = new File("config/packetlimit.json");
    public static final File LIMITS_FILE = new File("config/limits.json");
    public static JsonObject readDefaultLimits() throws IOException {
        URL url = ConfigHelper.class.getClassLoader().getResource("limits.json");
        JsonElement element = JsonParser.parseReader(new InputStreamReader(url.openStream()));
        return element.getAsJsonObject();
    }

    public static JsonObject readDefaultConfig() throws IOException {
        URL url = ConfigHelper.class.getClassLoader().getResource("packetlimit.json");
        JsonElement element = JsonParser.parseReader(new InputStreamReader(url.openStream()));
        return element.getAsJsonObject();
    }
    public static void load(){
        loadLimits();
        loadConfig();
    }

    public static void save(){
        saveLimits(null);
        saveConfig(null);
    }

    private static void loadLimits(){
        if (Objects.isNull(Limits.INSTANCE)){
            new Limits();
        }

        if (!LIMITS_FILE.exists()){
            try {
                LIMITS_FILE.createNewFile();
                LIMITS_FILE.setWritable(true);
                URL url = ConfigHelper.class.getClassLoader().getResource("limits.json");
                OutputStream out = new FileOutputStream(LIMITS_FILE);
                try (InputStream in = url.openStream()) {
                    byte[] buffer = new byte[in.available()];
                    in.read(buffer);
                    out.write(buffer);
                }
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JsonObject defaultConfig = null;
        boolean rewriting = false;

        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(LIMITS_FILE)).getAsJsonObject();
            Gson gson = new Gson();

            for (Limiter.AcceptLimitType acceptLimit: Limiter.AcceptLimitType.values()){
                String accept = acceptLimit.name;
                JsonObject limit = jsonObject.getAsJsonObject(accept);
                if (Objects.isNull(limit)){
                    if (Objects.isNull(defaultConfig)){
                        defaultConfig = readDefaultLimits();
                    }
                    jsonObject.add(accept, gson.toJsonTree(defaultConfig.get(accept)));
                    limit = defaultConfig.getAsJsonObject(accept);
                    rewriting = true;
                }

                Map<String, Object> map = new HashMap<>();
                for (Field field : LimitType.class.getFields()){
                    Object value = gson.fromJson(limit.get(field.getName()), field.getType());
                    if (Objects.isNull(value)){
                        if (Objects.isNull(defaultConfig)){
                            defaultConfig = readDefaultLimits();
                        }

                        value = gson.fromJson(defaultConfig.getAsJsonObject(accept).get(field.getName()), field.getType());
                        limit.add(field.getName(), gson.toJsonTree(value, field.getType()));
                        jsonObject.add(accept, limit);
                        rewriting = true;
                    }
                    map.put(field.getName(), value);
                }
                Limits.INSTANCE.setLimit(accept, new LimitType(map));
            }
            if (rewriting){
                saveLimits(jsonObject);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadConfig(){
        if (!CONFIG_FILE.exists()){
            try {
                CONFIG_FILE.createNewFile();
                CONFIG_FILE.setWritable(true);
                URL url = ConfigHelper.class.getClassLoader().getResource("packetlimit.json");
                OutputStream out = new FileOutputStream(CONFIG_FILE);
                try (InputStream in = url.openStream()) {
                    byte[] buffer = new byte[in.available()];
                    in.read(buffer);
                    out.write(buffer);
                }
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JsonObject defaultConfig = null;
        boolean rewriting = false;

        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_FILE)).getAsJsonObject();
            Gson gson = new Gson();
            for (Field field : PacketLimitConfig.class.getFields()){
                Object value = gson.fromJson(jsonObject.get(field.getName()), field.getType());
                if (Objects.isNull(value)){
                    if (Objects.isNull(defaultConfig)){
                        defaultConfig = readDefaultConfig();
                    }
                    value = gson.fromJson(defaultConfig.get(field.getName()), field.getType());
                    jsonObject.add(field.getName(), gson.toJsonTree(value, field.getType()));
                    rewriting = true;
                }
                field.set(PacketLimitConfig.class, value);
            }
            if (rewriting){
                saveLimits(jsonObject);
            }
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveLimits(@Nullable JsonObject limits){
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(LIMITS_FILE));
            writer.setIndent("  ");
            JsonObject limitsJson = new JsonObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (Objects.isNull(limits)){
                for (Map.Entry<String, LimitType> limit : Limits.INSTANCE.getLimits().entrySet()){
                    JsonObject limitJson = limit.getValue().toJson();
                    limitsJson.add(limit.getKey(), limitJson);
                }
            } else limitsJson = limits;
            gson.toJson(limitsJson, JsonObject.class, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig(@Nullable JsonObject config){
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(CONFIG_FILE));
            writer.setIndent("  ");
            JsonObject jsonObject = new JsonObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (Objects.isNull(config)){
                for (Field field : PacketLimitConfig.class.getFields()){
                    jsonObject.add(field.getName(), gson.toJsonTree(field.get(PacketLimitConfig.class)));
                }
            } else jsonObject = config;
            gson.toJson(jsonObject, JsonObject.class, writer);
            writer.close();
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
