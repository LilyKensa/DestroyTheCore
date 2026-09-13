package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import net.kyori.adventure.bossbar.BossBar;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class WorldsManager {
  
  public static class VoidGenerator extends ChunkGenerator {
    
    @Override
    public void generateNoise(
      WorldInfo worldInfo,
      Random random,
      int x,
      int z,
      ChunkData chunkData
    ) {
      chunkData.setRegion(
        0,
        chunkData.getMinHeight(),
        0,
        16,
        chunkData.getMaxHeight(),
        16,
        Material.AIR
      );
    }
    
    @Override
    public boolean shouldGenerateNoise() {
      return false;
    }
    
    @Override
    public boolean shouldGenerateSurface() {
      return false;
    }
    
    @Override
    public boolean shouldGenerateCaves() {
      return false;
    }
    
    @Override
    public boolean shouldGenerateDecorations() {
      return false;
    }
    
    @Override
    public boolean shouldGenerateMobs() {
      return false;
    }
    
    @Override
    public boolean shouldGenerateStructures() {
      return false;
    }
    
    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
      return new Location(world, 0, 80, 0);
    }
  }
  
  /** All worlds loaded properly */
  public boolean isReady = false;
  public String mapName = "castle";
  
  public World lobby, template, live;
  
  final NamespacedKey liveKey = new NamespacedKey(DTC.instance, "live");
  BossBar templateWarningBar;
  
  /**
   * Constructs a NamespacedKey for custom templates safely adhering to
   * namespace syntax.
   */
  public NamespacedKey getTemplateKey(String map) {
    String keyName = (ConfigManager.templateWorldPrefix + map).toLowerCase(
      Locale.ROOT
    );
    return new NamespacedKey(DTC.instance, keyName);
  }
  
  /**
   * Creates a WorldCreator using the updated 26.2 NamespacedKey standard.
   */
  public WorldCreator getCreator(NamespacedKey key) {
    return WorldCreator.ofKey(key).generator(new VoidGenerator());
  }
  
  public World getOrCreate(NamespacedKey key) {
    World world = Bukkit.getWorld(key);
    return world == null ? Bukkit.createWorld(getCreator(key)) : world;
  }
  
  public World fetchTemplate() {
    return getOrCreate(getTemplateKey(mapName));
  }
  
  public World fetchLive() {
    return getOrCreate(liveKey);
  }
  
  public void init() {
    lobby = Bukkit.getWorlds().getFirst();
    
    lobby.setGameRule(GameRules.ADVANCE_TIME, false);
    lobby.setGameRule(GameRules.ADVANCE_WEATHER, false);
    lobby.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
    lobby.setGameRule(GameRules.SPREAD_VINES, false);
    lobby.setGameRule(GameRules.SPAWN_MOBS, false);
    lobby.setGameRule(GameRules.SPAWN_MONSTERS, false);
    lobby.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
    lobby.setGameRule(GameRules.RESPAWN_RADIUS, 0);
    lobby.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
    lobby.setGameRule(GameRules.COMMAND_BLOCK_OUTPUT, false);
  }
  
  public void clearLiveWorldPlayers() {
    if (live == null) return;
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getWorld().equals(live)) {
        p.teleport(
          CoreUtils.def(
            DTC.game.lobby.spawn.toLoc(lobby),
            new Location(lobby, 0, 100, 0)
          )
        );
      }
    }
  }
  
  /**
   * Unloads and cleans up the live world files.
   */
  public void deleteLive() {
    World currentLive = Bukkit.getWorld(liveKey);
    
    if (currentLive != null) {
      currentLive.removePluginChunkTickets(DTC.instance);
      
      if (currentLive.getPlayerCount() > 0) {
        clearLiveWorldPlayers();
        CoreUtils.setTickOut(this::deleteLive);
        return;
      }
      
      File folder = currentLive.getWorldFolder();
      Bukkit.unloadWorld(currentLive, false);
      deleteFolder(folder);
      this.live = null;
    }
    else {
      File targetFolder = resolveWorldFolder(liveKey);
      if (targetFolder.exists()) {
        deleteFolder(targetFolder);
      }
    }
  }
  
  public void cloneLive() {
    isReady = false;
    
    NamespacedKey templateKey = getTemplateKey(mapName);
    World templateWorld = Bukkit.getWorld(templateKey);
    File templateFolder;
    
    if (templateWorld != null) {
      templateFolder = templateWorld.getWorldFolder();
      Bukkit.unloadWorld(templateWorld, true);
    }
    else {
      templateFolder = resolveWorldFolder(templateKey);
    }
    
    PlayerUtils.prefixedNotice(TextUtils.$("world.deleting-live"));
    deleteLive();
    
    PlayerUtils.prefixedNotice(TextUtils.$("world.copying-template"));
    File liveFolder = resolveWorldFolder(liveKey);
    
    try {
      FileUtils.copyDirectory(templateFolder, liveFolder);
      
      new File(liveFolder, "uid.dat").delete();
      new File(liveFolder, "session.lock").delete();
      FileUtils.deleteDirectory(
        new File(liveFolder, "data" + File.separator + "paper")
      );
    }
    catch (IOException e) {
      CoreUtils.error(
        "Failed to copy world files from template: " + e.getMessage()
      );
      return;
    }
    
    template = fetchTemplate();
    live = fetchLive();
    
    live.addPluginChunkTicket(0, 0, DTC.instance);
    
    PlayerUtils.prefixedNotice(TextUtils.$("world.copied"));
    isReady = true;
  }
  
  /**
   * Resolves the world folder dynamically using Bukkit's configured container.
   */
  private File resolveWorldFolder(NamespacedKey key) {
    return new File(
      lobby.getWorldFolder().getParentFile().getParentFile(),
      key.getNamespace() + File.separator + key.getKey()
    );
  }
  
  private void deleteFolder(File folder) {
    if (folder == null || !folder.exists()) return;
    try {
      FileUtils.deleteDirectory(folder);
    }
    catch (IOException e) {
      CoreUtils.error("Cannot delete live world folder: " + folder.getName());
    }
  }
  
  public void onPlayerChangeWorld(Player pl, World world) {
    if (template == null) return;
    
    if (templateWarningBar == null) {
      templateWarningBar = BossBar.bossBar(
        TextUtils.$("world.template-warning"),
        1.0F,
        BossBar.Color.RED,
        BossBar.Overlay.PROGRESS
      );
    }
    
    if (LocUtils.isSameWorld(world, template)) {
      pl.showBossBar(templateWarningBar);
    }
    else {
      pl.hideBossBar(templateWarningBar);
    }
  }
  
  public void exit() {
    deleteLive();
  }
}
