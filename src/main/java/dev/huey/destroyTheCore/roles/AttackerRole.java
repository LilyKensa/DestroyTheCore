package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AttackerRole extends Role {
  
  public AttackerRole() {
    super(RolesManager.RoleKey.ATTACKER);
    addInfo(Material.WOODEN_SWORD);
    addFeature();
    addExclusiveItem(
      Material.WOODEN_SWORD,
      meta -> {
        meta.addEnchant(Enchantment.SHARPNESS, 3, true);
      }
    );
    addSkill(60 * 20);
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0, false, true)
    );
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.RESISTANCE, 10 * 20, 0, false, true)
    );
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.attacker.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
  }
}
