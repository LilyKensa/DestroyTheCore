package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ShameChestplateGen extends ItemGen {
  public ShameChestplateGen() {
    super(
      ItemsManager.ItemKey.SHAME_CHESTPLATE,
      Material.LEATHER_CHESTPLATE
    );
    setVanish();
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    LeatherArmorMeta meta = (LeatherArmorMeta) uncastedMeta;
    meta.setColor(Color.WHITE);
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    
    meta.addItemFlags(ItemFlag.HIDE_DYE);
  }
}
