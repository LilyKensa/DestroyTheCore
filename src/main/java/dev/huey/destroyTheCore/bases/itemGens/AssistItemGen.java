package dev.huey.destroyTheCore.bases.itemGens;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AssistItemGen extends ItemGen {
  public AssistItemGen(ItemsManager.ItemKey id, Material iconType) {
    super(id, iconType);
  }
  
  /** @implNote Optional - Called every tick when a player has this item in off-hand */
  public void onEquippingTick(Player pl) {
  
  }
  
  /** @implNote Optional - Called when a player is attacked while this item is in off-hand */
  public void onAttack(Player victim, Player attacker) {
  
  }
}
