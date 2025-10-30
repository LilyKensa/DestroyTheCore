package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class KekkaiMasterLeggingsGen extends ItemGen {
  public KekkaiMasterLeggingsGen() {
    super(
      ItemsManager.ItemKey.KEKKAI_MASTER_LEGGINGS,
      Material.CHAINMAIL_LEGGINGS
    );
    setNeverDrop();
    setNotImportant();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    meta.addEnchant(Enchantment.SWIFT_SNEAK, 1, true);
  }
}
