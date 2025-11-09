package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class StarterHelmetGen extends ItemGen {
  public StarterHelmetGen() {
    super(
      ItemsManager.ItemKey.STARTER_HELMET,
      Material.LEATHER_HELMET
    );
    setNeverDrop();
    setTrash();
  }
}
