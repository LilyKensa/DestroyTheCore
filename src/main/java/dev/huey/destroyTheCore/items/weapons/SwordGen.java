package dev.huey.destroyTheCore.items.weapons;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class SwordGen extends ItemGen {
  public SwordGen() {
    super(
      ItemsManager.ItemKey.NETHERITE_SWORD,
      Material.NETHERITE_SWORD
    );
  }
}
