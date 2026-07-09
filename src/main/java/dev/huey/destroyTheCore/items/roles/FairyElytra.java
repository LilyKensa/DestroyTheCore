package dev.huey.destroyTheCore.items.roles;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FairyElytra extends ItemGen {
  
  public FairyElytra() {
    super(
      ItemsManager.ItemKey.FAIRY_ELYTRA,
      Material.ELYTRA
    );
    setNeverDrop();
    setTrash();
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    Damageable meta = (Damageable) uncastedMeta;
    
    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
    
    meta.setUnbreakable(false);
    meta.setDamage(432);
  }
}
