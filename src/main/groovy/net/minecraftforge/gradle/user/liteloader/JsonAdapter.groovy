package net.minecraftforge.gradle.user.liteloader;

import net.minecraftforge.gradle.user.liteloader.LiteModJson.Description;
import com.google.gson.stream.*
import com.google.gson.TypeAdapter


class JsonAdapter extends AbstractJsonAdapter<Description> {

    @Override
    public void write(JsonWriter out, Description desc)
    {
        out.value(desc.toString());
        desc.each { key, value ->
            if (key != Description.BASE && value != null)
            {
                out.name("description.$key").value(value.toString());
            }
        }
    }
}