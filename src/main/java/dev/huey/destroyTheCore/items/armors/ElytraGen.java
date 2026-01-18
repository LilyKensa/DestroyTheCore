package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class ElytraGen extends ItemGen {
  
  public ElytraGen() {
    super(ItemsManager.ItemKey.ELYTRA, Material.ELYTRA);
  }
}
