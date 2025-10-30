package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RangerHelmetGen extends ItemGen {
  public RangerHelmetGen() {
    super(
      ItemsManager.ItemKey.RANGER_HELMET,
      Material.LEATHER_HELMET
    );
    setNeverDrop();
    setNotImportant();
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    LeatherArmorMeta meta = (LeatherArmorMeta) uncastedMeta;
    
    meta.setColor(Color.BLACK);
    meta.addItemFlags(ItemFlag.HIDE_DYE);
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
  }
}
