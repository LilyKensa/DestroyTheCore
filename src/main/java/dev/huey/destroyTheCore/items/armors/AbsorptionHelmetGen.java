package dev.huey.destroyTheCore.items.armors;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class AbsorptionHelmetGen extends ItemGen {
  public AbsorptionHelmetGen() {
    super(
      ItemsManager.ItemKey.ABSORPTION_HELMET,
      Material.TURTLE_HELMET
    );
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
    
    meta.addAttributeModifier(
      Attribute.ARMOR,
      AttributeUtils.multiply("protection", EquipmentSlotGroup.HEAD, 0)
    );
    meta.addAttributeModifier(
      Attribute.MAX_HEALTH,
      AttributeUtils.addition("max-health", EquipmentSlotGroup.HEAD, 40)
    );
  }
}
