package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class StarterChestplateGen extends ItemGen {
  public StarterChestplateGen() {
    super(
      ItemsManager.ItemKey.STARTER_CHESTPLATE,
      Material.LEATHER_CHESTPLATE
    );
    setNeverDrop();
    setNotImportant();
  }
}
