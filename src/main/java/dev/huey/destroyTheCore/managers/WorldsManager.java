package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
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
  
  public World createTemplateWorld() {
    return Bukkit.createWorld(getCreator("template-" + mapName));
  }
  
  public World createLiveWorld() {
    return Bukkit.createWorld(getCreator("live"));
  }
  
  BossBar templateWarningBar;
  
  public void init() {
    lobby = Bukkit.getWorlds().getFirst();
  }
  
  public void clearLiveWorldPlayers() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getWorld().equals(live)) {
        p.teleport(
          CoreUtils.def(
            DestroyTheCore.game.lobby.spawn,
            new Location(lobby, 0, 100, 0)
          )
        );
      }
    }
  }
  
  public void deleteLive() {
    if (live != null && live.getPlayerCount() > 0) {
      clearLiveWorldPlayers();
      CoreUtils.setTickOut(this::deleteLive);
      return;
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
    
    Bukkit.unloadWorld("template-" + mapName, true);
    
    PlayerUtils.prefixedNotice(TextUtils.$("world.deleting-live"));
    deleteLive();
    
    File sourceFolder = new File(
      Bukkit.getWorldContainer(),
      "template-" + mapName
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
    template = createTemplateWorld();
    
    live = createLiveWorld();
    PlayerUtils.prefixedNotice(TextUtils.$("world.copied"));
    
    isReady = true;
  }
  
//  public void refreshForceLoadChunks() {
//    template = createTemplateWorld();
//    live = createLiveWorld();
//
//    Set<Chunk> toLoad = new HashSet<>();
//    Consumer<Location> addForceLoad = loc -> {
//      if (loc == null) return;
//
//      toLoad.add(loc.getChunk());
//      toLoad.add(LocUtils.flip(loc).getChunk());
//      toLoad.add(LocUtils.live(loc).getChunk());
//      toLoad.add(LocUtils.live(LocUtils.flip(loc)).getChunk());
//    };
//
//    addForceLoad.accept(DestroyTheCore.game.map.restArea);
//    addForceLoad.accept(DestroyTheCore.game.map.core);
//    for (Location loc : DestroyTheCore.game.map.spawnpoints)
//      addForceLoad.accept(
//        loc
//      );
//
//    for (Chunk chunk : toLoad) {
//      chunk.addPluginChunkTicket(DestroyTheCore.instance);
//    }
//
//    for (Chunk chunk : template.getForceLoadedChunks()) {
//      if (!toLoad.contains(chunk)) chunk.removePluginChunkTicket(
//        DestroyTheCore.instance
//      );
//    }
//    for (Chunk chunk : live.getForceLoadedChunks()) {
//      if (!toLoad.contains(chunk)) chunk.removePluginChunkTicket(
//        DestroyTheCore.instance
//      );
//    }
//  }
  
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
