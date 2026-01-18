package dev.huey.destroyTheCore.items.fragments;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class PlaceholderGen extends ItemGen {
  
  public PlaceholderGen() {
    super(ItemsManager.ItemKey.PLACEHOLDER, Material.GRAY_DYE);
  }
}
