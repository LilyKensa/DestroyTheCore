package dev.huey.destroyTheCore.utils;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PlayerUtils {
  static public Player getPlayerByEntityId(int id) {
    return Bukkit.getOnlinePlayers().stream()
      .filter(p -> p.getEntityId() == id)
      .findAny().orElse(null);
  }
  
  static public void send(Player pl, Component component) {
    pl.sendMessage(
      component.colorIfAbsent(NamedTextColor.GRAY)
    );
  }
  static public void send(Player pl, String text, TextColor color) {
    send(pl, Component.text(text).color(color));
  }
  static public void send(Player pl, String text) {
    send(pl, text, NamedTextColor.GRAY);
  }
  
  /** {@link #send} with {@link DestroyTheCore#prefix} */
  static public void prefixedSend(Player pl, Component component) {
    send(pl, DestroyTheCore.prefix.append(component));
  }
  static public void prefixedSend(Player pl, String text, TextColor color) {
    prefixedSend(pl, Component.text(text).color(color));
  }
  static public void prefixedSend(Player pl, String text) {
    prefixedSend(pl, text, NamedTextColor.GRAY);
  }
  
  /** Broadcast to admins */
  static public void prefixedNotice(Component comp) {
    for (Player p : Bukkit.getOnlinePlayers())
      if (PlayerUtils.isAdmin(p))
        PlayerUtils.prefixedSend(p, comp);
  }
  
  /** Broadcast to everyone */
  static public void broadcast(Component comp) {
    for (Player pl : Bukkit.getOnlinePlayers())
      send(pl, comp);
  }
  
  /** Broadcast to nearby players */
  static public void auraBroadcast(Location center, double dist, Component comp) {
    for (Player pl : Bukkit.getOnlinePlayers())
      if (LocationUtils.near(pl.getLocation(), center, dist))
        send(pl, comp);
  }
  
  /** {@link #broadcast} with {@link DestroyTheCore#prefix} */
  static public void prefixedBroadcast(Component comp) {
    for (Player pl : Bukkit.getOnlinePlayers())
      prefixedSend(pl, comp);
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
    return all().stream()
      .filter(p -> DestroyTheCore.game.getPlayerData(p).isGaming())
      .toList();
  }
  
  static public Component getName(Player pl) {
    Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(pl);
    
    TextComponent.Builder builder = Component.text();
    if (team != null) builder.append(team.prefix());
    builder.append(pl.displayName());
    if (team != null) builder.append(team.suffix());
    if (team != null) builder.color(team.color());
    
    return builder.build();
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
  
  /** If that block has right click functionalities, and shouldn't be canceled */
  static public boolean checkUsingBlock(Player pl, Block block) {
    return block != null && block.getBlockData() instanceof Openable && !pl.isSneaking();
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
      pl.sendActionBar(TextUtils.$("player.in-cooldown", List.of(
        Placeholder.unparsed("value", CoreUtils.toFixed(cooldown / 20D, 1))
      )));
      return false;
    }
    
    return true;
  }
  static public boolean checkHandCooldown(Player pl) {
    return checkHandCooldown(pl, 0);
  }
  
  static public void setGroupCooldown(Player pl, List<ItemsManager.ItemKey> keys, int ticks) {
    for (ItemsManager.ItemKey key : keys) {
      pl.setCooldown(
        DestroyTheCore.itemsManager.gens.get(key)
          .getItem().getType(),
        ticks
      );
    }
  }
  
  static public int getGroupCooldown(Player pl, List<ItemsManager.ItemKey> keys) {
    return pl.getCooldown(
      DestroyTheCore.itemsManager.gens.get(keys.getFirst())
        .getItem().getType()
    );
  }
  
  static public boolean checkGroupCooldown(Player pl, List<ItemsManager.ItemKey> keys) {
    if (!shouldHandle(pl)) return true;
    
    int cooldown = getGroupCooldown(pl, keys);
    if (cooldown > 0) {
      pl.sendActionBar(TextUtils.$("player.group-in-cooldown", List.of(
        Placeholder.unparsed("value", CoreUtils.toFixed(cooldown / 20D, 1))
      )));
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
        RandomUtils.hit(1D / (meta.getEnchantLevel(Enchantment.UNBREAKING) + 1))
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
  
  static public void kickAntiCheat(Player pl, String path) {
    pl.kick(
      TextUtils.$("anti-cheat.prefix")
        .append(TextUtils.$("anti-cheat." + path))
    );
  }
  
  static public boolean inLobby(Player pl) {
    if (DestroyTheCore.worldsManager.lobby == null) return false;
    
    return LocationUtils.isSameWorld(
      pl.getWorld(),
      DestroyTheCore.worldsManager.lobby
    );
  }
  
  static public void backToLobby(Player pl) {
    if (DestroyTheCore.game.lobby.spawn == null) return;
    
    pl.setGameMode(GameMode.SURVIVAL);
    fullyHeal(pl);
    pl.teleport(DestroyTheCore.game.lobby.spawn);
  }
  
  static public void teleportToRestArea(Player pl) {
    if (DestroyTheCore.game.map.restArea == null) return;
    
    if (DestroyTheCore.game.getPlayerData(pl).side.equals(Game.Side.SPECTATOR)) {
      Location centerLoc = LocationUtils.live(DestroyTheCore.game.map.restArea);
      centerLoc.setYaw(0);
      centerLoc.setX(0);
      pl.teleport(centerLoc);
    }
    else {
      pl.teleport(LocationUtils.selfSide(
        LocationUtils.live(DestroyTheCore.game.map.restArea),
        pl
      ));
    }
  }
  
  static public void teleportToSpawnPoint(Player pl) {
    pl.teleport(
      LocationUtils.live(
        LocationUtils.selfSide(
          LocationUtils.toSpawnPoint(
            RandomUtils.pick(DestroyTheCore.game.map.spawnpoints)
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
    pl.setHealth(20);
    pl.setFireTicks(0);
    pl.setFreezeTicks(0);
    pl.setFallDistance(0);
  }
  
  /** Grant invulnerabilities */
  static public void protect(Player pl, int ticks) {
    pl.addPotionEffect(new PotionEffect(
      PotionEffectType.RESISTANCE,
      ticks,
      9,
      true,
      false
    ));
    pl.addPotionEffect(new PotionEffect(
      PotionEffectType.FIRE_RESISTANCE,
      ticks,
      9,
      true,
      false
    ));
  }
  
  /** Refresh night vision effect based on their preference */
  static public void enforceNightVision(Player pl) {
    if (DestroyTheCore.game.stats.get(pl.getUniqueId()).nightVision) {
      pl.addPotionEffect(new PotionEffect(
        PotionEffectType.NIGHT_VISION,
        PotionEffect.INFINITE_DURATION,
        0,
        true,
        false
      ));
    }
    else {
      pl.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
  }
  
  /** Remove spectators from {@code viewer}'s pov world */
  static public void hideSpectators(Player viewer) {
    for (Player s : Bukkit.getOnlinePlayers()) {
      if (DestroyTheCore.game.getPlayerData(s).side == Game.Side.SPECTATOR) {
        viewer.hidePlayer(DestroyTheCore.instance, s);
      }
      else {
        viewer.showPlayer(DestroyTheCore.instance, s);
      }
    }
  }
  static public void hideSpectators() {
    if (!DestroyTheCore.game.isPlaying) {
      showAllPlayers();
      return;
    }
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      hideSpectators(p);
    }
  }
  
  /** Add spectators back from {@code viewer}'s pov world */
  static public void showAllPlayers(Player viewer) {
    for (Player s : Bukkit.getOnlinePlayers()) {
      viewer.showPlayer(DestroyTheCore.instance, s);
    }
  }
  static public void showAllPlayers() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      showAllPlayers(p);
    }
  }
  
  /** Set the player as a spectator */
  static public void refreshSpectatorAbilities(Player pl, boolean state) {
    if (!DestroyTheCore.game.isPlaying) state = false;
    
    boolean creativeAbility = state ||
      List.of(GameMode.CREATIVE, GameMode.SPECTATOR).contains(pl.getGameMode());
      
    pl.setInvisible(state);
    pl.setInvulnerable(creativeAbility);
    pl.setAllowFlight(creativeAbility);
    
    ItemStack teleporterItem = DestroyTheCore.itemsManager.gens
      .get(ItemsManager.ItemKey.SPECTATOR_TELEPORTER)
      .getItem();
    
    if (state) {
      DestroyTheCore.inventoriesManager.store(pl);
      pl.getInventory().setItem(4, teleporterItem);
    }
    else {
      pl.getInventory().remove(Material.ENDER_EYE);
      DestroyTheCore.inventoriesManager.restore(pl);
    }
  }
  static public void refreshSpectatorAbilities(Player pl) {
    refreshSpectatorAbilities(
      pl,
      DestroyTheCore.game.getPlayerData(pl).side.equals(Game.Side.SPECTATOR)
    );
  }
  
  /** Reduce respawn time process */
  static public void rrt(Player pl) {
    PlayerData d = DestroyTheCore.game.getPlayerData(pl);
    if (
      pl.isSneaking() &&
        LocationUtils.near(
          pl.getLocation(),
          LocationUtils.live(LocationUtils.selfSide(DestroyTheCore.game.map.core, d.side)),
          3
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
        
        DestroyTheCore.boardsManager.refresh(pl);
        
        pl.sendActionBar(TextUtils.$("game.reduce-respawn-time.done"));
        
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
        
        pl.sendActionBar(TextUtils.$("game.reduce-respawn-time.progress", List.of(
          Placeholder.component("progress", Component.text(d.rrtProgress / 20 + 1))
        )));
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
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    data.revive();
    
    refreshSpectatorAbilities(pl);
    if (data.side.equals(Game.Side.SPECTATOR)) return;
    
    pl.setCooldown(Material.KNOWLEDGE_BOOK, 0);
    DestroyTheCore.inventoriesManager.restore(pl);
    CoreUtils.setTickOut(() -> giveEssentials(pl));
    
    pl.setGameMode(GameMode.SURVIVAL);
    fullyHeal(pl);
    protect(pl, 400);
    teleportToSpawnPoint(pl);
  }
  
  static public void scheduleRespawn(Player pl) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    DestroyTheCore.quizManager.start(pl);
    
    new BukkitRunnable() {
      int waitTicks = data.respawnTime * 20;
      
      @Override
      public void run() {
        if (data.alive) {
          DestroyTheCore.quizManager.discard(pl);
          
          cancel();
          return;
        }
        
        boolean updateTitle = waitTicks % 20 == 0;
        
        if (DestroyTheCore.quizManager.isEnded(pl)) {
          updateTitle = true;
          
          if (DestroyTheCore.quizManager.isCorrect(pl))
            waitTicks -= 10 * 20;
          
          if (waitTicks > 0)
            DestroyTheCore.quizManager.start(pl);
        }
        
        if (waitTicks <= 0) {
          respawn(pl);
          DestroyTheCore.quizManager.discard(pl);
          
          PlayerUtils.normalTitleTimes(pl);
          pl.sendTitlePart(
            TitlePart.TITLE,
            TextUtils.$("player.respawned")
          );
          pl.sendTitlePart(
            TitlePart.SUBTITLE,
            Component.empty()
          );
          
          cancel();
          return;
        }
        
        if (updateTitle) {
          if (waitTicks <= 60) {
            PlayerUtils.normalTitleTimes(pl);
            pl.sendTitlePart(
              TitlePart.TITLE,
              Component.text(waitTicks / 20).color(NamedTextColor.GOLD)
            );
            pl.sendTitlePart(
              TitlePart.SUBTITLE,
              Component.empty()
            );
          }
          else {
            int secs = waitTicks / 20;
            PlayerUtils.normalTitleTimes(pl);
            pl.sendTitlePart(
              TitlePart.TITLE,
              Component.empty()
            );
            pl.sendTitlePart(
              TitlePart.SUBTITLE,
              TextUtils.$("player.respawning", List.of(
                Placeholder.component(
                  "seconds",
                  secs > 60
                    ? CoreUtils.formatTimeComp(secs, NamedTextColor.GOLD)
                    : Component.text(secs).color(NamedTextColor.GOLD)
                )
              ))
            );
          }
        }
        
        waitTicks--;
        if (data.role.id.equals(RolesManager.RoleKey.ATTACKER)) {
          waitTicks--;
        }
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 1);
  }
  
  /** Safely give a player an item, will not crash if item's empty */
  public static void give(Player pl, ItemStack item) {
    if (item == null || item.isEmpty()) return;
    pl.give(item);
  }
  
  /** Give a player basic armors, weapons & skill books */
  public static void giveEssentials(Player pl) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    PlayerInventory inv = pl.getInventory();
    
    Consumer<EquipmentSlot> defaultEquipment = slot -> {
      ItemsManager.ItemKey key = ItemsManager.ItemKey.PLACEHOLDER;
      switch (slot) {
        case HEAD -> key = data.role.defHelmet();
        case CHEST -> key = data.role.defChestplate();
        case LEGS -> key = data.role.defLeggings();
        case FEET -> key = data.role.defBoots();
      }
      ItemStack item = DestroyTheCore.itemsManager.gens.get(key).getItem();
      
      if (key.name().startsWith("STARTER"))
        item.editMeta(uncastedMeta -> {
          LeatherArmorMeta meta = (LeatherArmorMeta) uncastedMeta;
          
          meta.setColor(data.side.dyeColor);
          meta.addItemFlags(ItemFlag.HIDE_DYE);
        });
      
      inv.setItem(slot, item);
    };
    
    for (EquipmentSlot slot : new EquipmentSlot[] {
      EquipmentSlot.HEAD,
      EquipmentSlot.CHEST,
      EquipmentSlot.LEGS,
      EquipmentSlot.FEET,
    }) {
      ItemStack item = inv.getItem(slot);
      if (item.isEmpty() && data.role.id != RolesManager.RoleKey.ASSASSIN) {
        defaultEquipment.accept(slot);
      }
    }
    
    Predicate<ItemStack> isWeapon = item ->
      Pattern.compile("_(sword|axe)", Pattern.CASE_INSENSITIVE)
        .matcher(item.getType().name()).find();
    
    boolean hasFood = false, hasAnyWeapon = false, hasRoleItem = false;
    
    ItemStack roleItem = data.role.getExclusiveItem();
    
    if (!isWeapon.test(roleItem)) {
      for (ItemStack item : inv.getContents()) {
        if (item == null) continue;
        
        if (isWeapon.test(item)) {
          hasAnyWeapon = true;
          break;
        }
      }
      
      if (!hasAnyWeapon)
        pl.give(
          DestroyTheCore.itemsManager.gens
            .get(ItemsManager.ItemKey.STARTER_SWORD).getItem()
        );
    }
    
    for (ItemStack item : inv.getContents()) {
      if (item == null) continue;
      
      if (DestroyTheCore.rolesManager.isExclusiveItem(item)) {
        hasRoleItem = true;
        break;
      }
    }
    
    if (!hasRoleItem)
      give(pl, roleItem);
    
    for (ItemStack item : inv.getContents()) {
      if (item != null && item.getType().isEdible()) {
        hasFood = true;
        break;
      }
    }
    
    if (!hasFood)
      give(pl, new ItemStack(Material.BREAD, 8));
    
    if (!inv.contains(Material.KNOWLEDGE_BOOK)) {
      pl.give(data.role.getSkillItem());
    }
  }
  
  /** Use a particle trial to delay assign a task between 2 players */
  public static void delayAssign(
    Player from,
    Player to,
    Particle particle,
    Runnable task
  ) {
    new BukkitRunnable() {
      int duration = 100;
      Location pos = LocationUtils.hitboxCenter(from);
      
      @Override
      public void run() {
        if (!to.isOnline()) {
          cancel();
          return;
        }
        if (!LocationUtils.isSameWorld(from, to)) {
          task.run();
          cancel();
          return;
        }
        
        duration--;
        if (duration < 0) {
          task.run();
          cancel();
          return;
        }
        
        Vector offset = LocationUtils.hitboxCenter(to).subtract(pos).toVector();
        double dist = offset.length();
        
        if (dist < 0.3) {
          task.run();
          cancel();
          return;
        }
        
        if (dist < 5) offset = offset.multiply(5 / dist);
        if (dist > 20) offset = offset.multiply(20 / dist);
        
        pos.add(offset.multiply(0.1));
        
        new ParticleBuilder(particle)
          .allPlayers()
          .location(pos)
          .extra(0)
          .spawn();
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 1);
  }
  
  public static List<Player> getTeammates(Game.Side side) {
    return all().stream()
      .filter(p -> {
        PlayerData data = DestroyTheCore.game.getPlayerData(p);
        return data.alive && data.side.equals(side);
      })
      .toList();
  }
  public static List<Player> getTeammates(Player pl) {
    return getTeammates(DestroyTheCore.game.getPlayerData(pl).side);
  }
  
  public static List<Player> getEnemies(Game.Side side) {
    return getTeammates(side.opposite());
  }
  public static List<Player> getEnemies(Player pl) {
    return getEnemies(DestroyTheCore.game.getPlayerData(pl).side);
  }
  
  /** Including teammates & spectators */
  public static List<Player> getNonEnemies(Game.Side side) {
    return all().stream()
      .filter(p -> {
        PlayerData data = DestroyTheCore.game.getPlayerData(p);
        return !shouldHandle(p) || (
          !data.side.equals(side.opposite())
        );
      })
      .toList();
  }
  public static List<Player> getNonEnemies(Player pl) {
    return getNonEnemies(DestroyTheCore.game.getPlayerData(pl).side);
  }
  
  public static Player getTargetPlayer(Player pl, double maxDistance) {
    Location eyeLocation = pl.getEyeLocation();
    Vector origin = eyeLocation.toVector();
    Vector direction = eyeLocation.getDirection().normalize();
    
    Player target = null;
    double closest = maxDistance;
    
    for (Player other : getEnemies(pl)) {
      if (
        other.equals(pl) ||
          !LocationUtils.isSameWorld(other, pl) ||
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
    
    double[] mins = {box.getMinX(), box.getMinY(), box.getMinZ()};
    double[] maxs = {box.getMaxX(), box.getMaxY(), box.getMaxZ()};
    double[] origins = {origin.getX(), origin.getY(), origin.getZ()};
    double[] dirs = {dir.getX(), dir.getY(), dir.getZ()};
    for (int i = 0; i < 3; i++) {
      double min = mins[i], max = maxs[i], o = origins[i], d = dirs[i];
      
      if (Math.abs(d) < 1e-8) {
        if (o < min || o > max) return null;
      }
      else {
        double t1 = (min - o) / d;
        double t2 = (max - o) / d;
        if (t1 > t2) {
          double tmp = t1; t1 = t2; t2 = tmp;
        }
        if (t1 > tmin) tmin = t1;
        if (t2 < tmax) tmax = t2;
        if (tmin > tmax) return null;
      }
    }
    return tmin < 0 ? tmax : tmin;
  }
}
