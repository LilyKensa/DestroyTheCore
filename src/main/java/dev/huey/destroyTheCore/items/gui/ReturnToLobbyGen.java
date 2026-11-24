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

public class ReturnToLobbyGen extends UsableItemGen {
  
  public ReturnToLobbyGen() {
    super(ItemsManager.ItemKey.RETURN_TO_LOBBY, Material.STRUCTURE_VOID);
    setBound();
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (LocUtils.inLobby(pl)) {
      PlayerUtils.send(pl, TextUtils.$("items.return-to-lobby.in-lobby"));
      return;
    }
    
    PlayerData data = DTC.game.getPlayerData(pl);
    data.previewing = false;
    
    PlayerUtils.refreshSpectatorAbilities(pl);
    PlayerUtils.backToLobby(pl);
    
    pl.setCooldown(
      DTC.itemsManager.gens.get(
        ItemsManager.ItemKey.PREVIEW_MAP_ENTRY
      ).iconType,
      5 * 20
    );
    
    if (PlayerUtils.shouldHandle(pl)) {
      pl.getInventory().clear();
      PlayerUtils.giveLobbyItems(pl);
    }
  }
}
