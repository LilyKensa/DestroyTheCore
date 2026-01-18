package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class FreeSoupMission extends InstantMission {
  
  public FreeSoupMission() {
    super("free-soup");
  }
  
  List<SuspiciousEffectEntry> effects = List.of(
    // Regeneration (Oxeye Daisy) - 8s
    SuspiciousEffectEntry.create(PotionEffectType.REGENERATION, 160),
    // Jump Boost (Cornflower) - 6s
    SuspiciousEffectEntry.create(PotionEffectType.JUMP_BOOST, 120),
    // Poison (Lily of the Valley) - 12s
    SuspiciousEffectEntry.create(PotionEffectType.POISON, 240),
    // Wither (Wither Rose) - 8s
    SuspiciousEffectEntry.create(PotionEffectType.WITHER, 160),
    // Weakness (Tulip) - 9s
    SuspiciousEffectEntry.create(PotionEffectType.WEAKNESS, 180),
    // Blindness (Azure Bluet) - 8s
    SuspiciousEffectEntry.create(PotionEffectType.BLINDNESS, 160),
    // Fire Resistance (Allium) - 4s
    SuspiciousEffectEntry.create(PotionEffectType.FIRE_RESISTANCE, 80),
    // Night Vision (Poppy/Torchflower) - 5s
    SuspiciousEffectEntry.create(PotionEffectType.NIGHT_VISION, 100),
    // Saturation (Dandelion/Blue Orchid) - 0.35s (7 ticks)
    SuspiciousEffectEntry.create(PotionEffectType.SATURATION, 7)
  );
  
  @Override
  public void run() {
    for (Player p : PlayerUtils.allGaming()) {
      ItemStack item = new ItemStack(Material.SUSPICIOUS_STEW);
      
      item.editMeta(uncastedMeta -> {
        SuspiciousStewMeta meta = (SuspiciousStewMeta) uncastedMeta;
        
        meta.addCustomEffect(RandomUtils.pick(effects), true);
      });
      
      p.give(item);
    }
  }
}
