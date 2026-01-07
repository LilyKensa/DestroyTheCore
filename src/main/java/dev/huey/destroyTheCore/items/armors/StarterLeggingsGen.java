package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class StarterLeggingsGen extends ItemGen {
  public StarterLeggingsGen() {
    super(
      ItemsManager.ItemKey.STARTER_LEGGINGS,
      Material.LEATHER_LEGGINGS
    );
    setNeverDrop();
    setTrash();
  }
}
