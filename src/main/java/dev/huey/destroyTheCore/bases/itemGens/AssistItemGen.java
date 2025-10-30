package dev.huey.destroyTheCore.bases.itemGens;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AssistItemGen extends ItemGen {
  public AssistItemGen(ItemsManager.ItemKey id, Material iconType) {
    super(id, iconType);
  }
  
  public void onEquippingTick(Player pl) {
  
  }
  
  public void onAttack(Player victim, Player attacker) {
  
  }
}
