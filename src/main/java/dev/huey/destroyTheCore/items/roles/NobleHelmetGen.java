package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class NobleHelmetGen extends ItemGen {
  
  public NobleHelmetGen() {
    super(ItemsManager.ItemKey.NOBLE_HELMET, Material.GOLDEN_HELMET);
    setNeverDrop();
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    meta.addAttributeModifier(
      Attribute.MAX_HEALTH,
      AttributeUtils.addition("max-health", EquipmentSlotGroup.HEAD, -4)
    );
  }
}
