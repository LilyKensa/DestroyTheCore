package dev.huey.destroyTheCore;

import org.bukkit.Material;
import org.bukkit.entity.Villager;

import java.util.List;
import java.util.Map;

public class Constants {
  
  public record OreData(
                        Material dropType,
                        long cooldownSeconds,
                        int minXp,
                        int maxXp
  ) {
  }
  
  public static final Map<Material, OreData> ores = Map.ofEntries(
    Map.entry(Material.COAL_ORE, new OreData(Material.COAL, 5, 5, 10)),
    Map.entry(Material.IRON_ORE, new OreData(Material.RAW_IRON, 7, 0, 0)),
    Map.entry(Material.GOLD_ORE, new OreData(Material.RAW_GOLD, 8, 0, 0)),
    Map.entry(Material.REDSTONE_ORE, new OreData(Material.REDSTONE, 5, 2, 6)),
    Map.entry(Material.LAPIS_ORE, new OreData(Material.LAPIS_LAZULI, 10, 2, 6)),
    Map.entry(Material.EMERALD_ORE, new OreData(Material.EMERALD, 13, 3, 8)),
    Map.entry(Material.DIAMOND_ORE, new OreData(Material.DIAMOND, 60, 3, 12))
  );
  
  /** To be dropped when needed */
  public static final List<Material> oreItems = List.of(
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
  
  public static final Map<Villager.Type, Material> villagerIcons = Map.ofEntries(
    Map.entry(Villager.Type.PLAINS, Material.GRASS_BLOCK),
    Map.entry(Villager.Type.DESERT, Material.CHISELED_SANDSTONE),
    Map.entry(Villager.Type.TAIGA, Material.PODZOL),
    Map.entry(Villager.Type.SAVANNA, Material.ACACIA_LOG),
    Map.entry(Villager.Type.JUNGLE, Material.JUNGLE_LEAVES),
    Map.entry(Villager.Type.SNOW, Material.SNOW_BLOCK),
    Map.entry(Villager.Type.SWAMP, Material.MANGROVE_ROOTS)
  );
  
  public static final Map<Villager.Profession, Material> villagerJobSites = Map.ofEntries(
    Map.entry(Villager.Profession.ARMORER, Material.BLAST_FURNACE),
    Map.entry(Villager.Profession.BUTCHER, Material.SMOKER),
    Map.entry(Villager.Profession.CARTOGRAPHER, Material.CARTOGRAPHY_TABLE),
    Map.entry(Villager.Profession.CLERIC, Material.BREWING_STAND),
    Map.entry(Villager.Profession.FARMER, Material.COMPOSTER),
    Map.entry(Villager.Profession.FISHERMAN, Material.BARREL),
    Map.entry(Villager.Profession.FLETCHER, Material.FLETCHING_TABLE),
    Map.entry(Villager.Profession.LEATHERWORKER, Material.CAULDRON),
    Map.entry(Villager.Profession.LIBRARIAN, Material.LECTERN),
    Map.entry(Villager.Profession.MASON, Material.STONECUTTER),
    Map.entry(Villager.Profession.SHEPHERD, Material.LOOM),
    Map.entry(Villager.Profession.TOOLSMITH, Material.SMITHING_TABLE),
    Map.entry(Villager.Profession.WEAPONSMITH, Material.GRINDSTONE),
    Map.entry(Villager.Profession.NITWIT, Material.RED_BED),
    Map.entry(Villager.Profession.NONE, Material.GRASS_BLOCK)
  );
}
