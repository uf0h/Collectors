package me.ufo.collectors.adapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.ufo.collectors.CollectorsPlugin;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationTypeAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter writer, Location location) throws IOException {
        if (location != null && location.getWorld() != null) {
            writer.beginArray();
            writer.value(location.getWorld().getName());
            writer.value(location.getX());
            writer.value(location.getY());
            writer.value(location.getZ());
            writer.value(location.getYaw());
            writer.value(location.getPitch());
            writer.endArray();
            return;
        }

        writer.nullValue();
    }

    @Override
    public Location read(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL) {
            reader.beginArray();
            final World world = CollectorsPlugin.getInstance().getServer().getWorld(reader.nextString());
            final double x = reader.nextDouble(), y = reader.nextDouble(), z = reader.nextDouble();
            final float yaw = (float) reader.nextDouble(), pitch = (float) reader.nextDouble();
            reader.endArray();
            return new Location(world, x, y, z, yaw, pitch);
        }

        reader.nextNull();
        return null;
    }

}