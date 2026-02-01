package dev.huey.destroyTheCore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.managers.TicksManager;
import dev.huey.destroyTheCore.missions.InfiniteOresMission;
import dev.huey.destroyTheCore.records.*;
import dev.huey.destroyTheCore.roles.KekkaiMasterRole;
import dev.huey.destroyTheCore.utils.*;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import it.unimi.dsi.fastutil.Pair;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

public class Game {
  public boolean isPlaying = false;
  
  static public class LobbyPos implements ConfigurationSerializable {
    public Pos spawn = null;
    public Pos startButton = null;
    public Region joinRed = null;
    public Region joinGreen = null;
    public Region joinSpectator = null;
    
    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap<>();
      
      BiConsumer<String, Object> pusher = (key, value) -> {
        if (value != null) map.put(key, value);
      };
      
      pusher.accept("spawn", spawn);
      pusher.accept("start-button", startButton);
      pusher.accept("join-red", joinRed);
      pusher.accept("join-green", joinGreen);
      pusher.accept("join-spectator", joinSpectator);
      
      return map;
    }
    
    static public LobbyPos deserialize(Map<String, Object> map) {
      LobbyPos lp = new LobbyPos();
      
      lp.spawn = (Pos) map.getOrDefault("spawn", null);
      lp.startButton = (Pos) map.getOrDefault("start-button", null);
      lp.joinRed = (Region) map.getOrDefault("join-red", null);
      lp.joinGreen = (Region) map.getOrDefault("join-green", null);
      lp.joinSpectator = (Region) map.getOrDefault("join-spectator", null);
      
      return lp;
    }
  }
  
  static public class MapPos implements ConfigurationSerializable {
    public Pos restArea = null;
    public Pos core = null;
    public Pos mission = null;
    public Set<Pos> spawnpoints = new HashSet<>();
    public Set<Pos> woods = new HashSet<>();
    public Set<Pos> ores = new HashSet<>();
    public Set<Pos> diamonds = new HashSet<>();
    public Set<Pos> shops = new HashSet<>();
    
    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap<>();
      
      BiConsumer<String, Object> pusher = (key, value) -> {
        if (value != null) map.put(key, value);
      };
      
      pusher.accept("rest-area", restArea);
      pusher.accept("core", core);
      pusher.accept("mission", mission);
      
      pusher.accept("spawnpoints", new ArrayList<>(spawnpoints));
      pusher.accept("woods", new ArrayList<>(woods));
      pusher.accept("ores", new ArrayList<>(ores));
      pusher.accept("diamonds", new ArrayList<>(diamonds));
      pusher.accept("shops", new ArrayList<>(shops));
      
      return map;
    }
    
    static public MapPos deserialize(Map<String, Object> map) {
      MapPos mp = new MapPos();
      
      mp.restArea = (Pos) map.getOrDefault("rest-area", null);
      mp.core = (Pos) map.getOrDefault("core", null);
      mp.mission = (Pos) map.getOrDefault("mission", null);
      
      Function<String, List<Pos>> loader = CoreUtils.listLoader(Pos.class)
        .compose(map::get);
      
      mp.spawnpoints = new HashSet<>(loader.apply("spawnpoints"));
      mp.woods = new HashSet<>(loader.apply("woods"));
      mp.ores = new HashSet<>(loader.apply("ores"));
      mp.diamonds = new HashSet<>(loader.apply("diamonds"));
      mp.shops = new HashSet<>(loader.apply("shops"));
      
      return mp;
    }
  }
  
  public LobbyPos lobby = new LobbyPos();
  public MapPos map = new MapPos();
  
  record VillagerData(Location loc, Villager villager) {
  }
  
  List<VillagerData> villagers = new ArrayList<>();
  
  public void updateVillagers() {
    villagers.removeIf(vd -> !vd.villager.isValid() || vd.villager.isDead());
    
    for (VillagerData vd : villagers) {
      Location loc = vd.loc.clone();
      Villager villager = vd.villager;
      
      PlayerUtils.allGaming().stream()
        .filter(p -> LocUtils.near(p, villager, 5))
        .map(
          p -> LocUtils.hitboxCenter(p)
            .toVector()
            .subtract(LocUtils.hitboxCenter(villager).toVector())
        )
        .min(Comparator.comparing(Vector::lengthSquared))
        .ifPresent(loc::setDirection);
      
      villager.setRotation(loc.getYaw(), loc.getPitch());
    }
  }
  
  static public class Shop implements ConfigurationSerializable {
    public String name = "Anonymous Shop";
    public Villager.Type biome = Villager.Type.PLAINS;
    public Villager.Profession prof = Villager.Profession.NONE;
    public Material blockType = Material.BEDROCK;
    
    public List<MaybeGen> items = new ArrayList<>();
    
    public Villager summonVillager(Location loc) {
      World world = loc.getWorld();
      
      Villager villager = (Villager) world.spawnEntity(
        loc,
        EntityType.VILLAGER
      );
      
      villager.setAI(false);
      villager.setNoPhysics(true);
      villager.setCanPickupItems(false);
      villager.setPersistent(true);
      villager.setInvulnerable(true);
      
      villager.setVillagerType(biome);
      villager.setProfession(prof);
      villager.setVillagerLevel(5);
      
      List<MerchantRecipe> recipes = new ArrayList<>();
      
      List<ItemStack> itemList = items.stream().map(MaybeGen::get).toList();
      
      for (int i = 0; i + 1 < itemList.size(); i += 2) {
        ItemStack good = itemList.get(i), cost = itemList.get(i + 1);
        
        MerchantRecipe customTrade = new MerchantRecipe(
          good,
          0,
          Integer.MAX_VALUE,
          false,
          0,
          0.0f
        );
        customTrade.addIngredient(cost);
        
        recipes.add(customTrade);
      }
      
      villager.setRecipes(recipes);
      
      return villager;
    }
    
    @Override
    public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap<>();
      
      BiConsumer<String, Object> pusher = (key, value) -> {
        if (value != null) map.put(key, value);
      };
      
      pusher.accept("name", name);
      pusher.accept("biome", biome.key().asString());
      pusher.accept("profession", prof.key().asString());
      pusher.accept("block-type", blockType.name());
      pusher.accept("items", items);
      
      return map;
    }
    
    static public Shop deserialize(Map<String, Object> map) {
      Shop shop = new Shop();
      
      if (map.containsKey("name")) shop.name = (String) map.get("name");
      if (map.containsKey("biome")) shop.biome = Registry.VILLAGER_TYPE.get(
        Key.key(
          (String) map.get("biome")
        )
      );
      if (map.containsKey("profession"))
        shop.prof = Registry.VILLAGER_PROFESSION.get(
          Key.key((String) map.get("profession"))
        );
      if (map.containsKey("block-type")) shop.blockType = Material.valueOf(
        (String) map.get("block-type")
      );
      
      Function<String, List<MaybeGen>> loader = CoreUtils.listLoader(
        MaybeGen.class
      )
        .compose(map::get);
      
      shop.items = loader.apply("items");
      
      return shop;
    }
  }
  
  public List<Shop> shops = new ArrayList<>();
  
  public Map<UUID, Stats> stats = new HashMap<>();
  
  public enum Phase {
    CoreWilting(5, "core-wilting", null),
    DoubleDamage(4, "double-core-damage", CoreWilting),
    DeathPenalty(3, "death-penalty", DoubleDamage),
    MissionsStarted(
      2,
      "missions-started",
      DeathPenalty
    ),
    ShopOpened(
      1,
      "shop-opened",
      MissionsStarted
    ),
    CoreProtected(
      0,
      "core-protected",
      ShopOpened
    );
    
    public final int index;
    public final String translationKey;
    public final Phase next;
    
    Phase(int index, String translationKey, Phase next) {
      this.index = index;
      this.translationKey = translationKey;
      this.next = next;
    }
    
    Component $(String template) {
      return TextUtils.$(template.formatted(translationKey));
    }
    
    public Component displayName() {
      return $("game.phases.%s.name");
    }
    
    public Component description() {
      return $("game.phases.%s.desc");
    }
    
    public Component title() {
      return TextUtils.$(
        "game.phase-title",
        List.of(Placeholder.component("index", Component.text(index + 1)))
      );
    }
    
    public int minRespawnTime() {
      return PlayerData.minRespawnTime + 3 * index;
    }
    
    public boolean isAfter(Phase that) {
      return this.index >= that.index;
    }
  }
  
  public Phase phase;
  public int phaseTimer;
  
  public int truceTimer = 0;
  
  public boolean isInTruce() {
    return truceTimer > 0;
  }
  
  public enum Side {
    RED("red", NamedTextColor.RED, Color.RED),
    GREEN(
      "green",
      NamedTextColor.GREEN,
      Color.LIME
    ),
    SPECTATOR("spectator", NamedTextColor.GRAY, Color.GRAY);
    
    public final String id;
    public final String translateKey;
    public final NamedTextColor color;
    public final Color dyeColor;
    
    Side(String id, NamedTextColor color, Color dyeColor) {
      this.id = id;
      translateKey = "game.sides." + id;
      this.color = color;
      this.dyeColor = dyeColor;
    }
    
    public String title() {
      return TextUtils.$r(translateKey);
    }
    
    public String pureTitle() {
      return TextUtils.stripColor(title());
    }
    
    public Component titleComp() {
      return TextUtils.$(translateKey);
    }
    
    public Side opposite() {
      switch (this) {
        case RED -> {
          return GREEN;
        }
        case GREEN -> {
          return RED;
        }
      }
      
      return SPECTATOR;
    }
  }
  
  public Map<Side, SideData> sideData;
  
  public SideData getSideData(Side side) {
    return sideData.get(side);
  }
  
  public SideData getSideData(Player pl) {
    return getSideData(getPlayerData(pl).side);
  }
  
  public class BarSet {
    String id;
    Function<SideData, Integer> current, max;
    
    public BarSet(
      String id, Function<SideData, Integer> current, Function<SideData, Integer> max
    ) {
      this.id = id;
      this.current = current;
      this.max = max;
    }
    
    Map<Side, BossBar> bars = new HashMap<>();
    
    public Component getTitle(Side side) {
      return TextUtils.$(
        "game.bars." + id,
        List.of(
          Placeholder.component(
            "time",
            CoreUtils.formatTimeComp(
              Math.ceilDiv(current.apply(getSideData(side)), 20),
              NamedTextColor.AQUA
            )
          )
        )
      );
    }
    
    public void show(Side side) {
      if (getSideData(side).noOresTicks > 0) return;
      
      bars.put(
        side,
        BossBar.bossBar(
          getTitle(side),
          1F,
          BossBar.Color.WHITE,
          BossBar.Overlay.PROGRESS
        )
      );
      
      for (Player p : PlayerUtils.getTeammates(side)) {
        bars.get(side).addViewer(p);
      }
    }
    
    public void hide(Side side) {
      if (!bars.containsKey(side)) return;
      
      for (Player p : PlayerUtils.getTeammates(side)) {
        bars.get(side).removeViewer(p);
      }
    }
    
    public void update(Side side) {
      BossBar bar = bars.get(side);
      SideData sd = getSideData(side);
      bar.name(getTitle(side));
      bar.progress(1F * current.apply(sd) / max.apply(sd));
    }
  }
  
  public BarSet noOresBars = new BarSet(
    "no-ores",
    sd -> sd.noOresTicks,
    sd -> sd.maxNoOresTicks
  );
  public BarSet noShopBars = new BarSet(
    "no-shop",
    sd -> sd.noShopTicks,
    sd -> sd.maxNoShopTicks
  );
  
  Team spectatorTeam;
  Map<Side, Map<RolesManager.RoleKey, Team>> teams = new HashMap<>();
  
  public Team getTeam(Side side, Role role) {
    if (side == Side.SPECTATOR) return spectatorTeam;
    
    return teams.get(side).get(role.id);
  }
  
  public Team getTeam(Player pl) {
    PlayerData data = getPlayerData(pl);
    return getTeam(data.side, data.role);
  }
  
  public void enforceTeam(Player pl) {
    getTeam(pl).addPlayer(pl);
  }
  
  public void recreateTeams() {
    Scoreboard board = Bukkit.getServer().getScoreboardManager()
      .getMainScoreboard();
    
    for (Team team : board.getTeams()) {
      team.unregister();
    }
    
    teams.clear();
    
    spectatorTeam = board.registerNewTeam("spectator");
    
    spectatorTeam.color(Side.SPECTATOR.color);
    spectatorTeam.displayName(Side.SPECTATOR.titleComp());
    spectatorTeam.prefix(
      Component.text("[%s] ".formatted(Side.SPECTATOR.pureTitle()))
    );
    spectatorTeam.setOption(
      Team.Option.COLLISION_RULE,
      Team.OptionStatus.NEVER
    );
    
    for (Side side : new Side[]{
      Side.RED, Side.GREEN
    }) {
      Map<RolesManager.RoleKey, Team> sideTeams = new HashMap<>();
      
      for (Role role : DestroyTheCore.rolesManager.roles.values()) {
        Team team = board.registerNewTeam(
          side.id + "-" + role.id.name().toLowerCase()
        );
        
        team.color(side.color);
        team.displayName(
          Component.join(
            JoinConfiguration.spaces(),
            side.titleComp(),
            Component.text("-"),
            Component.text(role.name)
          )
        );
        team.prefix(Component.text("[%s] ".formatted(role.name)));
        
        sideTeams.put(role.id, team);
      }
      
      teams.put(side, sideTeams);
    }
  }
  
  Objective respawnTimeBoard, healthBoard;
  
  public void createScoreboards() {
    Scoreboard board = Bukkit.getServer().getScoreboardManager()
      .getMainScoreboard();
    
    respawnTimeBoard = board.getObjective("respawn-time");
    if (respawnTimeBoard == null) {
      respawnTimeBoard = board.registerNewObjective(
        "respawn-time",
        Criteria.DUMMY,
        Component.text("Sin Display").color(NamedTextColor.RED)
      );
    }
    
    healthBoard = board.getObjective("health");
    if (healthBoard == null) {
      healthBoard = board.registerNewObjective(
        "health",
        Criteria.HEALTH,
        Component.text("❤").color(NamedTextColor.RED)
      );
      
      healthBoard.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }
  }
  
  public void hideRTScore() {
    Scoreboard board = Bukkit.getServer().getScoreboardManager()
      .getMainScoreboard();
    board.clearSlot(DisplaySlot.PLAYER_LIST);
  }
  
  public void showRTScore() {
    respawnTimeBoard.setDisplaySlot(DisplaySlot.PLAYER_LIST);
  }
  
  public void enforceRTScore(Player pl) {
    Scoreboard board = Bukkit.getServer().getScoreboardManager()
      .getMainScoreboard();
    Objective respawnTimeBoard = board.getObjective("respawn-time");
    
    PlayerData data = getPlayerData(pl);
    Score score = respawnTimeBoard.getScore(pl);
    
    if (
      data.side.equals(Side.SPECTATOR)
    ) score.resetScore();
    else score.setScore(data.respawnTime);
  }
  
  public Map<UUID, PlayerData> playerData = new HashMap<>();
  
  public PlayerData getPlayerData(Player pl) {
    return playerData.get(pl.getUniqueId());
  }
  
  public void handleJoinedPlayer(Player pl) {
    UUID id = pl.getUniqueId();
    
    if (!stats.containsKey(id)) stats.put(id, new Stats());
    if (getPlayerData(pl) == null) playerData.put(id, new PlayerData(pl));
    
    Iterator<Recipe> rit = Bukkit.recipeIterator();
    while (rit.hasNext()) {
      if (rit.next() instanceof Keyed keyed) {
        pl.discoverRecipe(keyed.getKey());
      }
    }
    
    pl.clearActivePotionEffects();
    PlayerUtils.enforceNightVision(pl);
    PlayerUtils.refreshSpectatorAbilities(pl);
    PlayerUtils.fullyHeal(pl);
    
    if (isPlaying) {
      CoreUtils.setTickOut(() -> {
        PlayerUtils.prefixedSend(pl, TextUtils.$("game.prompt-rejoin"));
      });
    }
    else {
      getPlayerData(pl).side = Side.SPECTATOR;
    }
    enforceTeam(pl);
    
    if (PlayerUtils.shouldHandle(pl)) {
      DestroyTheCore.inventoriesManager.store(pl);
      
      pl.getInventory().setItem(
        4,
        DestroyTheCore.itemsManager.gens.get(
          ItemsManager.ItemKey.CHOOSE_ROLE
        ).getItem()
      );
      
      PlayerUtils.backToLobby(pl);
    }
    
    if (isPlaying) {
      PlayerUtils.hideSpectators(pl);
    }
    
    DestroyTheCore.worldsManager.onPlayerChangeWorld(pl, pl.getWorld());
  }
  
  public void handleQuitedPlayer(Player pl) {
    if (!isPlaying) return;
    
    DestroyTheCore.inventoriesManager.store(pl);
  }
  
  public void handleChat(AsyncChatEvent ev) {
    ev.renderer(
      ChatRenderer.viewerUnaware(
        (Player p, Component name, Component message) -> TextUtils.$(
          "chat.format",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(p)),
            Placeholder.component("message", message.color(null))
          )
        )
      )
    );
    
    if (!isPlaying) return;
    
    Player pl = ev.getPlayer();
    Game.Side side = DestroyTheCore.game.getPlayerData(pl).side;
    
    ev.viewers().removeIf(
      audience -> audience instanceof Player p
        && side != Side.SPECTATOR
        && DestroyTheCore.game.getPlayerData(p).side
          .equals(side.opposite())
    );
  }
  
  public void handleEntityDamage(EntityDamageByEntityEvent ev) {
    if (ev.getDamager() instanceof Player attacker) {
      if (isPlaying && getPlayerData(attacker).side == Side.SPECTATOR) {
        attacker.sendActionBar(TextUtils.$("game.banned.attack.spectator"));
        ev.setCancelled(true);
        return;
      }
      
      if (ev.getEntity() instanceof Player victim) {
        handlePlayerDamage(attacker, victim, null, ev);
      }
    }
    
    if (
      ev.getDamager() instanceof Projectile proj
        && proj.getShooter() instanceof Player shooter
        && ev.getEntity() instanceof Player victim
    ) {
      handlePlayerDamage(shooter, victim, proj, ev);
    }
  }
  
  public void handlePlayerDamage(
    Player attacker, Player victim, Projectile proj, EntityDamageByEntityEvent ev
  ) {
    if (isPlaying) {
      double damage = ev.getDamage(), finalDamage = ev.getFinalDamage();
      
      if (getPlayerData(attacker).side.equals(getPlayerData(victim).side)) {
        if (proj == null) {
          attacker.sendActionBar(TextUtils.$("game.banned.attack.teammate"));
        }
        else if (proj.getScoreboardTags().contains("grenade")) {
          ev.setDamage(damage * 0.5);
          return;
        }
        
        ev.setCancelled(true);
        return;
      }
      
      if (finalDamage <= 0) return;
      
      if (getPlayerData(victim).role.id != RolesManager.RoleKey.PROVOCATEUR) {
        List<Player> provocateurs = new ArrayList<>();
        for (Player p : PlayerUtils.getTeammates(victim)) {
          if (
            getPlayerData(p).role.id != RolesManager.RoleKey.PROVOCATEUR
          ) continue;
          if (!LocUtils.near(p, victim, 10)) continue;
          
          provocateurs.add(p);
        }
        
        double damageReduced = 0;
        for (Player p : provocateurs) {
          double ratio = p.hasPotionEffect(
            PotionEffectType.ABSORPTION
          ) ? 0.9 : 0.6;
          double amount = Math.max(1, damage * ratio / provocateurs.size());
          
          PlayerUtils.delayAssign(
            victim,
            p,
            Particle.CRIT,
            () -> {
              p.damage(
                amount,
                DamageSource.builder(DamageType.PLAYER_ATTACK).withDirectEntity(
                  attacker
                ).withCausingEntity(attacker).build()
              );
            }
          );
          damageReduced += amount;
        }
        
        damage -= damageReduced;
        ev.setDamage(damage);
      }
      
      DestroyTheCore.damageManager.addDamage(attacker, victim, finalDamage);
    }
    
    if (ev.getFinalDamage() >= 2) victim.removePotionEffect(
      PotionEffectType.INVISIBILITY
    );
    
    DestroyTheCore.itemsManager.onPlayerDamage(attacker, victim);
  }
  
  static public Component bountyPrefix;
  
  public boolean nextPlayerDropAll = false;
  
  public void handlePlayerDeath(PlayerDeathEvent ev) {
    Player pl = ev.getPlayer();
    
    if (!PlayerUtils.shouldHandle(pl)) return;
    ev.setCancelled(true);
    
    if (!LocUtils.inLive(pl.getLocation())) {
      CoreUtils.setTickOut(() -> PlayerUtils.backToLobby(pl));
      return;
    }
    
    pl.clearActivePotionEffects();
    PlayerUtils.enforceNightVision(pl);
    PlayerUtils.fullyHeal(pl);
    
    if (!isPlaying) return;
    
    CoreUtils.setTickOut(() -> PlayerUtils.teleportToRestArea(pl));
    
    PlayerData data = getPlayerData(pl);
    if (!data.alive) return;
    
    if (phase.isAfter(Phase.DeathPenalty)) {
      getSideData(pl).directAttackCore();
      checkWinner();
    }
    
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    head.editMeta(uncastedMeta -> {
      SkullMeta meta = (SkullMeta) uncastedMeta;
      meta.setOwningPlayer(pl);
    });
    pl.getWorld().dropItemNaturally(pl.getLocation(), head);
    
    DestroyTheCore.inventoriesManager.applyVanishingCurse(pl);
    DestroyTheCore.inventoriesManager.dropSome(pl, nextPlayerDropAll ? 1 : 0.1);
    DestroyTheCore.inventoriesManager.store(pl);
    
    nextPlayerDropAll = false;
    
    UUID killerId = DestroyTheCore.damageManager.getMostDamage(pl);
    if (killerId == null) {
      Component message = ev.deathMessage();
      if (message == null) message = TextUtils.$(
        "game.death.messages.unknown",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      );
      
      PlayerUtils.broadcast(message.colorIfAbsent(NamedTextColor.GRAY));
    }
    else {
      Player killer = Bukkit.getPlayer(killerId);
      assert killer != null;
      
      PlayerUtils.broadcast(
        TextUtils.$(
          "game.death.messages.kill",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.unparsed(
              "action",
              RandomUtils.pick(
                TextUtils.translateRaw(
                  "game.death.messages.kill-actions"
                ).split("\\|")
              )
            ),
            Placeholder.component("killer", PlayerUtils.getName(killer))
          )
        )
      );
      
      new ParticleBuilder(Particle.SOUL)
        .allPlayers()
        .location(LocUtils.hitboxCenter(pl))
        .offset(0.1, 0.3, 0.1)
        .count(15)
        .extra(0.2)
        .spawn();
      
      new ParticleBuilder(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS)
        .allPlayers()
        .location(LocUtils.hitboxCenter(pl))
        .offset(0.5, 0.6, 0.5)
        .count(20)
        .extra(0)
        .spawn();
      
      killer.sendActionBar(TextUtils.$("game.death.killer-sin"));
      
      killer.addPotionEffect(
        new PotionEffect(
          PotionEffectType.GLOWING,
          5 * 20,
          0,
          false,
          false
        )
      );
      killer.addPotionEffect(
        new PotionEffect(
          PotionEffectType.SLOWNESS,
          5 * 20,
          0,
          false,
          false
        )
      );
      killer.addPotionEffect(
        new PotionEffect(
          PotionEffectType.BLINDNESS,
          5 * 20,
          0,
          false,
          false
        )
      );
      killer.addPotionEffect(
        new PotionEffect(
          PotionEffectType.WEAKNESS,
          5 * 20,
          2,
          false,
          false
        )
      );
      
      killer.give(
        DestroyTheCore.itemsManager.gens.get(
          ItemsManager.ItemKey.SOUL
        ).getItem()
      );
      
      if (data.killStreak >= 10) {
        killer.give(new ItemStack(Material.EMERALD, data.killStreak));
        
        PlayerUtils.broadcast(
          bountyPrefix.append(
            TextUtils.$(
              "game.bounty.reward",
              List.of(
                Placeholder.component("player", PlayerUtils.getName(killer)),
                Placeholder.component("amount", Component.text(data.killStreak))
              )
            )
          )
        );
      }
      
      PlayerData killerData = getPlayerData(killer);
      killerData.addKill();
      
      if (killerData.killStreak == 10) {
        PlayerUtils.broadcast(
          bountyPrefix.append(
            TextUtils.$(
              "game.bounty.appear",
              List.of(
                Placeholder.component("player", PlayerUtils.getName(killer))
              )
            )
          )
        );
      }
      
      DestroyTheCore.boardsManager.refresh(killer);
    }
    
    data.kill();
    
    PlayerUtils.normalTitleTimes(pl);
    pl.sendTitlePart(TitlePart.TITLE, TextUtils.$("game.death.title"));
    
    DestroyTheCore.boardsManager.refresh(pl);
    PlayerUtils.scheduleRespawn(pl);
  }
  
  public void handleHungry(FoodLevelChangeEvent ev) {
    if (!(ev.getEntity() instanceof Player pl)) return;
    
    if (LocUtils.inLobby(pl) || !getPlayerData(pl).isGaming()) {
      PlayerUtils.resetHunger(pl);
      ev.setCancelled(true);
      return;
    }
  }
  
  BukkitTask startingTask = null;
  
  public void handleInteract(PlayerInteractEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = getPlayerData(pl);
    ItemStack item = ev.getItem();
    
    if (ev.getAction() == Action.LEFT_CLICK_BLOCK) handleLeftClickBlock(ev);
    if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) handleRightClickBlock(ev);
    
    if (
      List.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
        .contains(ev.getAction())
    ) {
      if (
        !PlayerUtils.checkUsingBlock(
          pl,
          ev.getClickedBlock()
        )
          && item != null
          && !item.isEmpty()
          && item
            .getType().equals(
              Material.KNOWLEDGE_BOOK
            )
      ) ev.setCancelled(true);
      
      if (!data.alive && !LocUtils.inLobby(pl)) {
        if (!PlayerUtils.shouldHandle(pl)) return;
        
        pl.sendActionBar(TextUtils.$("game.banned.use.time"));
        ev.setCancelled(true);
        return;
      }
      
      if (
        LocUtils.inLive(pl)
          &&
          ev.getHand() == EquipmentSlot.HAND
          && item != null
          && item
            .hasItemMeta()
          && item.getItemMeta().getPersistentDataContainer().has(
            Role.skillNamespace
          )
      ) {
        if (!PlayerUtils.checkHandCooldown(pl, data.extraSkillReload)) return;
        PlayerUtils.setHandCooldown(pl, data.role.skillCooldown);
        
        data.role.useSkill(pl);
      }
    }
  }
  
  public void handleLeftClickBlock(PlayerInteractEvent ev) {
    if (ev.getHand() != EquipmentSlot.HAND) return;
    
    Player pl = ev.getPlayer();
    Block block = ev.getClickedBlock();
    if (block == null || block.getType() != Material.ENDER_CHEST) return;
    
    SideData sd = getSideData(pl);
    if (sd == null) return;
    
    ItemStack item = pl.getInventory().getItemInMainHand();
    
    if (
      Tag.ITEMS_SWORDS.isTagged(item.getType())
        || Tag.ITEMS_AXES.isTagged(item.getType())
        || Tag.ITEMS_PICKAXES.isTagged(item.getType())
    ) return;
    
    Map<Integer, ItemStack> leftovers = sd.enderChest.addItem(item);
    
    if (leftovers.isEmpty()) {
      pl.getInventory().setItemInMainHand(ItemStack.empty());
      
      pl.playSound(
        pl.getLocation(),
        Sound.BLOCK_ENDER_CHEST_OPEN,
        1, // Volume
        1 // Pitch
      );
    }
    else {
      pl.getInventory().setItemInMainHand(leftovers.get(0));
      
      pl.playSound(
        pl.getLocation(),
        Sound.BLOCK_IRON_DOOR_CLOSE,
        1, // Volume
        1 // Pitch
      );
    }
  }
  
  public void handleRightClickBlock(PlayerInteractEvent ev) {
    Player pl = ev.getPlayer();
    Block block = ev.getClickedBlock();
    if (block == null) return;
    
    if (LocUtils.inLobby(pl)) {
      if (
        DestroyTheCore.worldsManager.isReady
          && lobby.startButton != null
          && Pos.of(block).isSameBlockAs(lobby.startButton)
      ) {
        if (startingTask == null || startingTask.isCancelled()) {
          scheduleStart();
        }
        else {
          cancelScheduleStart();
        }
        return;
      }
      else if (PlayerUtils.shouldHandle(pl)) {
        if (PlayerUtils.checkUsingBlock(pl, block)) {
          pl.sendActionBar(
            TextUtils.$("game.banned.use.lobby")
          );
        }
        ev.setCancelled(true);
      }
    }
    
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    if (getPlayerData(pl).side.equals(Side.SPECTATOR)) {
      pl.sendActionBar(TextUtils.$("game.banned.use.spectator"));
      ev.setCancelled(true);
      return;
    }
    
    for (Pos rest : new Pos[]{
      DestroyTheCore.game.map.restArea, LocUtils.flip(
        DestroyTheCore.game.map.restArea
      )
    }) {
      if (
        LocUtils.inLive(pl)
          &&
          LocUtils.near(Pos.of(block), rest, 6)
      ) {
        ev.getPlayer().sendActionBar(TextUtils.$("game.banned.use.rest-area"));
        ev.setCancelled(true);
        return;
      }
    }
    
    if (
      isPlaying
        && block.getType().equals(Material.ENDER_CHEST)
        && !pl.isSneaking()
    ) {
      ev.setCancelled(true);
      
      if (!LocUtils.canAccess(pl, block)) {
        pl.sendActionBar(TextUtils.$("game.banned.open-enemy-container"));
        return;
      }
      
      Location loc = block.getLocation();
      SideData sd = getSideData(pl);
      
      pl.openInventory(sd.enderChest);
      sd.addEnderChestViewer(loc, pl);
      if (sd.enderChestViewers.get(loc).size() == 1) {
        LocUtils.playChestAnimation(loc, true);
      }
    }
  }
  
  public void handleInteractEntity(PlayerInteractEntityEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = getPlayerData(pl);
    
    if (data.side == Side.SPECTATOR) {
      pl.sendActionBar(TextUtils.$("game.banned.use.spectator"));
      ev.setCancelled(true);
      return;
    }
  }
  
  public void handleBlockPlace(BlockPlaceEvent ev) {
    Player pl = ev.getPlayer();
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    Block block = ev.getBlock();
    Location blockLoc = block.getLocation();
    if (map.core == null) return;
    
    if (!LocUtils.inLive(blockLoc)) {
      pl.sendActionBar(TextUtils.$("game.banned.place.lobby"));
      ev.setCancelled(true);
      return;
    }
    
    if (
      List.of(Material.OBSIDIAN, Material.CRYING_OBSIDIAN).contains(
        block.getType()
      ) && LocUtils.nearAnyCore(blockLoc, 3)
    ) {
      pl.sendActionBar(TextUtils.$("game.banned.place.obsidian"));
      ev.setCancelled(true);
      return;
    }
    
    if (LocUtils.nearSpawn(blockLoc)) {
      ev.getPlayer().sendActionBar(TextUtils.$("game.banned.place.spawn"));
      ev.setCancelled(true);
      return;
    }
  }
  
  public AtomicInteger fakeBreakerId = new AtomicInteger(1_000_000);
  
  public void handleOresBreak(Player pl, Block block) {
    PlayerUtils.damageHandItem(pl);
    
    Material originalType = block.getType();
    Constants.OreData ore = Constants.ores.get(originalType);
    
    getPlayerData(pl).addOre(originalType);
    
    ItemStack tool = pl.getInventory().getItemInMainHand();
    if (block.isPreferredTool(tool)) {
      double amount = CoreUtils.applyFortune(
        tool.getEnchantmentLevel(Enchantment.FORTUNE)
      );
      if (pl.hasPotionEffect(PotionEffectType.LUCK)) {
        if (
          RandomUtils.hit(
            (pl.getPotionEffect(
              PotionEffectType.LUCK
            ).getAmplifier() + 1) * 0.25
          )
        ) amount *= 2;
      }
      if (pl.hasPotionEffect(PotionEffectType.UNLUCK)) {
        if (
          RandomUtils.hit(
            (pl.getPotionEffect(
              PotionEffectType.UNLUCK
            ).getAmplifier() + 1) * 0.25
          )
        ) amount *= 0.5;
      }
      if (amount >= 1) {
        pl.give(new ItemStack(ore.dropType(), (int) amount));
        
        if (amount >= 2) {
          pl.sendActionBar(
            TextUtils.$(
              "game.ores.luck",
              List.of(
                Placeholder.component("amount", Component.text((int) amount))
              )
            )
          );
        }
      }
      else {
        pl.sendActionBar(TextUtils.$("game.ores.bad-luck"));
      }
      
      int orbsCount = ore.maxXp() > 0 ? RandomUtils.range(5, 8) : 0;
      for (int i = 0; i < orbsCount; ++i) {
        ExperienceOrb orb = (ExperienceOrb) block.getWorld().spawnEntity(
          LocUtils.toBlockCenter(block.getLocation()),
          EntityType.EXPERIENCE_ORB
        );
        orb.setExperience(RandomUtils.range(ore.minXp(), ore.maxXp() + 1));
      }
      
      if (getPlayerData(pl).role.id == RolesManager.RoleKey.GOLD_DIGGER) {
        for (Player p : PlayerUtils.getTeammates(pl)) {
          if (p.equals(pl)) continue;
          if (!LocUtils.near(p, pl, 10)) continue;
          
          if (RandomUtils.hit(0.25)) {
            ItemStack item = new ItemStack(ore.dropType());
            p.give(item);
            
            p.sendActionBar(
              TextUtils.$(
                "roles.gold-digger.ores-bonus",
                List.of(
                  Placeholder.component(
                    "ore",
                    item.effectiveName().color(null)
                  ),
                  Placeholder.component(
                    "player",
                    PlayerUtils.getName(pl).color(null)
                  )
                )
              )
            );
          }
        }
      }
    }
    
    if (
      Mission.centerLoc != null
        && InfiniteOresMission.check(block.getLocation())
    ) return;
    
    block.setType(Material.BEDROCK);
    
    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    BlockPosition pos = new BlockPosition(
      block.getX(),
      block.getY(),
      block.getZ()
    );
    int breakerId = fakeBreakerId.getAndIncrement();
    
    int kekkaiBonus = KekkaiMasterRole.checkFastOres(
      LocUtils.toBlockCenter(block.getLocation())
    ) ? 2 : 1;
    
    new BukkitRunnable() {
      int stage = 0;
      
      @Override
      public void run() {
        if (stage > 9) stage = -1;
        
        PacketContainer packet = manager.createPacket(
          PacketType.Play.Server.BLOCK_BREAK_ANIMATION
        );
        packet.getIntegers().write(0, breakerId);
        packet.getBlockPositionModifier().write(0, pos);
        packet.getIntegers().write(1, stage);
        
        try {
          for (Player p : Bukkit.getOnlinePlayers()) {
            if (LocUtils.isSameWorld(p.getWorld(), block.getWorld())) {
              manager.sendServerPacket(
                p,
                packet
              );
            }
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        
        if (stage == -1) {
          block.setType(originalType);
          ParticleUtils.cloud(
            PlayerUtils.all(),
            LocUtils.toBlockCenter(block.getLocation())
          );
          
          cancel();
          return;
        }
        
        stage++;
      }
    }.runTaskTimer(
      DestroyTheCore.instance,
      0,
      ore.cooldownSeconds() * 2 / kekkaiBonus
    );
  }
  
  public void handleCoreAttack(Player pl, Block block) {
    PlayerUtils.damageHandItem(pl);
    
    BiConsumer<PotionEffectType, Integer> effector = (type, level) -> {
      pl.addPotionEffect(
        new PotionEffect(type, 10 * 20, level, false, false)
      );
    };
    
    effector.accept(PotionEffectType.GLOWING, 0);
    effector.accept(PotionEffectType.SLOWNESS, 0);
    effector.accept(PotionEffectType.MINING_FATIGUE, 0);
    effector.accept(PotionEffectType.WEAKNESS, 0);
    
    PlayerData data = getPlayerData(pl);
    data.addCoreAttack();
    
    Side oppositeSide = data.side.opposite();
    SideData ocd = getSideData(oppositeSide);
    ocd.attackCore();
    
    DestroyTheCore.boardsManager.refresh();
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (getPlayerData(p).side.equals(oppositeSide)) {
        PlayerUtils.normalTitleTimes(p);
        p.sendTitlePart(
          TitlePart.TITLE,
          TextUtils.$("game.core-attack.title").color(data.side.color)
        );
        p.sendTitlePart(
          TitlePart.SUBTITLE,
          TextUtils.$(
            "game.core-attack.subtitle",
            List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
          )
        );
        p.playSound(
          p.getLocation(),
          Sound.BLOCK_ANVIL_LAND,
          1, // Volume
          1 // Pitch
        );
      }
      else {
        p.playSound(
          LocUtils.live(
            LocUtils.selfSide(map.core.center(), oppositeSide)
          ),
          Sound.BLOCK_ANVIL_LAND,
          0.8f, // Volume
          1 // Pitch
        );
        
        p.playSound(
          p.getLocation(),
          Sound.BLOCK_ANVIL_LAND,
          0.2f, // Volume
          1 // Pitch
        );
      }
    }
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "game.core-attack.message",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("side", oppositeSide.titleComp()),
          Placeholder.component("health", Component.text(ocd.coreHealth))
        )
      )
    );
    
    checkWinner();
  }
  
  public void handleBlockBreak(BlockBreakEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = getPlayerData(pl);
    Block block = ev.getBlock();
    
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    Block blockBelow = block.getRelative(BlockFace.DOWN);
    if (blockBelow.getType().equals(Material.MOVING_PISTON)) {
      blockBelow.setType(Material.AIR);
    }
    
    if (!LocUtils.inLive(pl)) {
      pl.sendActionBar(TextUtils.$("game.banned.break.lobby"));
      ev.setCancelled(true);
      return;
    }
    
    if (getPlayerData(pl).side.equals(Side.SPECTATOR)) {
      pl.sendActionBar(TextUtils.$("game.banned.break.spectator"));
      ev.setCancelled(true);
      return;
    }
    
    for (Pos rest : new Pos[]{
      DestroyTheCore.game.map.restArea, LocUtils.flip(
        DestroyTheCore.game.map.restArea
      )
    }) {
      if (
        LocUtils.near(Pos.of(block), rest, 6)
      ) {
        pl.sendActionBar(TextUtils.$("game.banned.break.rest-area"));
        ev.setCancelled(true);
        return;
      }
    }
    
    if (!data.alive) {
      pl.sendActionBar(TextUtils.$("game.banned.break.time"));
      ev.setCancelled(true);
      return;
    }
    
    if (block.getType().equals(Material.NETHERITE_BLOCK)) {
      pl.sendActionBar(TextUtils.$("game.banned.break.netherite"));
      ev.setCancelled(true);
      return;
    }
    
    if (block.getType().equals(Material.DIAMOND_ORE)) {
      PlayerUtils.broadcast(
        TextUtils.$(
          "chat.format",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.unparsed(
              "message",
              RandomUtils.pick(
                TextUtils.translateRaw("game.diamond-broadcast").split("\\|")
              )
            )
          )
        )
      );
    }
    
    if (
      map.woods.stream().anyMatch(
        loc -> LocUtils.isSameBlock(
          LocUtils.live(loc),
          block.getLocation()
        )
          || LocUtils.isSameBlock(
            LocUtils.live(LocUtils.flip(loc)),
            block.getLocation()
          )
      )
    ) {
      PlayerUtils.damageHandItem(pl);
      
      pl.give(new ItemStack(ev.getBlock().getType(), 2));
      pl.giveExp(RandomUtils.range(1, 4));
      
      ev.setCancelled(true);
      return;
    }
    
    if (Constants.ores.containsKey(block.getType())) {
      handleOresBreak(pl, block);
      
      ev.setCancelled(true);
      return;
    }
    
    //    if (block.getState() instanceof InventoryHolder && !LocationUtils.canAccess(pl, block)) {
    //      pl.sendActionBar(TextUtils.$("game.banned.break.enemy-container"));
    //      getPlayerData(pl).addRespawnTime(10);
    //      DestroyTheCore.boardsManager.refresh(pl);
    //      return;
    //    }
    
    if (map.core != null && data.side != Side.SPECTATOR) {
      if (
        LocUtils.isSameBlock(
          block.getLocation(),
          LocUtils.live(LocUtils.selfSide(map.core, pl))
        )
      ) {
        pl.sendActionBar(TextUtils.$("game.banned.break.own-core"));
        ev.setCancelled(true);
        return;
      }
      if (
        LocUtils.isSameBlock(
          block.getLocation(),
          LocUtils.live(LocUtils.enemySide(map.core, pl))
        )
      ) {
        ev.setCancelled(true);
        if (!isPlaying) return;
        
        handleCoreAttack(pl, block);
        return;
      }
    }
    
    if (block.getType().equals(Material.COBWEB)) {
      ev.setDropItems(false);
    }
    
    if (block.getType().equals(Material.ENDER_CHEST)) {
      ev.setDropItems(false);
      block.getWorld().dropItemNaturally(
        LocUtils.toBlockCenter(block.getLocation()),
        new ItemStack(Material.ENDER_CHEST)
      );
    }
  }
  
  public void handleBlockForm(BlockFormEvent ev) {
    Block block = ev.getBlock();
    
    if (
      LocUtils.inLive(block.getLocation())
        &&
        List.of(Material.OBSIDIAN, Material.CRYING_OBSIDIAN).contains(
          block.getType()
        )
        && LocUtils.nearAnyCore(
          block.getLocation(),
          3
        )
    ) {
      ev.setCancelled(true);
    }
  }
  
  public void handleLiquidFlow(BlockFromToEvent ev) {
    Block block = ev.getToBlock();
    
    if (
      LocUtils.inLive(block.getLocation())
        &&
        LocUtils.nearAnyCore(block.getLocation(), 3)
    ) {
      ev.setCancelled(true);
    }
  }
  
  public void handlePourLiquid(PlayerBucketEmptyEvent ev) {
    Block block = ev.getBlock();
    
    if (
      LocUtils.inLive(block.getLocation())
        &&
        LocUtils.nearAnyCore(block.getLocation(), 3)
    ) {
      ev.getPlayer().sendActionBar(TextUtils.$("game.banned.pour"));
      ev.setCancelled(true);
    }
  }
  
  public boolean unmovable(Block block) {
    Pos pos = Pos.of(block);
    
    Predicate<Pos> checker = p -> p != null
      && (p.isSameBlockAs(pos) || LocUtils.flip(p).isSameBlockAs(pos));
    
    Predicate<Set<Pos>> listChecker = set -> set != null
      && set.stream().anyMatch(checker);
    
    return checker.test(map.core)
      || listChecker.test(map.woods)
      || listChecker.test(map.ores)
      || listChecker.test(map.diamonds);
  }
  
  public boolean anyUnmovable(List<Block> blocks) {
    return blocks.stream().anyMatch(this::unmovable);
  }
  
  public void handleExplosion(EntityExplodeEvent ev) {
    ev.blockList().removeIf(
      block -> unmovable(block)
        || block.getState() instanceof BlockInventoryHolder
        || block.getState() instanceof DoubleChest
    );
  }
  
  public void handlePistonExtend(BlockPistonExtendEvent ev) {
    if (anyUnmovable(ev.getBlocks())) ev.setCancelled(true);
  }
  
  public void handlePistonRetract(BlockPistonRetractEvent ev) {
    if (anyUnmovable(ev.getBlocks())) ev.setCancelled(true);
  }
  
  public void handlePickupItem(PlayerAttemptPickupItemEvent ev) {
    Player pl = ev.getPlayer();
    ItemStack item = ev.getItem().getItemStack();
    
    if (getPlayerData(pl).side.equals(Side.SPECTATOR)) {
      ev.setCancelled(true);
      return;
    }
    
    if (!DestroyTheCore.rolesManager.canTakeExclusiveItem(pl, item)) {
      ev.setCancelled(true);
      return;
    }
  }
  
  public void handlePickupArrow(PlayerPickupArrowEvent ev) {
    Player pl = ev.getPlayer();
    
    if (getPlayerData(pl).side.equals(Side.SPECTATOR)) {
      ev.setCancelled(true);
      return;
    }
  }
  
  public void handleInventoryClick(
    Inventory inv, Player pl, ItemStack item, ClickType click, InventoryClickEvent ev
  ) {
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    if (inv.getType() == InventoryType.PLAYER) return;
    
    if (!DestroyTheCore.rolesManager.canTakeExclusiveItem(pl, item)) {
      ev.setCancelled(true);
      return;
    }
    
    if (inv instanceof MerchantInventory minv && ev.getRawSlot() == 2) {
      if (DestroyTheCore.itemsManager.isGen(item)) {
        MerchantRecipe recipe = minv.getSelectedRecipe();
        ItemGen gen = DestroyTheCore.itemsManager.getGen(item);
        if (gen instanceof UsableItemGen ugen && ugen.isInstantUse()) {
          ev.setCancelled(true);
          
          Map<Material, Integer> costs = new HashMap<>();
          Map<Material, Integer> given = new HashMap<>();
          
          for (ItemStack ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) continue;
            
            costs.put(
              ingredient.getType(),
              costs.getOrDefault(
                ingredient.getType(),
                0
              ) + ingredient.getAmount()
            );
          }
          
          for (int i = 0; i < 2; ++i) {
            ItemStack slotItem = minv.getItem(i);
            if (slotItem == null || slotItem.isEmpty()) continue;
            
            given.put(
              slotItem.getType(),
              given.getOrDefault(slotItem.getType(), 0) + slotItem.getAmount()
            );
          }
          
          for (Material type : costs.keySet()) {
            int count = given.getOrDefault(type, 0) - costs.get(type);
            if (count < 0) return;
          }
          
          for (int i = 0; i < 2; ++i) {
            ItemStack slotItem = minv.getItem(i);
            if (slotItem == null || slotItem.isEmpty()) continue;
            
            Material type = slotItem.getType();
            int count = costs.getOrDefault(type, 0) - slotItem.getAmount();
            
            if (count >= 0) {
              slotItem = null;
              costs.put(type, count);
            }
            else {
              slotItem.setAmount(-count);
              costs.put(type, 0);
            }
            
            minv.setItem(i, slotItem);
          }
          
          ugen.use(pl, null);
        }
      }
    }
  }
  
  public void handleInventoryOpen(InventoryOpenEvent ev) {
    if (!(ev.getPlayer() instanceof Player pl)) return;
    if (!PlayerUtils.shouldHandle(pl)) return;
    if (!isPlaying) return;
    
    if (ev.getInventory().getHolder() instanceof BlockInventoryHolder holder) {
      if (!LocUtils.canAccess(pl, holder.getBlock())) {
        pl.sendActionBar(TextUtils.$("game.banned.open-enemy-container"));
        ev.setCancelled(true);
        return;
      }
    }
    if (ev.getInventory().getHolder() instanceof DoubleChest dc) {
      if (!LocUtils.canAccess(pl, dc.getLocation().getBlock())) {
        pl.sendActionBar(TextUtils.$("game.banned.open-enemy-container"));
        ev.setCancelled(true);
        return;
      }
    }
    
    if (ev.getInventory() instanceof MerchantInventory) {
      if (!phase.isAfter(Phase.ShopOpened) || getSideData(pl).noShopTicks > 0) {
        pl.sendActionBar(TextUtils.$("game.banned.shop"));
        ev.setCancelled(true);
      }
    }
  }
  
  public void handleInventoryClose(InventoryCloseEvent ev) {
    if (!isPlaying) return;
    if (!(ev.getPlayer() instanceof Player pl)) return;
    
    SideData sd = getSideData(pl);
    if (sd == null) return;
    
    Location loc = sd.getEnderChestViewer(pl);
    if (loc == null) return;
    
    sd.removeEnderChestViewer(loc, pl);
    if (
      sd.enderChestViewers.get(loc).isEmpty()
    ) LocUtils.playChestAnimation(loc, false);
  }
  
  public void handleCrafting(PrepareItemCraftEvent ev) {
    for (ItemStack item : ev.getInventory().getMatrix()) {
      if (DestroyTheCore.itemsManager.isGen(item)) {
        ev.getInventory().setResult(ItemStack.empty());
        return;
      }
    }
  }
  
  public void handleRepair(PrepareAnvilEvent ev) {
    AnvilInventory inv = ev.getInventory();
    ItemStack left = inv.getItem(0);
    ItemStack right = inv.getItem(1);
    ItemStack result = ev.getResult();
    
    if (result == null || left == null || right == null) return;
    
    if (left.isRepairableBy(right) || left.getType() == right.getType()) {
      if (
        DestroyTheCore.itemsManager.isGen(
          left
        ) || DestroyTheCore.itemsManager.isGen(right)
      ) {
        ev.setResult(null);
      }
    }
  }
  
  public void handlePlayerMove(Player pl) {
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    PlayerData data = getPlayerData(pl);
    
    if (!isPlaying && LocUtils.inLobby(pl)) {
      BiConsumer<Region, Side> sideChecker = (region, side) -> {
        if (data.side.equals(side)) return;
        if (region == null || !region.contains(Pos.of(pl))) return;
      
//        if (isPlaying) {
//          pl.sendActionBar(TextUtils.$("game.side.join.bad-time"));
//          return;
//        }
        
        PlayerUtils.prefixedSend(
          pl,
          TextUtils.$(
            "game.side.join.success",
            List.of(Placeholder.component("side", side.titleComp()))
          )
        );
        getPlayerData(pl).join(side);
        enforceTeam(pl);
        DestroyTheCore.boardsManager.refresh(pl);
      };
      
      sideChecker.accept(lobby.joinRed, Side.RED);
      sideChecker.accept(lobby.joinGreen, Side.GREEN);
      sideChecker.accept(lobby.joinSpectator, Side.SPECTATOR);
    }
    
    if (isPlaying && map.restArea != null) {
      playerLoop: for (Player p : Bukkit.getOnlinePlayers()) {
        PlayerData pd = getPlayerData(p);
        if (pd.alive || pd.side == Side.SPECTATOR) continue;
        
        for (Pos rest : new Pos[]{
          DestroyTheCore.game.map.restArea, LocUtils.flip(
            DestroyTheCore.game.map.restArea
          ),
        }) {
          if (
            LocUtils.inLive(p)
              &&
              LocUtils.near(Pos.of(p), rest, 6)
          ) continue playerLoop;
        }
        
        p.damage(Double.MAX_VALUE);
      }
    }
    
    if (isPlaying && LocUtils.inLive(pl) && data.side != Side.SPECTATOR) {
      if (map.core != null && isInTruce()) {
        Pos enemyCore = LocUtils.enemySide(map.core, pl).center();
        
        if (LocUtils.near(Pos.of(pl), enemyCore, 30)) {
          pl.sendActionBar(TextUtils.$("game.truce.warning"));
          
          pl.addPotionEffect(
            new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true, false)
          );
          pl.addPotionEffect(
            new PotionEffect(PotionEffectType.SLOWNESS, 40, 2, true, false)
          );
        }
        
        if (LocUtils.near(Pos.of(pl), enemyCore, 20)) {
          pl.setHealth(1);
          PlayerUtils.teleportToSpawnPoint(pl);
          
          PlayerUtils.broadcast(
            TextUtils.$(
              "game.truce.sent-spawn",
              List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
            )
          );
        }
      }
      
      if (map.restArea != null && data.alive) {
        double restY = map.restArea.getY();
        
        if (pl.getY() >= restY - 2) {
          pl.sendActionBar(TextUtils.$("game.height-warning"));
        }
        
        if (pl.getY() >= restY) {
          if (!pl.hasPotionEffect(PotionEffectType.NAUSEA)) pl.addPotionEffect(
            new PotionEffect(PotionEffectType.NAUSEA, 10 * 20, 0, true, false)
          );
          pl.addPotionEffect(
            new PotionEffect(PotionEffectType.DARKNESS, 40, 0, true, false)
          );
          
          if (pl.getFreezeTicks() < 40 * 20) pl.setFreezeTicks(
            pl.getFreezeTicks() + 20
          );
        }
      }
    }
  }
  
  public void init() {
    bountyPrefix = TextUtils.$("game.bounty.prefix");
    
    sideData = new HashMap<>(
      Map.ofEntries(
        Map.entry(Side.RED, new SideData()),
        Map.entry(Side.GREEN, new SideData())
      )
    );
    
    recreateTeams();
    createScoreboards();
    hideRTScore();
  }
  
  public void setBothCoreMaterial(Material type) {
    for (Pos pos : new Pos[]{
      map.core, LocUtils.flip(map.core),
    }) {
      LocUtils.setLiveBlock(pos, type);
    }
  }
  
  public void setDiamonds(Material type) {
    for (Pos pos : map.diamonds) {
      LocUtils.setLiveBlock(pos, type);
    }
  }
  
  Map<BlockPos, Material> oresTypeCache = new HashMap<>();
  
  public void banOres(Side side) {
    for (Pos pos : map.ores) {
      Block block = LocUtils.live(pos).getBlock();
      if (block.getType() == Material.BEDROCK) continue;
      
      oresTypeCache.put(
        pos.toBlockPos(),
        block.getType()
      );
      LocUtils.setLiveBlock(
        LocUtils.selfSide(pos, side),
        Material.BEDROCK
      );
    }
  }
  
  public void unbanOres(Side side) {
    for (Pos pos : map.ores) {
      LocUtils.setLiveBlock(
        LocUtils.selfSide(pos, side),
        oresTypeCache.getOrDefault(pos.toBlockPos(), Material.GLASS)
      );
    }
  }
  
  public void summonShopVillagers() {
    for (Pos pos : map.shops) {
      Location loc = LocUtils.live(
        LocUtils.toSpawnPoint(pos)
      );
      loc.setY(loc.getBlockY());
      
      for (Villager e : loc.getNearbyEntitiesByType(Villager.class, 2)) {
        if (LocUtils.near(Pos.of(e), pos, 1)) e.remove();
      }
      
      offsetLoop: for (Vector offset : new Vector[]{
        new Vector(0, -2, 0), new Vector(1, 0, 0), new Vector(
          -1,
          0,
          0
        ), new Vector(0, 0, 1), new Vector(0, 0, -1)
      }) {
        for (Shop shop : shops) {
          if (shop.blockType != loc.clone().add(offset).getBlock().getType())
            continue;
          
          Location flipped = LocUtils.live(LocUtils.flip(Pos.of(loc)));
          
          villagers.add(
            new VillagerData(
              loc,
              shop.summonVillager(loc)
            )
          );
          villagers.add(
            new VillagerData(
              flipped,
              shop.summonVillager(flipped)
            )
          );
          
          break offsetLoop;
        }
      }
    }
  }
  
  public void scheduleStart() {
    startingTask = new BukkitRunnable() {
      int countdown = 5;
      
      @Override
      public void run() {
        if (countdown <= 0) {
          DestroyTheCore.inventoriesManager.reset();
          
          for (Player p : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.shouldHandle(p)) {
              p.clearActivePotionEffects();
              PlayerUtils.enforceNightVision(p);
              
              p.getInventory().clear();
              p.setExperienceLevelAndProgress(0);
              
              PlayerUtils.teleportToRestArea(p);
            }
            
            PlayerUtils.longTitleTimes(p);
            p.sendTitlePart(TitlePart.TITLE, TextUtils.$("general.title"));
            p.sendTitlePart(
              TitlePart.SUBTITLE,
              TextUtils.$("general.subtitle")
            );
            p.playSound(
              p.getLocation(),
              Sound.BLOCK_NOTE_BLOCK_BELL,
              1, // Volume
              2 // Pitch
            );
          }
          
          Bukkit.getScheduler().runTaskLater(
            DestroyTheCore.instance,
            Game.this::start,
            100
          );
          
          cancel();
          return;
        }
        
        for (Player p : Bukkit.getOnlinePlayers()) {
          PlayerUtils.normalTitleTimes(p);
          p.sendTitlePart(
            TitlePart.TITLE,
            Component.text(countdown).color(
              countdown <= 3 ? NamedTextColor.GOLD : NamedTextColor.GRAY
            )
          );
          p.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
          p.playSound(
            p.getLocation(),
            Sound.BLOCK_NOTE_BLOCK_BELL,
            1, // Volume
            1 // Pitch
          );
        }
        
        countdown--;
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 20);
  }
  
  public void cancelScheduleStart() {
    startingTask.cancel();
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.normalTitleTimes(p);
      p.sendTitlePart(TitlePart.TITLE, TextUtils.$("game.start-canceled"));
    }
  }
  
  public void start() {
    List<Pair<String, Object>> posChecks = List.of(
      Pair.of("rest-area", map.restArea),
      Pair.of("core", map.core),
      Pair.of("mission", map.mission),
      Pair.of("spawnpoints", map.spawnpoints),
      Pair.of("woods", map.woods),
      Pair.of("ores", map.ores),
      Pair.of("diamonds", map.diamonds),
      Pair.of("shops", map.shops)
    );
    boolean mapGood = true;
    for (Pair<String, Object> pair : posChecks) {
      String name = pair.first();
      Object stuff = pair.second();
      
      if (stuff == null || (stuff instanceof List<?> list && list.isEmpty())) {
        PlayerUtils.prefixedBroadcast(
          TextUtils.$(
            "game.missing-locs.message",
            List.of(
              Placeholder.unparsed(
                "location",
                TextUtils.stripColor(
                  TextUtils.$r("game.missing-locs.locs." + name)
                )
              )
            )
          )
        );
        mapGood = false;
      }
    }
    if (!mapGood) return;
    
//    DestroyTheCore.worldsManager.refreshForceLoadChunks();
    
    isPlaying = true;
    
    phase = Phase.CoreProtected;
    phaseTimer = 10 * 60 * 20;
    truceTimer = 0;
    
    showRTScore();
    
    setBothCoreMaterial(Material.BEDROCK);
    setDiamonds(Material.BEDROCK);
    
    summonShopVillagers();
    
    DestroyTheCore.ticksManager.ticksCount = 0;
    
    sideData.put(Side.RED, new SideData());
    sideData.put(Side.GREEN, new SideData());
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (!PlayerUtils.shouldHandle(p)) continue;
      
      PlayerData oldData = getPlayerData(p);
      playerData.put(
        p.getUniqueId(),
        new PlayerData(p, oldData.side, oldData.role)
      );
      
      PlayerUtils.refreshSpectatorAbilities(p);
      PlayerUtils.hideSpectators(p);
      PlayerUtils.respawn(p);
    }
    DestroyTheCore.boardsManager.refresh();
    
    // After 0: respawn, 1: give essential items
    CoreUtils.setTickOut(
      () -> DestroyTheCore.rolesManager.onPhaseChange(phase),
      2
    );
  }
  
  public void nextPhase() {
    phase = phase.next;
    phaseTimer = 10 * 60 * 20;
    
    if (phase == null) {
      checkWinner();
      return;
    }
    
    if (phase.equals(Phase.CoreProtected.next)) {
      setBothCoreMaterial(Material.END_STONE);
    }
    
    if (phase.equals(Phase.MissionsStarted)) {
      setDiamonds(Material.DIAMOND_ORE);
      DestroyTheCore.missionsManager.start();
    }
    if (phase.equals(Phase.MissionsStarted.next)) {
      DestroyTheCore.missionsManager.stop();
    }
    
    DestroyTheCore.rolesManager.onPhaseChange(phase);
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.longTitleTimes(p);
      p.sendTitlePart(TitlePart.TITLE, phase.title());
      p.sendTitlePart(TitlePart.SUBTITLE, phase.description());
      
      p.playSound(
        p.getLocation(),
        Sound.ENTITY_ENDER_DRAGON_AMBIENT,
        0.5f, // Volume
        1 // Pitch
      );
    }
    
    for (PlayerData data : playerData.values()) {
      data.setRespawnTime(Math.max(data.respawnTime, phase.minRespawnTime()));
    }
  }
  
  public void stop() {
    isPlaying = false;
    
    DestroyTheCore.missionsManager.stop();
    
    PlayerUtils.showAllPlayers();
    for (Player p : Bukkit.getOnlinePlayers())
      PlayerUtils.refreshSpectatorAbilities(
        p,
        false
      );
    
    hideRTScore();
    
    DestroyTheCore.boardsManager.refresh();
    
    CoreUtils.setTickOut(this::showCredits);
  }
  
  public void reset() {
    if (isPlaying) {
      stop();
      CoreUtils.setTickOut(this::reset, 2);
      return;
    }
    
    DestroyTheCore.inventoriesManager.reset();
    DestroyTheCore.missionsManager.forceStop();
    
    villagers.clear();
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.backToLobby(p);
      
      if (PlayerUtils.shouldHandle(p)) {
        p.getInventory().clear();
        p.getInventory().setItem(
          4,
          DestroyTheCore.itemsManager.gens.get(
            ItemsManager.ItemKey.CHOOSE_ROLE
          ).getItem()
        );
      }
    }
    
    CoreUtils.setTickOut(() -> {
      DestroyTheCore.worldsManager.cloneLive();
      DestroyTheCore.configManager.map.load();
    });
  }
  
  public void checkWinner() {
    int redHealth = getSideData(Side.RED).coreHealth, greenHealth = getSideData(
      Side.GREEN
    ).coreHealth;
    
    if (phase == null) {
      if (redHealth == greenHealth) {
        draw();
      }
      if (redHealth > greenHealth) {
        reflectResult(Side.RED, "more-health");
      }
      if (redHealth < greenHealth) {
        reflectResult(Side.GREEN, "more-health");
      }
    }
    else {
      if (greenHealth <= 0) {
        reflectResult(Side.RED, "destroyed");
      }
      else if (redHealth <= 0) {
        reflectResult(Side.GREEN, "destroyed");
      }
      else {
        return;
      }
    }
    
    stop();
  }
  
  static public class TopThree {
    int v1 = Integer.MIN_VALUE;
    int v2 = Integer.MIN_VALUE;
    int v3 = Integer.MIN_VALUE;
    
    List<Player> p1 = new ArrayList<>();
    List<Player> p2 = new ArrayList<>();
    List<Player> p3 = new ArrayList<>();
    
    public void add(Player p, int v) {
      if (v >= v1) {
        if (v > v1) {
          v3 = v2;
          p3 = p2;
          
          v2 = v1;
          p2 = p1;
          
          v1 = v;
          p1 = new ArrayList<>();
        }
        p1.add(p);
      }
      else if (v >= v2) {
        if (v > v2) {
          v3 = v2;
          p3 = p2;
          
          v2 = v;
          p2 = new ArrayList<>();
        }
        p2.add(p);
      }
      else if (v >= v3) {
        if (v > v3) {
          v3 = v;
          p3 = new ArrayList<>();
        }
        p3.add(p);
      }
    }
    
    String name;
    
    public TopThree(String name) {
      this.name = name;
    }
    
    void sendLine(Player pl, String id, List<Player> tops, int value) {
      if (tops.isEmpty()) return;
      
      PlayerUtils.send(
        pl,
        TextUtils.$(
          "game.credits.list." + id,
          List.of(
            Placeholder.component(
              "players",
              Component.join(
                JoinConfiguration.separator(Component.text(", ")),
                tops.stream().map(PlayerUtils::getName).toList().toArray(
                  new Component[0]
                )
              )
            ),
            Placeholder.component("count", Component.text(value))
          )
        )
      );
    }
    
    public void send(Player pl) {
      PlayerUtils.send(pl, TextUtils.$("game.credits." + name));
      PlayerUtils.send(pl, TextUtils.$("game.credits.list.empty"));
      sendLine(pl, "first", p1, v1);
      sendLine(pl, "second", p2, v2);
      sendLine(pl, "third", p3, v3);
      PlayerUtils.send(pl, TextUtils.$("game.credits.list.empty"));
      PlayerUtils.send(pl, TextUtils.$("game.credits.list.foot"));
    }
  }
  
  public void showCredits() {
    TopThree topAttackers = new TopThree("top-attackers"),
      topKillers = new TopThree("top-killers"),
      topDyers = new TopThree(
        "top-dyers"
      ), topMiners = new TopThree("top-miners");
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData data = getPlayerData(p);
      
      topAttackers.add(p, data.coreAttacks);
      topKillers.add(p, data.kills);
      topDyers.add(p, data.deaths);
      topMiners.add(p, data.ores.values().stream().reduce(0, Integer::sum));
    }
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.send(p, Component.empty());
      PlayerUtils.send(p, TextUtils.$("game.credits.title"));
      PlayerUtils.send(
        p,
        TextUtils.$(
          "game.credits.time",
          List.of(
            Placeholder.component(
              "time",
              CoreUtils.formatTimeComp(
                Math.ceilDiv(DestroyTheCore.ticksManager.ticksCount, 20),
                NamedTextColor.GREEN
              )
            )
          )
        )
      );
      PlayerUtils.send(p, Component.empty());
      topKillers.send(p);
      PlayerUtils.send(p, Component.empty());
      topDyers.send(p);
      PlayerUtils.send(p, Component.empty());
      topAttackers.send(p);
      PlayerUtils.send(p, Component.empty());
      topMiners.send(p);
      PlayerUtils.send(p, Component.empty());
      PlayerUtils.send(p, TextUtils.$("game.credits.thanks"));
      PlayerUtils.send(p, TextUtils.$("game.credits.sign"));
      PlayerUtils.send(p, Component.empty());
    }
  }
  
  public void draw() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerUtils.longTitleTimes(p);
      p.sendTitlePart(TitlePart.TITLE, TextUtils.$("game.result.titles.draw"));
      p.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
      p.playSound(
        p.getLocation(),
        Sound.BLOCK_ANVIL_USE,
        1, // Volume
        1 // Pitch
      );
      
      PlayerData data = getPlayerData(p);
      Stats stat = stats.get(p.getUniqueId());
      
      if (data.side.equals(Side.SPECTATOR)) continue;
      
      stat.addFromPlayerData(data);
    }
  }
  
  public void reflectResult(Side winner, String reasonKey) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData data = getPlayerData(p);
      Stats stat = stats.get(p.getUniqueId());
      
      String titleKey;
      Sound sound;
      if (data.side == Side.SPECTATOR) {
        titleKey = "spectator";
        sound = Sound.BLOCK_ANVIL_USE;
      }
      else if (data.side.equals(winner)) {
        titleKey = "win";
        sound = Sound.ITEM_GOAT_HORN_SOUND_1;
      }
      else {
        titleKey = "lose";
        sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
      }
      
      PlayerUtils.longTitleTimes(p);
      p.sendTitlePart(
        TitlePart.TITLE,
        TextUtils.$("game.result.titles." + titleKey).color(winner.color)
      );
      p.sendTitlePart(
        TitlePart.SUBTITLE,
        TextUtils.$(
          "game.result.subtitles." + reasonKey,
          List.of(Placeholder.component("side", winner.titleComp()))
        )
      );
      p.playSound(
        p.getLocation(),
        sound,
        1, // Volume
        1 // Pitch
      );
      
      if (data.side.equals(Side.SPECTATOR)) continue;
      
      stat.addFromPlayerData(data, data.side.equals(winner));
    }
  }
  
  public void onTick() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (
        PlayerUtils.wearingLeather(p)
          && p.getFreezeTicks() > 140
          && p.getFreezeTicks() % 20 == 1
      ) {
        p.damage(1, DamageSource.builder(DamageType.FREEZE).build());
      }
    }
    
    if (!isPlaying) return;
    
    if (phaseTimer <= 0) {
      nextPhase();
      if (!isPlaying) return;
    }
    
    phaseTimer--;
    
    if (isInTruce()) {
      truceTimer--;
    }
    
    for (Side side : sideData.keySet()) {
      SideData sd = getSideData(side);
      
      if (sd.clearInvCooldown > 0) {
        sd.clearInvCooldown--;
      }
      
      if (sd.extraDamageTicks > 0) {
        sd.extraDamageTicks--;
      }
      
      if (sd.noOresTicks > 0) {
        sd.noOresTicks--;
        
        if (DestroyTheCore.ticksManager.isSeconds()) {
          noOresBars.update(side);
        }
        
        if (sd.noOresTicks <= 0) {
          unbanOres(side);
          noOresBars.hide(side);
        }
      }
      
      if (sd.noShopTicks > 0) {
        sd.noShopTicks--;
        
        if (DestroyTheCore.ticksManager.isSeconds()) {
          noShopBars.update(side);
        }
        
        if (sd.noShopTicks <= 0) {
          noShopBars.hide(side);
        }
      }
      
      if (sd.isInvuln()) {
        sd.invulnTicks--;
        
        if (!sd.isInvuln()) {
          LocUtils.setLiveBlock(
            LocUtils.selfSide(map.core, side),
            Material.END_STONE
          );
        }
      }
    }
    
    if (isPlaying && DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        PlayerData data = getPlayerData(p);
        if (LocUtils.inLobby(p)) continue;
        
        if (
          p.getInventory().contains(
            Material.ENCHANTING_TABLE
          )
            || p.getInventory().contains(
              Material.ENDER_CHEST
            )
        ) {
          p.addPotionEffect(
            new PotionEffect(PotionEffectType.SLOWNESS, 30, 2, true, false)
          );
          p.addPotionEffect(
            new PotionEffect(PotionEffectType.WEAKNESS, 30, 9, true, false)
          );
        }
        
        if (
          DestroyTheCore.itemsManager.checkGen(
            p.getInventory().getItemInOffHand(),
            ItemsManager.ItemKey.SKILL_COOLDOWN_ASSIST
          )
        ) {
          data.extraSkillReload += TicksManager.updateRate;
        }
        
        if (
          p.getCooldown(Material.KNOWLEDGE_BOOK) - data.extraSkillReload <= 0
        ) {
          p.setCooldown(Material.KNOWLEDGE_BOOK, 0);
          data.extraSkillReload = 0;
        }
      }
    }
    
    updateVillagers();
    
    if (
      map.core != null
        && phase.isAfter(
          Phase.CoreWilting
        )
        && DestroyTheCore.ticksManager.ticksCount % (15 * 20) == 0
    ) {
      getSideData(Side.RED).directAttackCore();
      getSideData(Side.GREEN).directAttackCore();
      checkWinner();
      
      for (Pos pos : new Pos[]{
        map.core, LocUtils.flip(map.core)
      }) {
        new ParticleBuilder(Particle.WITCH)
          .allPlayers()
          .location(LocUtils.live(pos.center()).add(0, -0.2, 0))
          .offset(0, 0, 0)
          .count(20)
          .extra(1)
          .spawn();
      }
      
      for (Player p : Bukkit.getOnlinePlayers()) {
        p.playSound(
          p.getLocation(),
          Sound.ENTITY_WITHER_HURT,
          1, // Volume
          1 // Pitch
        );
      }
    }
    
    if (map.core != null) {
      for (Player p : DestroyTheCore.worldsManager.live.getPlayers()) {
        PlayerUtils.rrt(p);
      }
    }
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData d = getPlayerData(p);
      if (d.side == Side.SPECTATOR) continue;
      
      if (d.shoutCooldown > 0) d.shoutCooldown--;
      
      d.role.onTick(p);
    }
  }
  
  public void onParticleTick() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (!isPlaying) continue;
      if (LocUtils.inLobby(p)) continue;
      if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
      
      PlayerData data = getPlayerData(p);
      
      if (
        p.getInventory().contains(
          Material.ENCHANTING_TABLE
        )
          || p.getInventory().contains(
            Material.ENDER_CHEST
          )
      ) {
        ParticleUtils.dust(
          PlayerUtils.all(),
          p.getEyeLocation().add(0, 0.6, 0),
          Color.RED
        );
      }
      
      if (data.killStreak >= 10) {
        ParticleUtils.dust(
          PlayerUtils.all(),
          p.getEyeLocation().add(0, 0.6, 0),
          Color.YELLOW
        );
      }
    }
    
    if (isPlaying && map.core != null) {
      for (Pos pos : new Pos[]{
        map.core, LocUtils.flip(map.core)
      }) {
        new ParticleBuilder(Particle.ENCHANT)
          .allPlayers()
          .location(
            LocUtils.live(pos.center()).add(0, 0.6, 0)
          )
          .count(2)
          .offset(0.3, 0.2, 0.3)
          .extra(1.5)
          .spawn();
      }
    }
  }
}
