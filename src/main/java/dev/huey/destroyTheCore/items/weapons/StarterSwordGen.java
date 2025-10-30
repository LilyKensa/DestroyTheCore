package dev.huey.destroyTheCore.items.weapons;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class StarterSwordGen extends ItemGen {
  public StarterSwordGen() {
    super(
      ItemsManager.ItemKey.STARTER_SWORD,
      Material.WOODEN_SWORD
    );
    setNeverDrop();
    setNotImportant();
  }
}
