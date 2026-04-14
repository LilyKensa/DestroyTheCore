package dev.huey.destroyTheCore;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Villager;

public class Constants {
  
  public record OreData(
    Material blockType,
    Material dropType,
    NamedTextColor textColor,
    int cooldownSeconds,
    int minXp,
    int maxXp
  ) {
  }
  
  static public final Map<Material, OreData> ores = Map.ofEntries(
    Map.entry(
      Material.COAL_ORE,
      new OreData(
        Material.COAL_ORE,
        Material.COAL,
        NamedTextColor.GRAY,
        5,
        3,
        9
      )
    ),
    Map.entry(
      Material.IRON_ORE,
      new OreData(
        Material.IRON_ORE,
        Material.RAW_IRON,
        NamedTextColor.WHITE,
        7,
        0,
        0
      )
    ),
    Map.entry(
      Material.GOLD_ORE,
      new OreData(
        Material.GOLD_ORE,
        Material.RAW_GOLD,
        NamedTextColor.YELLOW,
        8,
        0,
        0
      )
    ),
    Map.entry(
      Material.REDSTONE_ORE,
      new OreData(
        Material.REDSTONE_ORE,
        Material.REDSTONE,
        NamedTextColor.RED,
        5,
        1,
        5
      )
    ),
    Map.entry(
      Material.LAPIS_ORE,
      new OreData(
        Material.LAPIS_ORE,
        Material.LAPIS_LAZULI,
        NamedTextColor.BLUE,
        10,
        1,
        5
      )
    ),
    Map.entry(
      Material.EMERALD_ORE,
      new OreData(
        Material.EMERALD_ORE,
        Material.EMERALD,
        NamedTextColor.GREEN,
        13,
        2,
        6
      )
    ),
    Map.entry(
      Material.DIAMOND_ORE,
      new OreData(
        Material.DIAMOND_ORE,
        Material.DIAMOND,
        NamedTextColor.AQUA,
        90,
        4,
        8
      )
    )
  );
  
  /** To be dropped when needed */
  static public final List<Material> oreItems = List.of(
    Material.COAL,
    Material.LAPIS_LAZULI,
    Material.DIAMOND,
    Material.EMERALD,
    Material.REDSTONE,
    Material.IRON_INGOT,
    Material.GOLD_INGOT,
    Material.RAW_IRON,
    Material.RAW_GOLD,
    Material.IRON_NUGGET,
    Material.GOLD_NUGGET,
    Material.COAL_BLOCK,
    Material.LAPIS_BLOCK,
    Material.DIAMOND_BLOCK,
    Material.EMERALD_BLOCK,
    Material.REDSTONE_BLOCK,
    Material.IRON_BLOCK,
    Material.GOLD_BLOCK,
    Material.RAW_IRON_BLOCK,
    Material.RAW_GOLD_BLOCK,
    Material.WOODEN_HOE // Easter Egg :)
  );
  
  static public final Map<Villager.Type, Material> villagerIcons = Map
    .ofEntries(
      Map.entry(Villager.Type.PLAINS, Material.GRASS_BLOCK),
      Map.entry(Villager.Type.DESERT, Material.CHISELED_SANDSTONE),
      Map.entry(Villager.Type.TAIGA, Material.PODZOL),
      Map.entry(Villager.Type.SAVANNA, Material.ACACIA_LOG),
      Map.entry(Villager.Type.JUNGLE, Material.JUNGLE_LEAVES),
      Map.entry(Villager.Type.SNOW, Material.SNOW_BLOCK),
      Map.entry(Villager.Type.SWAMP, Material.MANGROVE_ROOTS)
    );
  
  static public final Map<Villager.Profession, Material> villagerJobSites = Map
    .ofEntries(
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
