package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoldDiggerRole extends Role {
  
  public GoldDiggerRole() {
    super(RolesManager.RoleKey.GOLD_DIGGER);
    addInfo(Material.GOLDEN_PICKAXE);
    addFeature();
    addExclusiveItem(
      Material.IRON_PICKAXE,
      meta -> {
        meta.addEnchant(Enchantment.EFFICIENCY, 3, true);
      }
    );
    addSkill(120 * 20);
  }
  
  @Override
  public ItemsManager.ItemKey defChestplate() {
    return ItemsManager.ItemKey.GOLD_DIGGER_CHESTPLATE;
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.GLOWING, 30, 0, true, false)
      );
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.SLOWNESS, 30, 0, true, false)
      );
      
      for (Player p : PlayerUtils.getTeammates(pl)) {
        if (p.equals(pl)) continue;
        if (!LocUtils.near(p, pl, 10)) continue;
        
        p.addPotionEffect(
          new PotionEffect(PotionEffectType.HASTE, 30, 0, true, false)
        );
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.LUCK, 15 * 20, 2, false, true)
    );
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.gold-digger.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
  }
}
