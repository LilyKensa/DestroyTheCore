package dev.huey.destroyTheCore.records;

import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public record Region(Location loc1,
                     Location loc2) implements ConfigurationSerializable {
  public boolean contains(Location loc) {
    Location min = LocationUtils.min(loc1, loc2), max = LocationUtils.max(loc1,
      loc2).add(1, 1, 1);
    
    return (loc.getX() >= min.getBlockX() && loc.getY() >= min.getBlockY() && loc.getZ() >= min.getBlockZ() && loc.getX() < max.getBlockX() && loc.getY() < max.getBlockY() && loc.getZ() < max.getBlockZ());
  }
  
  public void forEachBlock(Consumer<Block> consumer) {
    Location min = LocationUtils.min(loc1, loc2), max = LocationUtils.max(loc1,
      loc2).add(1, 1, 1);
    
    for (int x = min.getBlockX(); x < max.getBlockX(); ++x) for (
                                                                 int y = min.getBlockY(); y < max.getBlockY(); ++y
    ) for (int z = min.getBlockZ(); z < max.getBlockZ(); ++z) {
      consumer.accept(min.getWorld().getBlockAt(x, y, z));
    }
  }
  
  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.ofEntries(Map.entry("loc1", loc1), Map.entry("loc2", loc2));
  }
  
  public static Region deserialize(Map<String, Object> map) {
    return new Region((Location) map.get("loc1"), (Location) map.get("loc2"));
  }
}
