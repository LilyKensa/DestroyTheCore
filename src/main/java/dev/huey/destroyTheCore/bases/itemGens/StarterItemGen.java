package dev.huey.destroyTheCore.bases.itemGens;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class StarterItemGen extends ItemGen {
  
  public StarterItemGen(ItemsManager.ItemKey id, Material iconType) {
    super(id, iconType);
    setNeverDrop();
    setTrash();
  }
  
  public int getLevelsBasedOnPhase() {
    if (!DTC.game.isPlaying) return 0;
    
    int level = 0;
    
    switch (DTC.game.phase) {
      case CoreProtected -> level = 0;
      case ShopOpened, MissionsStarted -> level = 1;
      case DeathPenalty -> level = 2;
      case DoubleDamage -> level = 3;
      case CoreWilting -> level = 4;
    }
    
    return level;
  }
}
