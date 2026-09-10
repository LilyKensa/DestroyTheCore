package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import net.kyori.adventure.bossbar.BossBar;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class WorldsManager {
  
  static public class VoidGenerator extends ChunkGenerator {
    
    @Override
    public void generateNoise(
      WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData
    ) {
      chunkData.setRegion(
        0,
        chunkData.getMinHeight(),
        0, // Min x, y, z
        16,
        chunkData.getMaxHeight(),
        16, // Max x, y, z
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
  
  /** All world is loaded properly */
  public boolean isReady = false;
  public String mapName = "castle";
  
  public World lobby, template, live;
  
  public WorldCreator getCreator(String name) {
    return new WorldCreator(name).generator(new VoidGenerator());
  }
  
  public World getOrCreate(String name) {
    World world = Bukkit.getWorld(name);
    return world == null
      ? Bukkit.createWorld(getCreator(name))
      : world;
  }
  
  public World fetchTemplate() {
    return getOrCreate(
      ConfigManager.templateWorldPrefix + mapName
    );
  }
  
  public World fetchLive() {
    return getOrCreate("live");
  }
  
  BossBar templateWarningBar;
  
  public void init() {
    lobby = Bukkit.getWorlds().getFirst();
    
    lobby.setGameRule(GameRules.ADVANCE_TIME, false);
    lobby.setGameRule(GameRules.ADVANCE_WEATHER, false);
    lobby.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
    lobby.setGameRule(GameRules.SPREAD_VINES, false);
    lobby.setGameRule(GameRules.SPAWN_MOBS, false);
    lobby.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
    lobby.setGameRule(GameRules.RESPAWN_RADIUS, 0);
    lobby.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
    lobby.setGameRule(GameRules.COMMAND_BLOCK_OUTPUT, false);
  }
  
  public void clearLiveWorldPlayers() {
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
  
  public void deleteLive() {
    if (live != null) {
      live.removePluginChunkTickets(DTC.instance);
      
      if (live.getPlayerCount() > 0) {
        clearLiveWorldPlayers();
        CoreUtils.setTickOut(this::deleteLive);
        return;
      }
    }
    
    Bukkit.unloadWorld("live", false);
    
    File targetFolder = new File(Bukkit.getWorldContainer(), "live");
    if (!targetFolder.exists()) return;
    
    try {
      FileUtils.deleteDirectory(targetFolder);
    }
    catch (IOException e) {
      CoreUtils.error("Cannot delete live world!");
    }
  }
  
  public void cloneLive() {
    isReady = false;
    
    Bukkit.unloadWorld(ConfigManager.templateWorldPrefix + mapName, true);
    
    PlayerUtils.prefixedNotice(TextUtils.$("world.deleting-live"));
    deleteLive();
    
    File sourceFolder = new File(
      Bukkit.getWorldContainer(),
      ConfigManager.templateWorldPrefix + mapName
    );
    File targetFolder = new File(Bukkit.getWorldContainer(), "live");
    PlayerUtils.prefixedNotice(TextUtils.$("world.copying-template"));
    
    try {
      FileUtils.copyDirectory(sourceFolder, targetFolder);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    new File(targetFolder, "uid.dat").delete();
    
    new File(targetFolder, "session.lock").delete();
    template = fetchTemplate();
    
    live = fetchLive();
    live.addPluginChunkTicket(0, 0, DTC.instance);
    
    PlayerUtils.prefixedNotice(TextUtils.$("world.copied"));
    isReady = true;
  }
  
  public void onPlayerChangeWorld(Player pl, World world) {
    if (template == null) return;
    
    if (templateWarningBar == null) templateWarningBar = BossBar.bossBar(
      TextUtils.$("world.template-warning"),
      1.0F,
      BossBar.Color.RED,
      BossBar.Overlay.PROGRESS
    );
    
    if (LocUtils.isSameWorld(world, template)) {
      pl.showBossBar(templateWarningBar);
    }
    else {
      pl.hideBossBar(templateWarningBar);
    }
  }
}
