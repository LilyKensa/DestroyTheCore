package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.managers.TicksManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MoleRole extends Role {
  
  static public void setMoleMode(Player pl) {
    pl.setInvisible(true);
    pl.setInvulnerable(true);
    pl.getAttribute(Attribute.SCALE).setBaseValue(0.5);
    pl.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.15);
    pl.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(1.5);
    
    DestroyTheCore.inventoriesManager.store(pl);
  }
  
  static public void resetMoleMode(Player pl) {
    pl.setInvisible(false);
    pl.setInvulnerable(false);
    pl.getAttribute(Attribute.SCALE).setBaseValue(1);
    pl.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);
    pl.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(0.6);
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    if (data.alive) {
      DestroyTheCore.inventoriesManager.restore(pl);
    }
  }
  
  static public void pound(Player pl) {
    final double radius = 6.0;
    final double maxDamage = 10.0;
    
    new ParticleBuilder(Particle.EXPLOSION)
      .allPlayers()
      .location(pl.getLocation().add(0, 0.1, 0))
      .offset(1.4, 1, 1.4)
      .extra(0)
      .count(10)
      .spawn();
    
    for (Player p : pl.getLocation().getNearbyPlayers(radius)) {
      p.playSound(
        pl,
        Sound.ENTITY_GENERIC_EXPLODE,
        1, // Volume
        1 // Pitch
      );
    }
    
    for (Player e : PlayerUtils.getEnemies(pl)) {
      double distance = e.getLocation().distance(pl.getLocation());
      
      if (distance > radius) continue;
      
      double damageRatio = 1 - (distance / radius);
      double finalDamage = damageRatio * maxDamage;
      e.damage(
        finalDamage,
        DamageSource.builder(DamageType.PLAYER_EXPLOSION)
          .withCausingEntity(pl)
          .withDirectEntity(pl)
          .build()
      );
      
      Vector knockback = e.getLocation().toVector().subtract(
        pl.getLocation().toVector()
      );
      
      if (knockback.length() > 0) {
        knockback.normalize();
      }
      
      knockback.multiply(damageRatio * 1.5).setY(0.4);
      e.setVelocity(knockback);
    }
  }
  
  static Map<UUID, Integer> moleModeTime = new HashMap<>();
  
  static public void onUpdateTick() {
    Iterator<Map.Entry<UUID, Integer>> it = moleModeTime.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<UUID, Integer> entry = it.next();
      UUID id = entry.getKey();
      int time = entry.getValue() - TicksManager.updateRate;
      
      entry.setValue(time);
      
      Player pl = Bukkit.getPlayer(id);
      if (pl == null || !pl.isOnline()) continue;
      
      if (time < 0) {
        pound(pl);
        resetMoleMode(pl);
        it.remove();
      }
    }
  }
  
  static public void onParticleTick() {
    for (UUID id : moleModeTime.keySet()) {
      Player pl = Bukkit.getPlayer(id);
      if (pl == null || !pl.isOnline()) continue;
      
      Block standingBlock = pl.getLocation().getBlock().getRelative(
        BlockFace.DOWN
      );
      
      new ParticleBuilder(Particle.BLOCK)
        .data(
          Bukkit.createBlockData(
            standingBlock.getType().isAir() ? Material.DIRT : standingBlock
              .getType()
          )
        )
        .allPlayers()
        .location(pl.getLocation())
        .offset(0.2, 0.1, 0.2)
        .extra(0)
        .count(20)
        .spawn();
    }
  }
  
  static public void onPlayerJump(Player pl) {
    if (!moleModeTime.containsKey(pl.getUniqueId())) return;
    
    pound(pl);
    resetMoleMode(pl);
    
    moleModeTime.remove(pl.getUniqueId());
  }
  
  static final EnumSet<Material> hardBlocks = EnumSet.of(
    Material.IRON_ORE,
    Material.GOLD_ORE,
    Material.EMERALD_ORE,
    Material.LAPIS_ORE,
    Material.REDSTONE_ORE,
    Material.DIAMOND_ORE,
    Material.END_STONE,
    Material.ANCIENT_DEBRIS,
    Material.OBSIDIAN,
    Material.CRYING_OBSIDIAN,
    Material.ENCHANTING_TABLE,
    Material.BOOKSHELF,
    Material.FURNACE,
    Material.BLAST_FURNACE,
    Material.SMOKER,
    Material.CHEST,
    Material.BARREL,
    Material.ENDER_CHEST
  );
  
  static public void onBlockBreak(Player pl, Block block, BlockBreakEvent ev) {
    ItemStack item = pl.getInventory().getItemInMainHand();
    if (
      !DestroyTheCore.rolesManager.checkExclusiveItem(
        item,
        RolesManager.RoleKey.MOLE
      )
    ) return;
    
    if (PlayerUtils.getHandCooldown(pl) > 0) {
      ev.setCancelled(true);
      return;
    }
    
    if (hardBlocks.contains(block.getType())) {
      pl.sendActionBar(TextUtils.$("roles.mole.block-too-hard"));
      
      PlayerUtils.setHandCooldown(pl, 20);
      
      pl.damage(
        2,
        DamageSource.builder(DamageType.FALL).build()
      );
      PlayerUtils.addEffect(
        pl,
        PotionEffectType.HUNGER,
        20,
        3
      );
      
      ev.setCancelled(true);
      return;
    }
    
    pl.setExhaustion(pl.getExhaustion() + 0.045f);
  }
  
  public MoleRole() {
    super(RolesManager.RoleKey.MOLE);
    addInfo(Material.RABBIT_HIDE);
    addFeature();
    addExclusiveItem(
      Material.PRISMARINE_SHARD,
      meta -> {
        meta.setEnchantmentGlintOverride(true);
        
        ToolComponent toolComp = meta.getTool();
        
        toolComp.addRule(org.bukkit.Tag.MINEABLE_PICKAXE, 85f, true);
        toolComp.addRule(org.bukkit.Tag.MINEABLE_SHOVEL, 15f, true);
        toolComp.addRule(org.bukkit.Tag.MINEABLE_AXE, 10f, true);
        toolComp.addRule(org.bukkit.Tag.MINEABLE_HOE, 10f, true);
        
        meta.setTool(toolComp);
      }
    );
    addSkill(60 * 20);
  }
  
  @Override
  public ItemsManager.ItemKey defBoots() {
    return ItemsManager.ItemKey.MOLE_BOOTS;
  }
  
  @Override
  public void onTick(Player pl) {
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      if (PlayerUtils.isUnderSky(pl)) {
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.DARKNESS,
          40,
          1
        );
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    if (moleModeTime.containsKey(pl.getUniqueId())) return;
    
    skillFeedback(pl);
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.mole.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
    
    setMoleMode(pl);
    moleModeTime.put(pl.getUniqueId(), 5 * 20);
  }
}
