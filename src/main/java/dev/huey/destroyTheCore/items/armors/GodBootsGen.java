package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class GodBootsGen extends ItemGen {
  
  public GodBootsGen() {
    super(ItemsManager.ItemKey.GOD_BOOTS, Material.GOLDEN_BOOTS);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, 2, true);
    
    meta.addAttributeModifier(
      Attribute.ARMOR,
      AttributeUtils.addition("protection", EquipmentSlotGroup.FEET, 2)
    );
    meta.addAttributeModifier(
      Attribute.MOVEMENT_SPEED,
      AttributeUtils.multiply("movement-speed", EquipmentSlotGroup.FEET, 1.4)
    );
  }
}
