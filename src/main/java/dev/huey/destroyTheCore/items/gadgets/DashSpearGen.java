package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.AttrUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DashSpearGen extends ItemGen {
  
  public DashSpearGen() {
    super(ItemsManager.ItemKey.DASH_SPEAR, Material.DIAMOND_SPEAR);
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    Damageable meta = (Damageable) uncastedMeta;
    
    meta.addAttributeModifier(
      Attribute.ATTACK_DAMAGE,
      AttrUtils.multiply("attack", EquipmentSlotGroup.MAINHAND, 0)
    );
    
    meta.addEnchant(Enchantment.LUNGE, 5, true);
    
    meta.setUnbreakable(false);
    meta.setDamage(Material.DIAMOND_SPEAR.getMaxDurability() - 1);
  }
}
