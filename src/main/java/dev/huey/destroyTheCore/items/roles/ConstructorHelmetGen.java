package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class ConstructorHelmetGen extends ItemGen {
  public ConstructorHelmetGen() {
    super(
      ItemsManager.ItemKey.CONSTRUCTOR_HELMET,
      Material.TURTLE_HELMET
    );
    setNeverDrop();
    setNotImportant();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);
  }
}
