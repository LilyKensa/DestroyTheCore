package dev.huey.destroyTheCore.items.gui;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class PreviewMapEntryGen extends UsableItemGen {
  
  public PreviewMapEntryGen() {
    super(ItemsManager.ItemKey.PREVIEW_MAP_ENTRY, Material.ENDER_EYE);
    setBound();
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!LocUtils.inLobby(pl)) {
      PlayerUtils.send(pl, TextUtils.$("items.preview-map-entry.only-lobby"));
      return;
    }
    
    PlayerData data = DTC.game.getPlayerData(pl);
    data.previewing = true;
    
    PlayerUtils.refreshSpectatorAbilities(pl);
    PlayerUtils.teleportToRestAreaCenter(pl);
    
    pl.setCooldown(
      DTC.itemsManager.gens.get(ItemsManager.ItemKey.RETURN_TO_LOBBY).iconType,
      5 * 20
    );
    
    pl.setFlying(true);
    
    PlayerInventory inv = pl.getInventory();
    if (PlayerUtils.shouldHandle(pl)) {
      inv.clear();
      inv.setItem(
        4,
        DTC.itemsManager.gens.get(ItemsManager.ItemKey.RETURN_TO_LOBBY)
          .getItem()
      );
      inv.setHeldItemSlot(4);
    }
  }
}
