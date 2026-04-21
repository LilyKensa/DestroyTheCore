package dev.huey.destroyTheCore.items.gui;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class RoleSelectorGen extends UsableItemGen {
  
  public RoleSelectorGen() {
    super(ItemsManager.ItemKey.ROLE_SELECTOR, Material.ENDER_CHEST);
    setBound();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.setEnchantmentGlintOverride(false);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (
      DTC.game.getPlayerData(pl).side.equals(Game.Side.SPECTATOR)
    ) {
      pl.sendActionBar(TextUtils.$("items.role-selector.no-team"));
      return;
    }
    
    DTC.guiManager.openRoleSelection(pl);
  }
}
