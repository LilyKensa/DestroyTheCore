package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class MoleBootsGen extends ItemGen {
  public MoleBootsGen() {
    super(ItemsManager.ItemKey.MOLE_BOOTS, Material.IRON_BOOTS);
    setNeverDrop();
    setTrash();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
  }
}
