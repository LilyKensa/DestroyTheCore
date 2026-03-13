package dev.huey.destroyTheCore.items.gui;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class ChooseRoleGen extends UsableItemGen {
  
  public ChooseRoleGen() {
    super(ItemsManager.ItemKey.CHOOSE_ROLE, Material.ENDER_CHEST);
    setBound();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.setEnchantmentGlintOverride(false);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (
      DestroyTheCore.game.getPlayerData(pl).side.equals(Game.Side.SPECTATOR)
    ) {
      pl.sendActionBar(TextUtils.$("items.choose-role.no-team"));
      return;
    }
    
    DestroyTheCore.guiManager.openRoleSelection(pl);
  }
}
