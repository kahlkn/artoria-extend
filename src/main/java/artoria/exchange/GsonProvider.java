package artoria.exchange;

import artoria.util.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Json provider simple implement by gson.
 * @author Kahle
 */
public class GsonProvider implements JsonProvider {
    private boolean prettyFormat;
    private Gson gson;

    public GsonProvider() {

        this(false);
    }

    public GsonProvider(Gson gson) {
        Assert.notNull(gson, "Parameter \"gson\" must not null. ");
        this.prettyFormat = false;
        this.gson = gson;
    }

    public GsonProvider(boolean prettyFormat) {
        this.prettyFormat = prettyFormat;
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (prettyFormat) { gsonBuilder.setPrettyPrinting(); }
        this.gson = gsonBuilder.create();
    }

    @Override
    public boolean getPrettyFormat() {

        return prettyFormat;
    }

    @Override
    public String toJsonString(Object object) {

        return gson.toJson(object);
    }

    @Override
    public <T> T parseObject(String jsonString, Type type) {

        return gson.fromJson(jsonString, type);
    }

}
