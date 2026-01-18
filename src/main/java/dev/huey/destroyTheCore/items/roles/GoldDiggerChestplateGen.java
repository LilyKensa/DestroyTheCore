package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class GoldDiggerChestplateGen extends ItemGen {
  
  public GoldDiggerChestplateGen() {
    super(
      ItemsManager.ItemKey.GOLD_DIGGER_CHESTPLATE,
      Material.IRON_CHESTPLATE
    );
    setNeverDrop();
    setTrash();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    meta.addEnchant(Enchantment.THORNS, 1, true);
    
    meta.addItemFlags(ItemFlag.HIDE_DYE);
  }
}
