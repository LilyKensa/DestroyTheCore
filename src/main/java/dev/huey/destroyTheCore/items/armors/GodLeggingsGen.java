package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttrUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class GodLeggingsGen extends ItemGen {
  
  public GodLeggingsGen() {
    super(ItemsManager.ItemKey.GOD_LEGGINGS, Material.GOLDEN_LEGGINGS);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, 2, true);
    
    meta.addAttributeModifier(
      Attribute.ARMOR,
      AttrUtils.addition("protection", EquipmentSlotGroup.LEGS, 5)
    );
    meta.addAttributeModifier(
      Attribute.ATTACK_DAMAGE,
      AttrUtils.addition("attack-damage", EquipmentSlotGroup.LEGS, 4)
    );
  }
}
