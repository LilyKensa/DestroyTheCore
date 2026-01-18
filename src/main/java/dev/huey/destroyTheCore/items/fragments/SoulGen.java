package dev.huey.destroyTheCore.items.fragments;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class SoulGen extends ItemGen {
  
  public SoulGen() {
    super(ItemsManager.ItemKey.SOUL, Material.PRISMARINE_CRYSTALS);
  }
}
