package dev.huey.destroyTheCore.records;

import dev.huey.destroyTheCore.utils.LocUtils;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

public class Region implements ConfigurationSerializable {
  Pos first, second, min, max;
  
  public Region(Pos first, Pos second) {
    this.first = first;
    this.second = second;
    min = LocUtils.min(first, second).floor();
    max = LocUtils.max(first, second).add(1, 1, 1).floor();
  }
  
  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.ofEntries(
      Map.entry("first", first),
      Map.entry("second", second)
    );
  }
  
  static public Region deserialize(Map<String, Object> map) {
    return new Region((Pos) map.get("first"), (Pos) map.get("second"));
  }
  
  public boolean contains(Pos loc) {
    return (loc.getX() >= min.getX()
      && loc.getY() >= min.getY()
      && loc.getZ() >= min.getZ()
      && loc.getX() < max.getX()
      && loc.getY() < max.getY()
      && loc.getZ() < max.getZ());
  }
  
  public void forEachBlock(World world, Consumer<Block> consumer) {
    for (int x = (int) min.getX(); x < max.getX(); ++x) {
      for (int y = (int) min.getY(); y < max.getY(); ++y) {
        for (int z = (int) min.getZ(); z < max.getZ(); ++z) {
          consumer.accept(world.getBlockAt(x, y, z));
        }
      }
    }
  }
  
  public Pos getFirst() {
    return first;
  }
  
  public Pos getSecond() {
    return second;
  }
}
