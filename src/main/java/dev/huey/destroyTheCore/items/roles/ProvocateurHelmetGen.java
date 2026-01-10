package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ProvocateurHelmetGen extends ItemGen {
  public ProvocateurHelmetGen() {
    super(
      ItemsManager.ItemKey.PROVOCATEUR_HELMET,
      Material.LEATHER_HELMET
    );
    setNeverDrop();
    setTrash();
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    LeatherArmorMeta meta = (LeatherArmorMeta) uncastedMeta;
    
    meta.setColor(Color.fromRGB(220, 175, 130));
    meta.addItemFlags(ItemFlag.HIDE_DYE);
    
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    
    meta.addAttributeModifier(
      Attribute.ARMOR,
      AttributeUtils.multiply("protection", EquipmentSlotGroup.CHEST, 0.8)
    );
  }
}
