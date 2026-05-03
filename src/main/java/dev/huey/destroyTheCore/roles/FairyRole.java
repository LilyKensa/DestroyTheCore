package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.AttrUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffectType;

public class FairyRole extends Role {
  
  static public class Sprinkle {
    
    static final int maxLife = 3 * 20;
    static final double radius = 3;
    static final double gravity = -0.1;
    
    Location loc;
    double deltaY = gravity;
    
    Game.Side side;
    int life = maxLife;
    
    public Sprinkle(Location loc, Game.Side side) {
      this.loc = loc.clone();
      this.side = side;
    }
    
    public void onTick() {
      deltaY += gravity;
      Location next = loc.clone().add(0, deltaY, 0);
      
      if (next.getBlock().isCollidable()) {
        life--;
        deltaY = gravity;
      }
      else {
        life = maxLife;
        loc = next;
      }
      
      if (loc.getY() < loc.getWorld().getMinHeight()) life = -1;
      
      if (life <= 0) return;
      
      if (DTC.ticksManager.isParticleTick()) {
        new ParticleBuilder(Particle.FALLING_OBSIDIAN_TEAR)
          .allPlayers()
          .location(loc)
          .offset(
            life == maxLife ? 0.8 : radius,
            0.5,
            life == maxLife ? 0.8 : radius
          )
          .extra(0)
          .count(10)
          .spawn();
      }
      
      if (life == maxLife) return;
      
      if (DTC.ticksManager.isUpdateTick()) {
        for (Player p : PlayerUtils.allGaming()) {
          if (!LocUtils.near(Pos.of(p), Pos.of(loc), radius)) continue;
          
          if (DTC.game.getPlayerData(p).side == side) {
            PlayerUtils.addEffect(
              p,
              PotionEffectType.INSTANT_HEALTH,
              10 * 20,
              2
            );
            PlayerUtils.addEffect(
              p,
              PotionEffectType.SPEED,
              10 * 20,
              2
            );
          }
          else {
            PlayerUtils.addEffect(
              p,
              PotionEffectType.WITHER,
              10 * 20,
              2
            );
            PlayerUtils.addEffect(
              p,
              PotionEffectType.SLOWNESS,
              10 * 20,
              2
            );
          }
        }
      }
    }
  }
  
  static Queue<Sprinkle> queue = new ArrayDeque<>();
  
  static public void onTick() {
    for (Sprinkle s : queue) {
      s.onTick();
    }
    
    while (queue.peek() != null && queue.peek().life <= 0) {
      queue.remove();
    }
  }
  
  static Set<UUID> flying = new HashSet<>();
  
  static void fixElytra(Player pl) {
    flying.add(pl.getUniqueId());
    
    AttrUtils.set(pl, Attribute.MAX_HEALTH, 4);
    
    PlayerInventory inv = pl.getInventory();
    
    ItemStack elytra = inv.getChestplate();
    if (elytra == null || elytra.getType() != Material.ELYTRA) return;
    
    elytra.editMeta(Damageable.class, meta -> {
      meta.setDamage(0);
      meta.setUnbreakable(true);
    });
  }
  
  static void breakElytra(Player pl) {
    if (flying.contains(pl.getUniqueId())) {
      flying.remove(pl.getUniqueId());
      
      AttrUtils.set(pl, Attribute.MAX_HEALTH, 20);
      
      PlayerInventory inv = pl.getInventory();
      
      ItemStack elytra = inv.getChestplate();
      if (elytra == null || elytra.getType() != Material.ELYTRA) return;
      
      Damageable meta = (Damageable) elytra.getItemMeta();
      if (!meta.isUnbreakable()) return;
      
      meta.setUnbreakable(false);
      meta.setDamage(432);
      elytra.setItemMeta(meta);
      
      inv.setChestplate(elytra);
      
      PlayerUtils.addEffect(
        pl,
        PotionEffectType.SLOWNESS,
        5 * 20,
        2
      );
    }
  }
  
  static void spawnPotion(Location loc, Game.Side side) {
    queue.add(new Sprinkle(loc, side));
  }
  
  public FairyRole() {
    super(RolesManager.RoleType.ASSISTANCE, RolesManager.RoleKey.FAIRY);
    addInfo(Material.ELYTRA);
    addFeature();
    addExclusiveItem(Material.BOW, meta -> {
      meta.addEnchant(Enchantment.PUNCH, 1, true);
    });
    addSkill(180 * 20);
    addLevelReq(3);
  }
  
  @Override
  public ItemsManager.ItemKey defChestplate() {
    return ItemsManager.ItemKey.FAIRY_ELYTRA;
  }
  
  @Override
  public void onTick(Player pl) {
    if (DTC.ticksManager.isUpdateTick()) {
      PlayerData data = DTC.game.getPlayerData(pl);
      
      if (((Entity) pl).isOnGround()) {
        breakElytra(pl);
      }
      
      if (pl.isGliding()) {
        spawnPotion(pl.getLocation(), data.side);
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
        "roles.fairy.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
    
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.LEVITATION,
      20,
      30
    );
    
    fixElytra(pl);
  }
}
