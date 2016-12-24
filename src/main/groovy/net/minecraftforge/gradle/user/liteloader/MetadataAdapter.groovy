package net.minecraftforge.gradle.user.liteloader

import com.google.gson.stream.JsonWriter
import net.minecraftforge.gradle.user.liteloader.LiteModJson.Metadata

class MetadataAdapter extends AbstractJsonAdapter<Metadata> {

    @Override
    void write(JsonWriter out, Metadata desc)
    {
        out.nullValue()
        desc.each { key, value ->
            if (key && value)
            {
                out.name(key.toString()).value(value.toString())
            }
        }
    }
}
