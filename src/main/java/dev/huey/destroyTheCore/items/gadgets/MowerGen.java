package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

public class MowerGen extends UsableItemGen {
  
  public MowerGen() {
    super(ItemsManager.ItemKey.MOWER, Material.TRIDENT);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.setUnbreakable(false);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkHandCooldown(pl)) return;
    PlayerUtils.setHandCooldown(pl, 30);
    
    pl.damageItemStack(EquipmentSlot.HAND, 10);
    
    Location loc = block != null ? block.getLocation().toCenterLocation()
      : pl.getLocation();
    
    loc.getWorld().playSound(
      loc,
      Sound.BLOCK_BEACON_DEACTIVATE,
      1,
      0.89f
    );
    
    Pos origin = Pos.of(loc);
    for (int dx = -6; dx <= 6; dx++) {
      for (int dy = -6; dy <= 6; dy++) {
        for (int dz = -6; dz <= 6; dz++) {
          Pos pos = origin.add(dx, dy, dz);
          if (!LocUtils.near(pos, origin, 5)) continue;
          
          Block b = pos.toLoc(loc.getWorld()).getBlock();
          if (
            !b.getType().isAir() &&
              b.isPassable() &&
              !b.isLiquid() &&
              !Tag.CROPS.isTagged(b.getType()) &&
              !Tag.SAPLINGS.isTagged(b.getType()) &&
              b.getType() != Material.SWEET_BERRY_BUSH &&
              b.getType() != Material.NETHER_WART
          ) {
            CoreUtils.breakBlock(b, false);
          }
        }
      }
    }
  }
}
