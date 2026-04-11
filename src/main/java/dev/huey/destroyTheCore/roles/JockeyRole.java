package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.AttrUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class JockeyRole extends Role {
  public JockeyRole() {
    super(RolesManager.RoleType.ATTACKING, RolesManager.RoleKey.JOCKEY);
    addInfo(Material.DIAMOND_HORSE_ARMOR);
    addFeature();
    addExclusiveItem(Material.LEAD, meta -> {
      meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
    });
    addSkill(60 * 20);
    addLevelReq(4);
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      if (pl.getVehicle() instanceof Horse) {
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.STRENGTH,
          20,
          1
        );
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.RESISTANCE,
          20,
          2
        );
      }
      else {
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.SLOWNESS,
          20,
          1
        );
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.jockey.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component(
            "action",
            TextUtils.$(
              "roles.jockey.skill.actions." + (pl
                .getVehicle() instanceof Horse ? "enhance" : "summon")
            )
          )
        )
      )
    );
    
    if (pl.getVehicle() instanceof Horse horse) {
      AttrUtils.set(horse, Attribute.MAX_HEALTH, 30);
      horse.setHealth(AttrUtils.get(horse, Attribute.MAX_HEALTH));
      
      HorseInventory inv = horse.getInventory();
      Material armorType;
      if (inv.getArmor() == null) {
        armorType = Material.IRON_HORSE_ARMOR;
      }
      else {
        armorType = Material.DIAMOND_HORSE_ARMOR;
      }
      
      inv.setArmor(new ItemStack(armorType));
      
      new ParticleBuilder(Particle.HEART)
        .allPlayers()
        .location(horse.getEyeLocation())
        .offset(0.6, 0.2, 0.6)
        .count(10)
        .extra(0)
        .spawn();
    }
    else {
      Horse horse = (Horse) pl.getWorld().spawnEntity(
        pl.getLocation(),
        EntityType.HORSE
      );
      
      horse.setColor(Horse.Color.WHITE);
      horse.setStyle(Horse.Style.NONE);
      
      horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      
      horse.setOwner(pl);
      horse.addPassenger(pl);
    }
  }
}
