package net.minecraftforge.gradle.user.liteloader

import com.google.gson.stream.JsonWriter
import net.minecraftforge.gradle.user.liteloader.LiteModJson.Description

class DescriptionAdapter extends AbstractJsonAdapter<Description> {

    @Override
    void write(JsonWriter out, Description desc)
    {
        out.value((String) desc.toString())
        desc.each { key, value ->
            if (key != Description.BASE && value)
            {
                out.name("description.$key").value(value.toString())
            }
        }
    }
}
