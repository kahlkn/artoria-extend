package artoria.exchange;

import artoria.util.ArrayUtils;
import artoria.util.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import static artoria.exchange.JsonFormat.PRETTY_FORMAT;

/**
 * Json provider simple implement by gson.
 * @author Kahle
 */
public class GsonProvider implements JsonProvider {
    private Gson prettyFormatGson;
    private Gson gson;

    public GsonProvider() {

        this(new Gson());
    }

    public GsonProvider(Gson gson) {
        Assert.notNull(gson, "Parameter \"gson\" must not null. ");
        this.gson = gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        this.prettyFormatGson = gsonBuilder.create();
    }

    protected Gson getGson(JsonFeature... features) {
        if (ArrayUtils.isEmpty(features)) { return gson; }
        for (JsonFeature jsonFeature : features) {
            if (jsonFeature == null) { continue; }
            if (jsonFeature instanceof JsonFormat
                    && PRETTY_FORMAT.equals(jsonFeature)) {
                return prettyFormatGson;
            }
        }
        return gson;
    }

    @Override
    public String toJsonString(Object object, JsonFeature... features) {

        return getGson(features).toJson(object);
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, JsonFeature... features) {

        return getGson(features).fromJson(jsonString, type);
    }

}
