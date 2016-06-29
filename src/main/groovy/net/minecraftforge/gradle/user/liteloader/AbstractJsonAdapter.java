package net.minecraftforge.gradle.user.liteloader;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

// groovy was being a butt
abstract class AbstractJsonAdapter<T> extends TypeAdapter<T>
{
    // groovy complained. Probably because generic.
    @Override
    public T read(JsonReader in) throws IOException
    {
        return null;
    }
}
