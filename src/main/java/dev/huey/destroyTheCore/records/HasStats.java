package dev.huey.destroyTheCore.records;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;

public interface HasStats {
  int kills = 0, deaths = 0;
  int coreAttacks = 0;
  int skills = 0;
  Map<Material, Integer> ores = new HashMap<>();
}
