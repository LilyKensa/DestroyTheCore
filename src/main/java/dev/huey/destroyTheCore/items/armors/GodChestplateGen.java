package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class GodChestplateGen extends ItemGen {
  public GodChestplateGen() {
    super(
      ItemsManager.ItemKey.GOD_CHESTPLATE,
      Material.GOLDEN_CHESTPLATE
    );
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, 2, true);
    
    meta.addAttributeModifier(
      Attribute.ARMOR,
      AttributeUtils.addition("protection", EquipmentSlotGroup.CHEST, 6)
    );
    meta.addAttributeModifier(
      Attribute.MAX_HEALTH,
      AttributeUtils.addition("max-health", EquipmentSlotGroup.CHEST, 10)
    );
  }
}
