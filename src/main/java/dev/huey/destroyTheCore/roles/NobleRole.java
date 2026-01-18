package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NobleRole extends Role {
  public NobleRole() {
    super(RolesManager.RoleKey.NOBLE);
    addInfo(Material.GOLDEN_HELMET);
    addFeature();
    addExclusiveItem(
      Material.GOLDEN_SWORD,
      meta -> meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true)
    );
    
    addSkill(180 * 20);
  }
  
  @Override
  public ItemsManager.ItemKey defHelmet() {
    return ItemsManager.ItemKey.NOBLE_HELMET;
  }
  
  public void useSkill(Player pl) {
    for (Player p : PlayerUtils.getTeammates(pl)) {
      p.addPotionEffect(new PotionEffect(
        PotionEffectType.ABSORPTION,
        60 * 20,
        2,
        false,
        false
      ));
      pl.addPotionEffect(new PotionEffect(
        PotionEffectType.ABSORPTION,
        60 * 20,
        2,
        false,
        false
      ));
    }
    
    
  }
  
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isUpdateTick())
      for (Entity entity : pl.getNearbyEntities(10, 10, 10))
        if (entity instanceof Player p)
          if (DestroyTheCore.game.getPlayerData(
            pl).side == DestroyTheCore.game.getPlayerData(p).side) {
              p.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                1 * 20,
                0,
                false,
                false
              ));
            }
  }
  
  public static void onPlayerDeath(Player pl) {
    for (Entity entity : pl.getLocation().getNearbyPlayers(15)) {
      if (entity instanceof Player p) {
        if (
          DestroyTheCore.game.getPlayerData(
            pl).side != DestroyTheCore.game.getPlayerData(
              p).side && DestroyTheCore.game.getPlayerData(
                pl).role.id == RolesManager.RoleKey.NOBLE) {
          pl.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
        }
      }
    }
  }
}
