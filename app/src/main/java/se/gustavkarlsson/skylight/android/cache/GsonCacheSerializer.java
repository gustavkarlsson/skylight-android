package se.gustavkarlsson.skylight.android.cache;

import com.google.gson.Gson;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;

class GsonCacheSerializer<T> implements CacheSerializer<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;

    GsonCacheSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T fromString(String json) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public String toString(T report) {
        return gson.toJson(report);
    }
}
