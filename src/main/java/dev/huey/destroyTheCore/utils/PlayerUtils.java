package dev.huey.destroyTheCore.utils;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.Pos;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Openable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class PlayerUtils {
  static public Player getPlayerByEntityId(int id) {
    return Bukkit.getOnlinePlayers().stream().filter(
      p -> p.getEntityId() == id
    ).findAny().orElse(null);
  }
  
  static public boolean wearingLeather(Player pl) {
    Predicate<Material> isLeather = (
      type
    ) -> type == Material.LEATHER_HELMET ||
      type == Material.LEATHER_CHESTPLATE ||
      type == Material.LEATHER_LEGGINGS ||
      type == Material.LEATHER_BOOTS;
    
    for (ItemStack item : pl.getInventory().getArmorContents()) {
      if (item != null && isLeather.test(item.getType())) {
        return true;
      }
    }
    return false;
  }
  
  static public boolean isUnderSky(Player pl) {
    int emptyCount = 0;
    
    for (int dx = -1; dx <= 1; ++dx) {
      horizontalLoop: for (int dz = -1; dz <= 1; ++dz) {
        for (int dy = 0; dy <= 8; ++dy) {
          if (pl.getLocation().add(dx, dy, dz).getBlock().isCollidable())
            continue horizontalLoop;
        }
        
        emptyCount++;
      }
    }
    
    return emptyCount > 4;
  }
  
  static public void send(Player pl, Component component) {
    pl.sendMessage(component.colorIfAbsent(NamedTextColor.GRAY));
  }
  
  static public void send(Player pl, String text, TextColor color) {
    send(pl, Component.text(text).color(color));
  }
  
  static public void send(Player pl, String text) {
    send(pl, text, NamedTextColor.GRAY);
  }
  
  /** {@link #send} with {@link DTC#prefix} */
  static public void prefixedSend(Player pl, Component component) {
    if (DTC.prefix == null) return;
    send(pl, DTC.prefix.append(component));
  }
  
  static public void prefixedSend(Player pl, String text, TextColor color) {
    prefixedSend(pl, Component.text(text).color(color));
  }
  
  static public void prefixedSend(Player pl, String text) {
    prefixedSend(pl, text, NamedTextColor.GRAY);
  }
  
  static public void notice(Component comp) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (isAdmin(p)) {
        send(p, comp);
      }
    }
  }
  
  /** Broadcast to admins */
  static public void prefixedNotice(Component comp) {
    if (DTC.prefix == null) return;
    notice(DTC.prefix.append(comp));
  }
  
  /** Broadcast to everyone */
  static public void broadcast(Component comp) {
    for (Player pl : Bukkit.getOnlinePlayers()) {
      send(pl, comp);
    }
  }
  
  /** Broadcast to nearby players */
  static public void auraBroadcast(
    Location center, double dist, Component comp
  ) {
    for (Player pl : center.getWorld().getPlayers()) {
      if (LocUtils.near(Pos.of(pl), Pos.of(center), dist)) {
        send(pl, comp);
      }
    }
  }
  
  /** {@link #broadcast} with {@link DTC#prefix} */
  static public void prefixedBroadcast(Component comp) {
    for (Player pl : Bukkit.getOnlinePlayers()) {
      prefixedSend(pl, comp);
    }
  }
  
  static public void prefixedBroadcast(String text, TextColor color) {
    prefixedBroadcast(Component.text(text).color(color));
  }
  
  static public void prefixedBroadcast(String text) {
    prefixedBroadcast(text, NamedTextColor.GRAY);
  }
  
  static public List<Player> all() {
    return new ArrayList<>(Bukkit.getOnlinePlayers());
  }
  
  static public List<Player> allGaming() {
    return all().stream().filter(
      p -> DTC.game.getPlayerData(p).isGaming()
    ).toList();
  }
  
  static public Component getName(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    
    return Component.text(
      "[%s] %s".formatted(
        data.side == Game.Side.SPECTATOR
          ? data.side.pureTitle()
          : data.role.name,
        pl.getName()
      )
    ).color(data.side.color);
  }
  
  /** Send 1.5 + 0.25 title duration */
  static public void normalTitleTimes(Player pl) {
    pl.sendTitlePart(
      TitlePart.TIMES,
      Title.Times.times(
        Duration.ZERO,
        Duration.ofMillis(1500),
        Duration.ofMillis(250)
      )
    );
  }
  
  /** Send 3 + 1 title duration */
  static public void longTitleTimes(Player pl) {
    pl.sendTitlePart(
      TitlePart.TIMES,
      Title.Times.times(
        Duration.ZERO,
        Duration.ofMillis(3000),
        Duration.ofMillis(1000)
      )
    );
  }
  
  /**
   * If that block has right click functionalities, and shouldn't be canceled
   */
  static public boolean checkUsingBlock(Player pl, Block block) {
    return (block != null &&
      block.getBlockData() instanceof Openable &&
      !pl
        .isSneaking());
  }
  
  static public ItemStack getHandItem(Player pl) {
    return pl.getInventory().getItemInMainHand();
  }
  
  static public void setHandCooldown(Player pl, int ticks) {
    pl.setCooldown(getHandItem(pl).getType(), ticks);
  }
  
  static public int getHandCooldown(Player pl) {
    return pl.getCooldown(getHandItem(pl).getType());
  }
  
  static public boolean checkHandCooldown(Player pl, int offset) {
    if (!shouldHandle(pl)) return true;
    
    int cooldown = getHandCooldown(pl) - offset;
    if (cooldown > 0) {
      pl.sendActionBar(
        TextUtils.$(
          "player.in-cooldown",
          List.of(
            Placeholder.unparsed("value", CoreUtils.toFixed(cooldown / 20D, 1))
          )
        )
      );
      return false;
    }
    
    return true;
  }
  
  static public boolean checkHandCooldown(Player pl) {
    return checkHandCooldown(pl, 0);
  }
  
  static public void setGroupCooldown(
    Player pl, List<ItemsManager.ItemKey> keys, int ticks
  ) {
    for (ItemsManager.ItemKey key : keys) {
      pl.setCooldown(
        DTC.itemsManager.gens.get(key).getItem().getType(),
        ticks
      );
    }
  }
  
  static public int getGroupCooldown(
    Player pl, List<ItemsManager.ItemKey> keys
  ) {
    return pl.getCooldown(
      DTC.itemsManager.gens.get(keys.getFirst()).getItem().getType()
    );
  }
  
  static public boolean checkGroupCooldown(
    Player pl, List<ItemsManager.ItemKey> keys
  ) {
    if (!shouldHandle(pl)) return true;
    
    int cooldown = getGroupCooldown(pl, keys);
    if (cooldown > 0) {
      pl.sendActionBar(
        TextUtils.$(
          "player.group-in-cooldown",
          List.of(
            Placeholder.unparsed("value", CoreUtils.toFixed(cooldown / 20D, 1))
          )
        )
      );
      return false;
    }
    
    return true;
  }
  
  static public void takeOneItemFromHand(Player pl) {
    if (pl.getGameMode().equals(GameMode.CREATIVE)) return;
    
    ItemStack item = pl.getInventory().getItemInMainHand();
    item.setAmount(item.getAmount() - 1);
    pl.getInventory().setItemInMainHand(item);
  }
  
  static public void damageHandItem(Player pl) {
    ItemStack item = getHandItem(pl);
    if (item.isEmpty()) return;
    if (item.getType().getMaxDurability() <= 0) return;
    
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      if (meta.isUnbreakable()) return;
      if (
        meta.hasEnchant(Enchantment.UNBREAKING) &&
          RandomUtils.hit(
            1D / (meta.getEnchantLevel(Enchantment.UNBREAKING) + 1)
          )
      ) return;
    }
    
    pl.damageItemStack(EquipmentSlot.HAND, 1);
  }
  
  /** Skip players in creative mode */
  static public boolean shouldHandle(Player pl) {
    return !pl.getGameMode().equals(GameMode.CREATIVE);
  }
  
  static public boolean isAdmin(Player pl) {
    return pl.hasPermission("dtc.admin");
  }
  
  static public void reportNoPerm(Player pl) {
    prefixedSend(pl, TextUtils.$("player.no-perm"));
  }
  
  static public void backToLobby(Player pl) {
    if (DTC.game.lobby.spawn == null) return;
    
    pl.setGameMode(GameMode.SURVIVAL);
    fullyHeal(pl);
    pl.teleport(LocUtils.lobby(DTC.game.lobby.spawn));
  }
  
  static public void teleportToRestArea(Player pl) {
    if (DTC.game.map.restArea == null) return;
    
    if (
      DTC.game.getPlayerData(pl).side.equals(Game.Side.SPECTATOR)
    ) {
      Location centerLoc = LocUtils.live(DTC.game.map.restArea);
      centerLoc.setYaw(0);
      centerLoc.setX(0);
      pl.teleport(centerLoc);
    }
    else {
      pl.teleport(
        LocUtils.live(
          LocUtils.selfSide(
            DTC.game.map.restArea,
            pl
          )
        )
      );
    }
  }
  
  static public void teleportToSpawnPoint(Player pl) {
    pl.teleport(
      LocUtils.live(
        LocUtils.selfSide(
          LocUtils.toSpawnPoint(
            RandomUtils.pick(DTC.game.map.spawnpoints)
          ),
          pl
        )
      )
    );
  }
  
  static public void resetHunger(Player pl) {
    pl.setFoodLevel(20);
    pl.setSaturation(7);
  }
  
  static public void fullyHeal(Player pl) {
    resetHunger(pl);
    pl.setHealth(AttrUtils.get(pl, Attribute.MAX_HEALTH));
    pl.setFireTicks(0);
    pl.setFreezeTicks(0);
    pl.setFallDistance(0);
  }
  
  /** Refresh night vision effect based on their preference */
  static public void enforceNightVision(Player pl) {
    if (DTC.game.getStats(pl).nightVision) {
      addPassiveEffect(
        pl,
        PotionEffectType.NIGHT_VISION,
        PotionEffect.INFINITE_DURATION,
        1
      );
    }
    else {
      pl.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
  }
  
  static public void refreshSpectatorVisibility(Player target, Player viewer) {
    if (
      DTC.game.isPlaying &&
        DTC.game.getPlayerData(
          target
        ).side == Game.Side.SPECTATOR
    ) {
      viewer.hidePlayer(DTC.instance, target);
    }
    else {
      viewer.showPlayer(DTC.instance, target);
    }
  }
  
  static public void refreshAllSpectatorVisibilitiesFor(Player pl) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      refreshSpectatorVisibility(target, pl);
    }
  }
  
  static public void refreshAllSpectatorVisibilities() {
    for (Player pl : Bukkit.getOnlinePlayers()) {
      refreshAllSpectatorVisibilitiesFor(pl);
    }
  }
  
  /** Set the player as a spectator */
  static public void refreshSpectatorAbilities(Player pl, boolean state) {
    if (!DTC.game.isPlaying) state = false;
    
    boolean creativeAbility = state ||
      List.of(
        GameMode.CREATIVE,
        GameMode.SPECTATOR
      ).contains(pl.getGameMode());
    
    pl.setInvisible(state);
    pl.setInvulnerable(state);
    pl.setAllowFlight(creativeAbility);
    
    ItemStack teleporterItem = DTC.itemsManager.gens.get(
      ItemsManager.ItemKey.SPECTATOR_TELEPORTER
    ).getItem();
    
    if (state) {
      DTC.inventoriesManager.store(pl);
      pl.getInventory().setItem(4, teleporterItem);
    }
    else {
      pl.getInventory().remove(Material.ENDER_EYE);
      DTC.inventoriesManager.restore(pl);
    }
  }
  
  static public void refreshSpectatorAbilities(Player pl) {
    refreshSpectatorAbilities(
      pl,
      DTC.game.getPlayerData(pl).side.equals(Game.Side.SPECTATOR)
    );
  }
  
  /** Apply potion effect, level is 1-based */
  static public void addEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level,
    boolean beacon, boolean particles
  ) {
    if (level <= 0) return;
    
    pl.addPotionEffect(
      new PotionEffect(type, ticks, level - 1, beacon, particles)
    );
  }
  
  static public void addEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level
  ) {
    addEffect(pl, type, ticks, level, false, true);
  }
  
  static public void addPassiveEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level
  ) {
    addEffect(pl, type, ticks, level, true, false);
  }
  
  static public void setEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level,
    boolean beacon, boolean particles
  ) {
    pl.removePotionEffect(type);
    addEffect(pl, type, ticks, level - 1, beacon, particles);
  }
  
  static public void setEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level
  ) {
    setEffect(pl, type, ticks, level, false, true);
  }
  
  static public void extendEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level,
    boolean beacon, boolean particles
  ) {
    int duration = ticks;
    if (pl.hasPotionEffect(type))
      duration += pl.getPotionEffect(type).getDuration();
    setEffect(pl, type, duration, level, beacon, particles);
  }
  
  static public void extendEffect(
    LivingEntity pl, PotionEffectType type, int ticks, int level
  ) {
    extendEffect(pl, type, ticks, level, false, true);
  }
  
  static public void glow(LivingEntity pl, int ticks) {
    addEffect(pl, PotionEffectType.GLOWING, ticks, 1, true, false);
  }
  
  /** Reduce respawn time process */
  static public void rrt(Player pl) {
    PlayerData d = DTC.game.getPlayerData(pl);
    if (
      pl.isSneaking() &&
        LocUtils.near(
          Pos.of(pl),
          LocUtils.selfSide(DTC.game.map.core, d.side),
          5
        )
    ) {
      if (d.respawnTime <= PlayerData.minRespawnTime) {
        pl.sendActionBar(TextUtils.$("game.reduce-respawn-time.no-more"));
        return;
      }
      
      int xp = d.role.id == RolesManager.RoleKey.GUARD ? 6 : 2;
      
      if (d.rrtProgress == -20) {
        pl.playSound(
          pl.getLocation(),
          Sound.BLOCK_PISTON_EXTEND,
          1F, // Volume
          1.5f // Pitch
        );
      }
      
      if (d.rrtProgress >= PlayerData.rrtDuration) {
        d.rrtProgress = 0;
        
        pl.giveExp(xp);
        d.addRespawnTime(-1);
        
        DTC.boardsManager.refresh(pl);
        
        pl.sendActionBar(TextUtils.$("game.reduce-respawn-time.done"));
        
        if (d.role.id == RolesManager.RoleKey.HACKER) {
          give(pl, Material.IRON_INGOT);
        }
        
        pl.playSound(
          pl.getLocation(),
          Sound.BLOCK_BEACON_POWER_SELECT,
          1, // Volume
          1.5f // Pitch
        );
        new ParticleBuilder(Particle.HAPPY_VILLAGER)
          .location(pl.getEyeLocation())
          .offset(0.2, 0.2, 0.2)
          .count(3)
          .spawn();
      }
      else if (d.rrtProgress >= 0 && d.rrtProgress % 20 == 0) {
        pl.giveExp(xp);
        
        pl.sendActionBar(
          TextUtils.$(
            "game.reduce-respawn-time.progress",
            List.of(
              Placeholder.component(
                "progress",
                Component.text(d.rrtProgress / 20)
              )
            )
          )
        );
        pl.playSound(
          pl.getLocation(),
          Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF,
          0.5F, // Volume
          1.5f // Pitch
        );
      }
      
      d.rrtProgress++;
    }
    else {
      if (d.rrtProgress > -20) {
        d.rrtProgress = -20;
        pl.playSound(
          pl.getLocation(),
          Sound.BLOCK_PISTON_EXTEND,
          1F, // Volume
          1.5f // Pitch
        );
      }
    }
  }
  
  static public void respawn(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    data.revive();
    
    refreshSpectatorAbilities(pl);
    refreshAllSpectatorVisibilities();
    if (data.side.equals(Game.Side.SPECTATOR)) return;
    
    pl.setCooldown(
      Material.KNOWLEDGE_BOOK,
      Math.max(
        pl.getCooldown(Material.KNOWLEDGE_BOOK) - data.extraSkillReload,
        0
      ) / 2
    );
    data.extraSkillReload = 0;
    
    DTC.inventoriesManager.restore(pl);
    CoreUtils.setTickOut(() -> giveEssentials(pl));
    
    pl.setGameMode(GameMode.SURVIVAL);
    fullyHeal(pl);
    teleportToSpawnPoint(pl);
  }
  
  static public void scheduleRespawn(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    
    DTC.quizManager.start(pl);
    
    new BukkitRunnable() {
      int waitTicks = data.respawnTime * 20;
      
      @Override
      public void run() {
        if (!pl.isOnline()) {
          cancel();
          return;
        }
        
        if (data.alive) {
          DTC.quizManager.discard(pl);
          
          cancel();
          return;
        }
        
        boolean updateTitle = waitTicks % 20 == 0;
        
        if (DTC.quizManager.isEnded(pl)) {
          updateTitle = true;
          
          if (DTC.quizManager.isCorrect(pl)) {
            waitTicks -= 10 * 20;
          }
          
          if (data.quizQuota <= 0) {
            DTC.quizManager.discard(pl);
          }
          else if (waitTicks > 0) {
            DTC.quizManager.start(pl);
          }
        }
        
        if (waitTicks <= 0) {
          respawn(pl);
          DTC.quizManager.discard(pl);
          
          normalTitleTimes(pl);
          pl.sendTitlePart(TitlePart.TITLE, TextUtils.$("player.respawned"));
          pl.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
          
          cancel();
          return;
        }
        
        if (updateTitle) {
          int secs = waitTicks / 20;
          
          data.respawnTime = secs;
          DTC.game.enforceRTScore(pl);
          
          if (waitTicks <= 60) {
            normalTitleTimes(pl);
            pl.sendTitlePart(
              TitlePart.TITLE,
              Component.text(secs).color(NamedTextColor.GOLD)
            );
            pl.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
          }
          else {
            normalTitleTimes(pl);
            pl.sendTitlePart(TitlePart.TITLE, Component.empty());
            pl.sendTitlePart(
              TitlePart.SUBTITLE,
              TextUtils.$(
                "player.respawning",
                List.of(
                  Placeholder.component(
                    "seconds",
                    secs > 60 ? CoreUtils.formatTimeComp(
                      secs,
                      NamedTextColor.GOLD
                    )
                      : Component.text(secs).color(
                        NamedTextColor.GOLD
                      )
                  )
                )
              )
            );
          }
        }
        
        waitTicks--;
        if (data.role.id.equals(RolesManager.RoleKey.ATTACKER)) {
          waitTicks--;
        }
      }
    }.runTaskTimer(DTC.instance, 0, 1);
  }
  
  static public boolean isTeammate(Player a, Player b) {
    return DTC.game.getPlayerData(a).side == DTC.game
      .getPlayerData(b).side;
  }
  
  /** Drop an item at spawnpoint */
  static public void dropAtSpawn(Game.Side side, ItemStack item) {
    DTC.worldsManager.live.dropItemNaturally(
      LocUtils.live(
        LocUtils.selfSide(
          LocUtils.toSpawnPoint(
            RandomUtils.pick(DTC.game.map.spawnpoints)
          ),
          side
        )
      ),
      item
    ).setPickupDelay(20);
  }
  
  static public void dropAtSpawn(Player pl, ItemStack item) {
    dropAtSpawn(DTC.game.getPlayerData(pl).side, item);
  }
  
  /** Give a player an item, or send to their spawn if they're dead */
  static public void give(Player pl, ItemStack item) {
    if (item == null || item.isEmpty()) return;
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    if (!DTC.game.isPlaying || data.alive) {
      pl.give(item);
    }
    else {
      dropAtSpawn(data.side, item);
      
      pl.sendActionBar(TextUtils.$("player.item-sent-to-spawn"));
    }
  }
  
  static public void give(Player pl, Material type, int count) {
    int maxCount = type.getMaxStackSize();
    if (count > maxCount * 9) {
      count = maxCount * 9;
    }
    
    if (count > maxCount) {
      give(pl, type, maxCount);
      give(pl, type, count - maxCount);
      return;
    }
    
    give(pl, new ItemStack(type, count));
  }
  
  static public void give(Player pl, Material type) {
    give(pl, type, 1);
  }
  
  static public void give(Player pl, ItemsManager.ItemKey key, int count) {
    ItemGen gen = DTC.itemsManager.gens.get(key);
    
    int maxCount = gen.iconType.getMaxStackSize();
    if (count > maxCount * 9) {
      count = maxCount * 9;
    }
    
    if (count > maxCount) {
      give(pl, key, maxCount);
      give(pl, key, count - maxCount);
      return;
    }
    
    give(pl, gen.getItem(count));
  }
  
  static public void give(Player pl, ItemsManager.ItemKey key) {
    give(pl, key, 1);
  }
  
  static final Pattern weaponSuffix = Pattern.compile(
    "_(sword|axe)",
    Pattern.CASE_INSENSITIVE
  );
  
  /** Give a player basic armors, weapons & skill books */
  static public void giveEssentials(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    PlayerInventory inv = pl.getInventory();
    
    Consumer<EquipmentSlot> defaultEquipment = slot -> {
      ItemsManager.ItemKey key = ItemsManager.ItemKey.PLACEHOLDER;
      switch (slot) {
        case HEAD -> key = data.role.defHelmet();
        case CHEST -> key = data.role.defChestplate();
        case LEGS -> key = data.role.defLeggings();
        case FEET -> key = data.role.defBoots();
      }
      ItemStack item = DTC.itemsManager.gens.get(key).getItem();
      
      if (key.name().startsWith("STARTER")) {
        CoreUtils.dyeTeamColor(item, data.side);
      }
      
      inv.setItem(slot, item);
    };
    
    for (EquipmentSlot slot : new EquipmentSlot[]{
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
    }) {
      ItemStack item = inv.getItem(slot);
      if (item.isEmpty() && data.role.id != RolesManager.RoleKey.ASSASSIN) {
        defaultEquipment.accept(slot);
      }
    }
    
    Predicate<ItemStack> isWeapon = item -> weaponSuffix.matcher(
      item.getType().name()
    ).find();
    
    boolean hasFood = false;
    boolean hasAnyWeapon = false;
    boolean hasRoleItem = false;
    boolean hasPickaxe = false;
    
    ItemStack roleItem = data.role.getExclusiveItem();
    
    if (!isWeapon.test(roleItem)) {
      for (ItemStack item : inv.getContents()) {
        if (item == null) continue;
        
        if (isWeapon.test(item)) {
          hasAnyWeapon = true;
          break;
        }
      }
      
      if (!hasAnyWeapon) {
        give(pl, ItemsManager.ItemKey.STARTER_SWORD);
      }
    }
    
    for (ItemStack item : inv.getContents()) {
      if (item == null) continue;
      
      if (DTC.rolesManager.isExclusiveItem(item)) {
        hasRoleItem = true;
        break;
      }
    }
    
    if (!hasRoleItem) {
      if (
        roleItem.getType() == Material.SHIELD &&
          inv.getItemInOffHand()
            .isEmpty()
      ) {
        inv.setItemInOffHand(roleItem);
      }
      else {
        give(pl, roleItem);
      }
    }
    
    for (ItemStack item : inv.getContents()) {
      if (item != null && item.getType().isEdible()) {
        hasFood = true;
        break;
      }
    }
    
    if (!hasFood) give(pl, Material.BREAD, 8);
    
    if (!inv.contains(Material.KNOWLEDGE_BOOK)) {
      give(pl, data.role.getSkillItem());
    }
    
    for (ItemStack item : inv.getContents()) {
      if (item != null && item.getType() == Material.GOLDEN_PICKAXE) {
        hasPickaxe = true;
        break;
      }
    }
    
    if (!hasPickaxe) give(pl, Material.GOLDEN_PICKAXE);
  }
  
  static public boolean banBothHandItem(Player pl, Material type) {
    boolean found = false;
    
    for (EquipmentSlot slot : new EquipmentSlot[]{
      EquipmentSlot.HAND, EquipmentSlot.OFF_HAND
    }) {
      ItemStack item = pl.getInventory().getItem(slot);
      
      if (item.getType().equals(type)) {
        pl.getInventory().setItem(slot, ItemStack.empty());
        pl.getWorld().dropItemNaturally(
          LocUtils.hitboxCenter(pl),
          item
        ).setPickupDelay(20);
        
        found = true;
      }
    }
    
    return found;
  }
  
  static public void growNearbyCrops(Player pl) {
    if (!LocUtils.inLive(pl)) return;
    
    final int radius = 6, outerRadius = radius + 1;
    
    for (int x = -outerRadius; x <= outerRadius; x++) {
      for (int y = -outerRadius; y <= outerRadius; y++) {
        for (int z = -outerRadius; z <= outerRadius; z++) {
          Block block = pl.getLocation().add(x, y, z).getBlock();
          Location centerLoc = block.getLocation().add(0.5, 0.1, 0.5);
          if (pl.getLocation().distanceSquared(centerLoc) > radius * radius)
            continue;
          
          if (!(block.getBlockData() instanceof Ageable ageable)) continue;
          if (ageable.getAge() >= ageable.getMaximumAge()) continue;
          
          if (RandomUtils.range(10) < 2) {
            ageable.setAge(ageable.getAge() + 1);
            block.setBlockData(ageable);
            
            new ParticleBuilder(Particle.HAPPY_VILLAGER)
              .allPlayers()
              .location(centerLoc)
              .offset(0.4, 0.2, 0.4)
              .extra(0)
              .count(5)
              .spawn();
            
            pl.giveExp(RandomUtils.range(1, 4));
          }
        }
      }
    }
  }
  
  /** Use a particle trial to delay assign a task between 2 players */
  static public void delayAssign(
    Player from, Player to, Particle particle, Runnable task
  ) {
    new BukkitRunnable() {
      int duration = 60;
      Location pos = LocUtils.hitboxCenter(from);
      
      void apply() {
        task.run();
        cancel();
      }
      
      @Override
      public void run() {
        if (!to.isOnline()) {
          cancel();
          return;
        }
        if (!LocUtils.isSameWorld(from, to)) {
          apply();
          return;
        }
        
        duration--;
        if (duration < 0) {
          apply();
          return;
        }
        
        Vector vel = LocUtils.hitboxCenter(to).subtract(pos).toVector();
        double dist = vel.length();
        
        if (dist < 0.25) {
          apply();
          return;
        }
        
        if (dist < 3) vel.multiply(3 / dist);
        if (dist > 120) vel.multiply(120 / dist);
        
        vel.multiply(0.2);
        
        if (vel.length() >= dist) {
          apply();
          return;
        }
        
        pos.add(vel);
        
        if (
          !to.isInvisible() &&
            !to.hasPotionEffect(
              PotionEffectType.INVISIBILITY
            )
        ) {
          new ParticleBuilder(particle)
            .allPlayers()
            .location(pos)
            .extra(0)
            .spawn();
        }
      }
    }.runTaskTimer(DTC.instance, 0, 1);
  }
  
  static public List<Player> getTeammates(Game.Side side) {
    return all().stream().filter(p -> {
      PlayerData data = DTC.game.getPlayerData(p);
      return data.alive && data.side.equals(side);
    }).toList();
  }
  
  static public List<Player> getTeammates(Player pl) {
    return getTeammates(DTC.game.getPlayerData(pl).side);
  }
  
  static public List<Player> getEnemies(Game.Side side) {
    return getTeammates(side.opposite());
  }
  
  static public List<Player> getEnemies(Player pl) {
    return getEnemies(DTC.game.getPlayerData(pl).side);
  }
  
  /** Including teammates & spectators */
  static public List<Player> getNonEnemies(Game.Side side) {
    return all().stream().filter(p -> {
      PlayerData data = DTC.game.getPlayerData(p);
      return (!shouldHandle(p) || (!data.side.equals(side.opposite())));
    }).toList();
  }
  
  static public List<Player> getNonEnemies(Player pl) {
    return getNonEnemies(DTC.game.getPlayerData(pl).side);
  }
  
  static public Player getTargetPlayer(Player pl, double maxDistance) {
    Location eyeLocation = pl.getEyeLocation();
    Vector origin = eyeLocation.toVector();
    Vector direction = eyeLocation.getDirection().normalize();
    
    Player target = null;
    double closest = maxDistance;
    
    for (Player other : getEnemies(pl)) {
      if (
        other.equals(pl) ||
          !LocUtils.isSameWorld(
            other,
            pl
          ) ||
          !pl.hasLineOfSight(other)
      ) continue;
      
      BoundingBox box = other.getBoundingBox().clone().expand(0.2, 0.2, 0.2);
      
      Double hit = rayIntersectsBox(origin, direction, maxDistance, box);
      if (hit != null && hit < closest) {
        closest = hit;
        target = other;
      }
    }
    
    return target;
  }
  
  /** Pure math, by Gemini */
  static Double rayIntersectsBox(
    Vector origin, Vector dir, double maxDist, BoundingBox box
  ) {
    double tmin = 0.0;
    double tmax = maxDist;
    
    double[] mins = {
      box.getMinX(), box.getMinY(), box.getMinZ()
    };
    double[] maxs = {
      box.getMaxX(), box.getMaxY(), box.getMaxZ()
    };
    double[] origins = {
      origin.getX(), origin.getY(), origin.getZ()
    };
    double[] dirs = {
      dir.getX(), dir.getY(), dir.getZ()
    };
    for (int i = 0; i < 3; i++) {
      double min = mins[i], max = maxs[i], o = origins[i], d = dirs[i];
      
      if (Math.abs(d) < 1e-8) {
        if (o < min || o > max) return null;
      }
      else {
        double t1 = (min - o) / d;
        double t2 = (max - o) / d;
        if (t1 > t2) {
          double tmp = t1;
          t1 = t2;
          t2 = tmp;
        }
        if (t1 > tmin) tmin = t1;
        if (t2 < tmax) tmax = t2;
        if (tmin > tmax) return null;
      }
    }
    return tmin < 0 ? tmax : tmin;
  }
}
