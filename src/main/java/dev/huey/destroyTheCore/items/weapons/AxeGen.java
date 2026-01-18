package dev.huey.destroyTheCore.items.weapons;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class AxeGen extends ItemGen {
  
  public AxeGen() {
    super(ItemsManager.ItemKey.NETHERITE_AXE, Material.NETHERITE_AXE);
  }
}
