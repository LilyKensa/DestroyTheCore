package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KekkaiMasterRole extends Role {
  
  public static class Kekkai {
    
    public enum Type {
      SPEED(Material.IRON_INGOT, Particle.WAX_OFF), HEALING(Material.GOLD_INGOT,
        Particle.HAPPY_VILLAGER), BULLET_PROOF(Material.REDSTONE,
          Particle.WITCH), RESISTANCE(Material.EMERALD,
            Particle.CRIT), FAST_ORES(Material.LAPIS_LAZULI,
              Particle.END_ROD), STRENGTH(Material.DIAMOND,
                Particle.WAX_ON), SPEED_PLUS(Material.IRON_BLOCK,
                  Particle.WAX_OFF,
                  180), SATURATION(Material.GOLD_BLOCK,
                    Particle.FLAME,
                    10), BULLET_PROOF_PLUS(Material.REDSTONE_BLOCK,
                      Particle.WITCH,
                      180), RESISTANCE_PLUS(Material.EMERALD_BLOCK,
                        Particle.CRIT,
                        30), FAST_ORES_PLUS(Material.LAPIS_BLOCK,
                          Particle.END_ROD,
                          180), STRENGTH_PLUS(Material.DIAMOND_BLOCK,
                            Particle.WAX_ON,
                            180), CHAOS(Material.BARRIER,
                              Particle.DRAGON_BREATH,
                              10), SOUL(Material.BARRIER,
                                Particle.SOUL_FIRE_FLAME);
      
      public final Material sourceMaterial;
      public final Particle particle;
      public final int duration;
      
      Type(Material type, Particle p, int seconds) {
        sourceMaterial = type;
        particle = p;
        duration = seconds * 20;
      }
      
      Type(Material type, Particle p) {
        this(type, p, 30);
      }
      
      public Component displayName() {
        return TextUtils.$(
          "roles.kekkai-master.kekkais.%s".formatted(
            name().toLowerCase().replace('_', '-')
          )
        ).color(null);
      }
    }
    
    int size = 15;
    int duration;
    public Type type;
    public Location loc;
    public UUID ownerId;
    public Game.Side side;
    public Slime center;
    public int centerYaw = 0;
    
    public PotionEffectType effectType;
    int effectLevel;
    
    void applyEffect() {
      switch (type) {
        case SPEED, SPEED_PLUS -> {
          effectType = PotionEffectType.SPEED;
          effectLevel = 5;
        }
        case HEALING -> {
          effectType = PotionEffectType.REGENERATION;
          effectLevel = 2;
        }
        case RESISTANCE -> {
          effectType = PotionEffectType.RESISTANCE;
          effectLevel = 2;
        }
        case RESISTANCE_PLUS -> {
          effectType = PotionEffectType.RESISTANCE;
          effectLevel = 4;
        }
        case STRENGTH -> {
          effectType = PotionEffectType.STRENGTH;
          effectLevel = 1;
        }
        case STRENGTH_PLUS -> {
          effectType = PotionEffectType.STRENGTH;
          effectLevel = 3;
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
    }
    
    public Kekkai(Type type, Location loc, Player owner) {
      this.type = type;
      this.duration = type.duration;
      this.loc = loc;
      this.ownerId = owner.getUniqueId();
      this.side = DestroyTheCore.game.getPlayerData(owner).side;
      
      int health = type.name().endsWith("PLUS") ? 30 : 10;
      applyEffect();
      
      center = (Slime) loc.getWorld().spawnEntity(loc.add(0, -0.25, 0),
        EntityType.SLIME);
      
      center.setAI(false);
      center.setSize(0);
      
      center.customName(
        Component.join(
          JoinConfiguration.noSeparators(),
          type.displayName(),
          TextUtils.$("roles.kekkai-master.center-name").color(null)
        ).color(side.color).decoration(TextDecoration.ITALIC, false)
      );
      
      center.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
      center.setHealth(health);
      
      center.addPotionEffect(
        new PotionEffect(PotionEffectType.RESISTANCE, 5 * 20, 9, true, false)
      );
    }
    
    public boolean contains(Location thatLoc) {
      return loc.distanceSquared(thatLoc) <= size * size;
    }
    
    public boolean isFastOres() {
      return List.of(Type.FAST_ORES, Type.FAST_ORES_PLUS).contains(type);
    }
    
    public boolean isBulletProof() {
      return List.of(Type.BULLET_PROOF, Type.BULLET_PROOF_PLUS).contains(type);
    }
    
    public void kill() {
      duration = -1;
      center.remove();
    }
  }
  
  public static List<Kekkai> kekkais = new ArrayList<>();
  
  public static boolean checkFastOres(Location oreLoc) {
    for (Kekkai kekkai : kekkais) {
      if (!kekkai.isFastOres()) continue;
      if (!kekkai.contains(oreLoc)) continue;
      
      return true;
    }
    
    return false;
  }
  
  public static void onEntityDeath(EntityDeathEvent ev) {
    for (Kekkai kekkai : kekkais) {
      if (kekkai.center.equals(ev.getEntity())) {
        ev.getDrops().clear();
        ev.setDroppedExp(5);
        
        kekkai.duration = -1;
        
        for (Player p : Bukkit.getOnlinePlayers()) p.playSound(
          kekkai.loc,
          Sound.ENTITY_BOGGED_DEATH,
          1, // Volume
          1 // Pitch
        );
      }
    }
    kekkais.removeIf(k -> k.duration <= 0);
  }
  
  public static void onTick() {
    for (Kekkai kekkai : kekkais) {
      if (kekkai.isBulletProof()) {
        for (Projectile proj : kekkai.loc.getNearbyEntitiesByType(
          Projectile.class,
          kekkai.size + 1
        )) {
          if (proj.isOnGround()) continue;
          if (
            proj instanceof Trident trident && trident.hasDealtDamage()
          ) continue;
          if (!kekkai.contains(proj.getLocation())) continue;
          if (kekkai.contains(proj.getOrigin())) continue;
          
          proj.setVelocity(
            CoreUtils.calculateBounce(
              kekkai.loc,
              proj.getLocation(),
              proj.getVelocity()
            )
          );
          
          new ParticleBuilder(Particle.WHITE_SMOKE).allPlayers().location(
            proj.getLocation()).count(3).extra(0).spawn();
        }
      }
      
      if (DestroyTheCore.ticksManager.isUpdateTick()) {
        for (Player p : kekkai.loc.getNearbyPlayers(kekkai.size + 1)) {
          PlayerData d = DestroyTheCore.game.getPlayerData(p);
          
          if (d.side != kekkai.side) continue;
          if (!kekkai.contains(p.getLocation())) continue;
          
          if (kekkai.effectType != null) p.addPotionEffect(
            new PotionEffect(
              kekkai.effectType,
              30,
              kekkai.effectLevel - 1,
              true,
              true
            )
          );
          
          if (kekkai.type == Kekkai.Type.SATURATION) {
            if (p.getFoodLevel() < 20) p.setFoodLevel(p.getFoodLevel() + 2);
            if (p.getSaturation() < 10) p.setSaturation(p.getSaturation() + 2);
          }
          
          if (kekkai.type == Kekkai.Type.SOUL) {
            p.addPotionEffect(
              new PotionEffect(PotionEffectType.SPEED, 30, 1, true, true)
            );
            p.addPotionEffect(
              new PotionEffect(PotionEffectType.RESISTANCE, 30, 1, true, true)
            );
            p.addPotionEffect(
              new PotionEffect(PotionEffectType.STRENGTH, 30, 1, true, true)
            );
            
            d.addRespawnTime(1);
            DestroyTheCore.boardsManager.refresh(p);
          }
        }
      }
      
      kekkai.centerYaw += 5;
      if (kekkai.centerYaw >= 360) kekkai.centerYaw -= 360;
      kekkai.center.setRotation(kekkai.centerYaw, 0);
      
      if (DestroyTheCore.ticksManager.ticksCount % 12 == 0) {
        kekkai.loc.addRotation(1, 0);
      }
      
      if (kekkai.duration <= 10 * 20 && kekkai.duration % 20 == 0) {
        for (Player p : kekkai.loc.getNearbyPlayers(kekkai.size + 1)) {
          PlayerData d = DestroyTheCore.game.getPlayerData(p);
          if (d.role.id != RolesManager.RoleKey.KEKKAI_MASTER) continue;
          if (d.side != kekkai.side) continue;
          if (!kekkai.contains(p.getLocation())) continue;
          if (
            DestroyTheCore.rolesManager.isExclusiveItem(
              p.getInventory().getItemInMainHand()
            )
          ) continue;
          
          p.sendActionBar(
            TextUtils.$(
              "roles.kekkai-master.skill.duration",
              List.of(
                Placeholder.component(
                  "value",
                  Component.text(kekkai.duration / 20)
                )
              )
            )
          );
        }
      }
      
      kekkai.duration--;
      if (kekkai.duration <= 0) {
        CoreUtils.setTickOut(() -> kekkai.center.remove());
      }
    }
    
    kekkais.removeIf(k -> k.duration <= 0);
  }
  
  public static void onParticleTick() {
    for (Kekkai kekkai : kekkais) {
      ParticleUtils.spiralSphere(kekkai.loc, kekkai.size, kekkai.type.particle);
    }
  }
  
  public KekkaiMasterRole() {
    super(RolesManager.RoleKey.KEKKAI_MASTER);
    addInfo(Material.BEACON);
    addFeature();
    addExclusiveItem(
      Material.FISHING_ROD,
      meta -> {
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
      }
    );
    addSkill(30 * 20);
  }
  
  @Override
  public ItemsManager.ItemKey defLeggings() {
    return ItemsManager.ItemKey.KEKKAI_MASTER_LEGGINGS;
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Kekkai kekkai : kekkais) {
        if (DestroyTheCore.game.getPlayerData(pl).side != kekkai.side) continue;
        
        if (
          kekkai.type != Kekkai.Type.CHAOS && DestroyTheCore.rolesManager.isExclusiveItem(
            pl.getInventory().getItemInMainHand()
          ) && kekkai.contains(pl.getLocation())
        ) {
          kekkai.duration += 10;
        }
      }
    }
    
    if (DestroyTheCore.ticksManager.isSeconds()) {
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30, 0, true, false)
      );
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    int replacedWarning = 0;
    for (Kekkai kekkai : kekkais) {
      if (!kekkai.contains(pl.getLocation())) continue;
      
      if (pl.isSneaking()) {
        kekkai.kill();
      }
      else {
        replacedWarning++;
      }
    }
    if (replacedWarning > 0) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 0);
      pl.sendActionBar(
        TextUtils.$(
          "roles.kekkai-master.skill.inside-kekkai",
          List.of(
            Placeholder.component("amount", Component.text(replacedWarning))
          )
        )
      );
      return;
    }
    kekkais.removeIf(k -> k.duration <= 0);
    
    ItemStack offhandItem = pl.getInventory().getItemInOffHand();
    
    Kekkai.Type type = null;
    for (Kekkai.Type t : Kekkai.Type.values()) if (
      offhandItem.getType().equals(t.sourceMaterial)
    ) type = t;
    
    if (
      DestroyTheCore.itemsManager.checkGen(
        offhandItem,
        ItemsManager.ItemKey.PLACEHOLDER
      )
    ) type = Kekkai.Type.CHAOS;
    if (
      DestroyTheCore.itemsManager.checkGen(
        offhandItem,
        ItemsManager.ItemKey.SOUL
      )
    ) type = Kekkai.Type.SOUL;
    
    if (type == null) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 0);
      pl.sendActionBar(TextUtils.$("roles.kekkai-master.skill.no-material"));
      return;
    }
    
    if (PlayerUtils.shouldHandle(pl)) {
      offhandItem.setAmount(offhandItem.getAmount() - 1);
      pl.getInventory().setItemInOffHand(offhandItem);
    }
    
    if (type.name().endsWith("PLUS")) pl.setCooldown(
      Material.KNOWLEDGE_BOOK,
      180 * 20
    );
    
    kekkais.add(new Kekkai(type, LocationUtils.hitboxCenter(pl), pl));
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.kekkai-master.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component("type", type.displayName())
        )
      )
    );
    
    skillFeedback(pl);
  }
}
