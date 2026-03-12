package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ProvocateurRole extends Role {
  
  public ProvocateurRole() {
    super(RolesManager.RoleKey.PROVOCATEUR);
    addInfo(Material.HEAVY_CORE);
    addExclusiveItem(
      Material.PUMPKIN_PIE,
      meta -> {
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
      }
    );
    addSkill(180 * 20);
  }
  
  @Override
  public ItemsManager.ItemKey defHelmet() {
    return ItemsManager.ItemKey.PROVOCATEUR_HELMET;
  }
  
  @Override
  public void onTick(Player pl) {
    if (
      PlayerUtils.getTeammates(pl).stream().anyMatch(
        p -> !p.equals(
          pl
        ) && LocUtils.near(p, pl, 10)
      )
    ) {
      new ParticleBuilder(Particle.PORTAL)
        .allPlayers()
        .location(LocUtils.hitboxCenter(pl))
        .extra(2)
        .spawn();
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerUtils.glow(pl, 10 * 20);
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.ABSORPTION,
      10 * 20,
      10
    );
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.RESISTANCE,
      10 * 20,
      3
    );
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.WEAKNESS,
      10 * 20,
      1
    );
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.provocateur.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
  }
}
