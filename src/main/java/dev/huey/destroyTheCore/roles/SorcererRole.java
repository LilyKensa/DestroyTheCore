package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.*;
import java.util.*;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SorcererRole extends Role {
  
  public enum Spell {
    SLOW(Material.IRON_INGOT, Particle.LARGE_SMOKE),
    HUNGER(
      Material.GOLD_INGOT,
      Particle.BUBBLE_POP
    ),
    BLIND(
      Material.REDSTONE,
      Particle.WITCH
    ),
    WEAK(
      Material.EMERALD,
      Particle.CRIT
    ),
    POISON(
      Material.LAPIS_LAZULI,
      Particle.FLAME,
      5
    ),
    TURN(
      Material.DIAMOND,
      Particle.INSTANT_EFFECT
    ),
    SLOW_PLUS(
      Material.IRON_BLOCK,
      Particle.LARGE_SMOKE
    ),
    HUNGER_PLUS(
      Material.GOLD_BLOCK,
      Particle.BUBBLE_POP
    ),
    BLIND_PLUS(
      Material.REDSTONE_BLOCK,
      Particle.WITCH
    ),
    WEAK_PLUS(
      Material.EMERALD_BLOCK,
      Particle.CRIT
    ),
    POISON_PLUS(
      Material.LAPIS_BLOCK,
      Particle.FLAME
    ),
    TURN_PLUS(
      Material.DIAMOND_BLOCK,
      Particle.INSTANT_EFFECT
    ),
    CHAOS(
      Material.BARRIER,
      Particle.DRAGON_BREATH
    ),
    SOUL(
      Material.BARRIER,
      Particle.SOUL_FIRE_FLAME
    );
    
    public final Material sourceMaterial;
    public final Particle particle;
    public final int duration;
    
    Spell(Material type, Particle p, int seconds) {
      sourceMaterial = type;
      particle = p;
      duration = seconds * 20;
    }
    
    Spell(Material type, Particle p) {
      this(type, p, 10);
    }
    
    public Component displayName() {
      return TextUtils.$(
        "roles.sorcerer.spells.%s".formatted(
          name().toLowerCase().replace('_', '-')
        )
      ).color(null);
    }
    
    void applyEffect(Player pl) {
      PotionEffectType effectType = PotionEffectType.BAD_OMEN;
      int effectLevel = 1;
      
      switch (this) {
        case SLOW, SLOW_PLUS -> {
          effectType = PotionEffectType.SLOWNESS;
          effectLevel = 2;
        }
        case HUNGER, HUNGER_PLUS -> {
          effectType = PotionEffectType.HUNGER;
          effectLevel = 2;
        }
        case BLIND -> {
          effectType = PotionEffectType.DARKNESS;
        }
        case BLIND_PLUS -> {
          effectType = PotionEffectType.BLINDNESS;
        }
        case WEAK, WEAK_PLUS -> {
          effectType = PotionEffectType.WEAKNESS;
        }
        case POISON -> {
          effectType = PotionEffectType.POISON;
        }
        case POISON_PLUS -> {
          effectType = PotionEffectType.WITHER;
        }
        case CHAOS -> {
          effectType = RandomUtils.pick(
            PotionEffectType.STRENGTH,
            PotionEffectType.WEAKNESS,
            PotionEffectType.SPEED,
            PotionEffectType.SLOWNESS,
            PotionEffectType.REGENERATION,
            PotionEffectType.POISON,
            PotionEffectType.JUMP_BOOST,
            PotionEffectType.BLINDNESS,
            PotionEffectType.ABSORPTION,
            PotionEffectType.WITHER
          );
          effectLevel = 3;
        }
      }
      
      switch (this) {
        case SLOW_PLUS -> {
          effectLevel = 4;
        }
        case HUNGER_PLUS -> {
          effectLevel = 5;
        }
        case WEAK_PLUS, POISON_PLUS -> {
          effectLevel = 2;
        }
      }
      
      PlayerUtils.addEffect(pl, effectType, 10 * 20, effectLevel);
    }
  }
  
  static Map<UUID, Integer> turnUntil = new HashMap<>();
  static Map<UUID, Integer> turnPlusUntil = new HashMap<>();
  
  static boolean checkTime(Map<UUID, Integer> map, Player pl) {
    return DTC.game.isPlaying &&
      !DTC.game.paused &&
      DTC.ticksManager.ticksCount <= map.getOrDefault(pl.getUniqueId(), -1);
  }
  
  static void setTime(Map<UUID, Integer> map, Player pl, int ticks) {
    map.put(pl.getUniqueId(), DTC.ticksManager.ticksCount + ticks);
  }
  
  static public void onPlayerJump(Player pl) {
    CoreUtils.setTickOut(() -> {
      Location loc = pl.getLocation();
      
      if (checkTime(turnPlusUntil, pl)) {
        loc.setRotation(
          loc.getYaw() + RandomUtils.range(-160, 160),
          loc.getPitch()
        );
      }
      else if (checkTime(turnUntil, pl)) {
        loc.setRotation(loc.getYaw() + 180, loc.getPitch());
      }
      else return;
      
      Vector vel = pl.getVelocity();
      pl.teleport(
        loc,
        PlayerTeleportEvent.TeleportCause.PLUGIN
      );
      pl.setVelocity(vel);
    }, 1);
  }
  
  public SorcererRole() {
    super(RolesManager.RoleType.ASSISTANCE, RolesManager.RoleKey.SORCERER);
    addInfo(Material.FLOW_POTTERY_SHERD);
    addFeature();
    addExclusiveItem(
      Material.DIAMOND_HOE,
      meta -> {
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
      }
    );
    addSkill(30 * 20);
    addLevelReq(4);
  }
  
  @Override
  public ItemsManager.ItemKey defLeggings() {
    return ItemsManager.ItemKey.KEKKAI_MASTER_LEGGINGS;
  }
  
  @Override
  public void onTick(Player pl) {
    if (DTC.ticksManager.isUpdateTick()) {
      pl.removePotionEffect(PotionEffectType.WITHER);
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    
    Vector dir = pl.getLocation().getDirection().setY(0).normalize();
    Player target = PlayerUtils.getEnemies(data.side).stream()
      .filter(e -> LocUtils.near(e, pl, 10))
      .min(Comparator.comparingDouble(e -> {
        Vector toEnemy = e.getLocation().subtract(pl.getLocation()).toVector();
        
        double length = toEnemy.length();
        if (length < 0.001) return -1;
        toEnemy.divide(new Vector(length, length, length));
        
        return dir.dot(toEnemy);
      }))
      .orElse(null);
    
    if (target == null) {
      PlayerUtils.setSkillCooldown(pl, 10);
      
      data.skillReloadedMessage = true;
      pl.sendActionBar(TextUtils.$("roles.sorcerer.skill.no-target"));
      return;
    }
    
    ItemStack offhandItem = pl.getInventory().getItemInOffHand();
    
    Spell spell = null;
    for (Spell s : Spell.values()) {
      if (offhandItem.getType().equals(s.sourceMaterial)) {
        spell = s;
      }
    }
    
    if (
      DTC.itemsManager.checkGen(
        offhandItem,
        ItemsManager.ItemKey.PLACEHOLDER
      )
    ) spell = Spell.CHAOS;
    if (
      DTC.itemsManager.checkGen(
        offhandItem,
        ItemsManager.ItemKey.SOUL
      )
    ) spell = Spell.SOUL;
    
    if (spell == null) {
      PlayerUtils.setSkillCooldown(pl, 10);
      
      data.skillReloadedMessage = true;
      pl.sendActionBar(TextUtils.$("roles.sorcerer.skill.no-material"));
      return;
    }
    
    if (PlayerUtils.shouldHandle(pl)) {
      offhandItem.setAmount(offhandItem.getAmount() - 1);
      pl.getInventory().setItemInOffHand(offhandItem);
    }
    
    if (spell.name().endsWith("PLUS")) {
      pl.setCooldown(
        Material.KNOWLEDGE_BOOK,
        3 * skillCooldown
      );
    }
    
    final Spell finalSpell = spell;
    PlayerUtils.delayAssign(pl, target, finalSpell.particle, () -> {
      if (finalSpell == Spell.TURN) {
        setTime(turnUntil, target, 10 * 20);
      }
      else if (finalSpell == Spell.TURN_PLUS) {
        setTime(turnPlusUntil, target, 10 * 20);
      }
      else if (finalSpell == Spell.SOUL) {
        Consumer<PotionEffectType> effector = effect -> {
          PlayerUtils.addEffect(
            target,
            effect,
            10 * 20,
            1
          );
        };
        
        effector.accept(PotionEffectType.SLOWNESS);
        effector.accept(PotionEffectType.DARKNESS);
        effector.accept(PotionEffectType.WEAKNESS);
      }
      else {
        finalSpell.applyEffect(target);
      }
      
      target.playSound(
        target.getLocation(),
        Sound.ENTITY_WITCH_CELEBRATE,
        1, // Volume
        1 // Pitch
      );
    });
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.sorcerer.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("role", name),
          Placeholder.component("type", spell.displayName())
        )
      )
    );
    
    skillFeedback(pl);
  }
}
