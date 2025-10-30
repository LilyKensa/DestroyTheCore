package dev.huey.destroyTheCore;

import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class Constants {
  public record OreData(
    Material dropType,
    long cooldownSeconds,
    int minXp,
    int maxXp
  ) { }
  
  static public final Map<Material, OreData> ores = Map.ofEntries(
    Map.entry(Material.COAL_ORE,     new OreData(Material.COAL,          5, 5, 10)),
    Map.entry(Material.IRON_ORE,     new OreData(Material.RAW_IRON,      7, 0, 0)),
    Map.entry(Material.GOLD_ORE,     new OreData(Material.RAW_GOLD,      8, 0, 0)),
    Map.entry(Material.REDSTONE_ORE, new OreData(Material.REDSTONE,      5, 2, 6)),
    Map.entry(Material.LAPIS_ORE,    new OreData(Material.LAPIS_LAZULI, 10, 2, 6)),
    Map.entry(Material.EMERALD_ORE,  new OreData(Material.EMERALD,      13, 3, 8)),
    Map.entry(Material.DIAMOND_ORE,  new OreData(Material.DIAMOND,      60, 3, 12))
  );
  
  static public final List<Material> oreItems = List.of(
    Material.COAL,
    Material.LAPIS_LAZULI,
    Material.DIAMOND,
    Material.EMERALD,
    Material.REDSTONE,
    Material.IRON_INGOT,
    Material.GOLD_INGOT,
    
    Material.RAW_IRON,
    Material.RAW_GOLD
  );
}
