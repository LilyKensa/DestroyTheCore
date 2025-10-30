package dev.huey.destroyTheCore.items.assistance;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageAssistGen extends ItemGen {
  public DamageAssistGen() {
    super(
      ItemsManager.ItemKey.DAMAGE_ASSIST,
      Material.DIAMOND_SWORD
    );
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addAttributeModifier(
      Attribute.ATTACK_DAMAGE,
      AttributeUtils.multiply("attack", EquipmentSlotGroup.OFFHAND, 1.25)
    );
  }
}
