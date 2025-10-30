package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RangerRole extends Role {
  static public class Mine {
    boolean active = true;
    public Location loc;
    public UUID ownerId;
    public Game.Side side;
    
    public Mine(Location loc, Player owner) {
      this.loc = loc;
      this.ownerId = owner.getUniqueId();
      this.side = DestroyTheCore.game.getPlayerData(owner).side;
    }
  }
  
  static public List<Mine> mines = new ArrayList<>();
  
  static public void onPlayerMove(Player pl) {
    for (Mine mine : mines) {
      if (!DestroyTheCore.game.getPlayerData(pl).side
        .equals(mine.side.opposite())) continue;
      
      if (!LocationUtils.near(pl.getLocation(), mine.loc, 1.5)) continue;
      
      new ParticleBuilder(Particle.LAVA)
        .allPlayers()
        .location(mine.loc)
        .count(25)
        .extra(0)
        .spawn();
      
      Player owner = Bukkit.getPlayer(mine.ownerId);
      
      if (owner != null) pl.damage(1, DamageSource.builder(DamageType.ARROW)
        .withDamageLocation(mine.loc)
        .withDirectEntity(owner)
        .withCausingEntity(owner)
        .build()
      );
      pl.addPotionEffect(new PotionEffect(
        PotionEffectType.POISON,
        10 * 20,
        9,
        false,
        true
      ));
      
      if (owner == null) {
        PlayerUtils.send(pl, TextUtils.$(
          "roles.ranger.skill.trap-hit.enemy-unknown"));
      }
      else {
        PlayerUtils.send(pl, TextUtils.$(
          "roles.ranger.skill.trap-hit.enemy",
          List.of(
            Placeholder.component("owner", PlayerUtils.getName(owner))
          )
        ));
        PlayerUtils.send(owner, TextUtils.$(
          "roles.ranger.skill.trap-hit.owner",
          List.of(
            Placeholder.component("enemy", PlayerUtils.getName(pl))
          )
        ));
      }
      
      mine.active = false;
    }
    
    mines.removeIf(m -> !m.active);
  }
  
  static public void onParticleTick() {
    for (Mine mine : mines) {
      LocationUtils.ring(mine.loc, 1.5, loc -> {
        new ParticleBuilder(Particle.SMALL_FLAME)
          .receivers(PlayerUtils.getNonEnemies(mine.side))
          .location(loc)
          .extra(0)
          .spawn();
      });
      
      if (RandomUtils.hit(0.01))
        new ParticleBuilder(Particle.LAVA)
          .allPlayers()
          .location(mine.loc)
          .count(RandomUtils.range(3) + 1)
          .extra(0)
          .spawn();
      
      mine.loc.addRotation(1, 0);
    }
  }
  
  public RangerRole() {
    super(RolesManager.RoleKey.RANGER);
    addInfo(Material.CROSSBOW);
    addFeature();
    addExclusiveItem(
      Material.CROSSBOW,
      meta -> {
        meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
      }
    );
    addSkill(150 * 20);
  }
  
  @Override
  public ItemsManager.ItemKey defHelmet() {
    return ItemsManager.ItemKey.RANGER_HELMET;
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      if (pl.getInventory().getItemInMainHand().getType().equals(Material.CROSSBOW))
        pl.addPotionEffect(new PotionEffect(
          PotionEffectType.RESISTANCE,
          30,
          0,
          true,
          false
        ));
    }
  }
  
  @Override
  public void onPhaseChange(Game.Phase phase, Player pl) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    ItemStack item = new ItemStack(Material.ARROW, 20);
    
    if (data.alive) {
      pl.give(item);
    }
    else {
      pl.getWorld().dropItemNaturally(
        LocationUtils.live(
          LocationUtils.selfSide(DestroyTheCore.game.map.spawnPoint, data.side)
        ),
        item
      ).setPickupDelay(20);
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    mines.add(new Mine(pl.getLocation().add(0, 0.1, 0), pl));
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$("roles.ranger.skill.announce", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.unparsed("role", name)
      ))
    );
  }
}
