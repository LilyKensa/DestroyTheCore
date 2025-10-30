package dev.huey.destroyTheCore.items.gui;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorTeleporterGen extends UsableItemGen {
  public SpectatorTeleporterGen() {
    super(
      ItemsManager.ItemKey.SPECTATOR_TELEPORTER,
      Material.ENDER_EYE
    );
    setBound();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.setEnchantmentGlintOverride(false);
  }
  
  @Override
  public void use(Player pl, Block block) {
    DestroyTheCore.guiManager.openTeleporter(pl);
  }
}
