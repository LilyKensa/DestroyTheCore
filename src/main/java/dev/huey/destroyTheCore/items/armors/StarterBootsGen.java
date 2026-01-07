package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class StarterBootsGen extends ItemGen {
  public StarterBootsGen() {
    super(
      ItemsManager.ItemKey.STARTER_BOOTS,
      Material.LEATHER_BOOTS
    );
    setNeverDrop();
    setTrash();
  }
}
