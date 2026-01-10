package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class HelmetGen extends ItemGen {
  public HelmetGen() {
    super(
      ItemsManager.ItemKey.GOLDEN_HELMET,
      Material.GOLDEN_HELMET
    );
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, 2, true);
    
    meta.addAttributeModifier(
      Attribute.ARMOR,
      AttributeUtils.addition("protection", EquipmentSlotGroup.HEAD, 2)
    );
    meta.addAttributeModifier(
      Attribute.KNOCKBACK_RESISTANCE,
      AttributeUtils.addition("knockback-resistance", EquipmentSlotGroup.HEAD, 2)
    );
  }
}
