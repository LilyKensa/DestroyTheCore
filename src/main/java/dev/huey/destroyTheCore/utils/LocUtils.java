package dev.huey.destroyTheCore.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.Pos;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LocUtils {
  static public boolean isSameWorld(World world1, World world2) {
    return world1.equals(world2);
  }
  
  static public boolean isSameWorld(Location loc1, Location loc2) {
    return isSameWorld(loc1.getWorld(), loc2.getWorld());
  }
  
  static public boolean isSameWorld(Entity e1, Entity e2) {
    return e1.getWorld().equals(e2.getWorld());
  }
  
  static public boolean isSameBlock(Location loc1, Location loc2) {
    return (isSameWorld(
      loc1,
      loc2
    )
      && loc1.getBlockX() == loc2.getBlockX()
      && loc1.getBlockY() == loc2
        .getBlockY()
      && loc1.getBlockZ() == loc2.getBlockZ());
  }
  
  static public Location lobby(Pos pos) {
    return pos.toLoc(DestroyTheCore.worldsManager.lobby);
  }
  
  static public Location template(Pos pos) {
    return pos.toLoc(DestroyTheCore.worldsManager.template);
  }
  
  static public Location live(Pos pos) {
    return pos.toLoc(DestroyTheCore.worldsManager.live);
  }
  
  static public boolean inLobby(Location loc) {
    return isSameWorld(loc.getWorld(), DestroyTheCore.worldsManager.lobby);
  }
  
  static public boolean inLobby(Entity e) {
    return inLobby(e.getLocation());
  }
  
  static public boolean inTemplate(Location loc) {
    return isSameWorld(loc.getWorld(), DestroyTheCore.worldsManager.template);
  }
  
  static public boolean inTemplate(Entity e) {
    return inTemplate(e.getLocation());
  }
  
  static public boolean inLive(Location loc) {
    return isSameWorld(loc.getWorld(), DestroyTheCore.worldsManager.live);
  }
  
  static public boolean inLive(Entity e) {
    return inLive(e.getLocation());
  }
  
  static Pos compareEachAxis(
    Pos loc1, Pos loc2, BiFunction<Double, Double, Double> func
  ) {
    return new Pos(
      func.apply(loc1.getX(), loc2.getX()),
      func.apply(loc1.getY(), loc2.getY()),
      func.apply(loc1.getZ(), loc2.getZ())
    );
  }
  
  static public Pos min(Pos first, Pos second) {
    return compareEachAxis(first, second, Math::min);
  }
  
  static public Pos max(Pos first, Pos second) {
    return compareEachAxis(first, second, Math::max);
  }
  
  static public Location toBlockCenter(Location loc) {
    return new Location(
      loc.getWorld(),
      loc.getBlockX() + 0.5,
      loc.getBlockY() + 0.5,
      loc.getBlockZ() + 0.5,
      loc.getYaw(),
      loc.getPitch()
    );
  }
  
  /**
   * {@link #toBlockCenter}, but it's 0.25 up from the ground, instead of 0.5
   */
  static public Pos toSpawnPoint(Pos pos) {
    pos = pos.center().add(0, -0.25, 0);
    pos.setRotation(CoreUtils.snapAngle(pos.getYaw()), 5);
    return pos;
  }
  
  static public boolean closeEnough(Location target, Location source) {
    return isSameWorld(target, source)
      && Math.abs(target.getX() - source.getX()) < 0.6
      && Math.abs(target.getY() - source.getY()) < 0.6
      && Math.abs(target.getZ() - source.getZ()) < 0.6;
  }
  
  static public Location hitboxCenter(Entity e) {
    return e.getBoundingBox().clone().getCenter().toLocation(e.getWorld());
  }
  
  /** Ender chest animation, as we use custom ender chests */
  static public void playChestAnimation(Location loc, boolean open) {
    Block block = loc.getBlock();
    
    PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
      PacketType.Play.Server.BLOCK_ACTION
    );
    
    // 1. Block Position (Location)
    packet.getBlockPositionModifier().write(
      0,
      new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
    );
    
    // 2. Action ID (1 for chest, door, etc.)
    packet.getIntegers().write(0, 1);
    
    // 3. Action Parameter (1 for open, 0 for close)
    packet.getIntegers().write(1, open ? 1 : 0);
    
    // 4. Block Type (Material enum) - ProtocolLib handles conversion to NMS Block
    packet.getBlocks().write(0, block.getType());
    
    // Send the packet to all players in the world who are near the block
    for (Player p : loc.getWorld().getPlayers()) {
      if (near(Pos.of(p), Pos.of(loc), 64)) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
        p.playSound(
          loc,
          open ? Sound.BLOCK_ENDER_CHEST_OPEN : Sound.BLOCK_ENDER_CHEST_CLOSE,
          SoundCategory.BLOCKS,
          1, // Volume
          1 // Pitch
        );
      }
    }
  }
  
  /** Do something several times in a circle */
  static public void ring(
    Location centerLoc, double radius, int count, Consumer<Location> task
  ) {
    double step = (2 * Math.PI) / count;
    
    for (int i = 0; i < count; i++) {
      double angle = centerLoc.getYaw() * Math.PI / 180 + i * step;
      
      double x = Math.cos(angle) * radius;
      double z = Math.sin(angle) * radius;
      
      Location loc = centerLoc.clone().add(x, 0, z);
      task.accept(loc);
    }
  }
  
  static public void ring(
    Location centerLoc, double radius, Consumer<Location> task
  ) {
    ring(centerLoc, radius, 16, task);
  }
  
  /** Set a block at specific location, but in live world */
  static public void setLiveBlock(Pos pos, Material type) {
    Location loc = live(pos.center());
    
    loc.getBlock().setType(type);
    
    ParticleUtils.cloud(
      PlayerUtils.all(),
      loc
    );
  }
  
  static public boolean near(Pos a, Pos b, double dist) {
    return a.center().distSq(b.center()) <= dist * dist;
  }
  
  static public boolean near(Entity a, Entity b, double dist) {
    return near(Pos.of(a.getLocation()), Pos.of(b), dist);
  }
  
  static public boolean nearAnyCore(Location loc, int dist) {
    if (!isSameWorld(loc.getWorld(), DestroyTheCore.worldsManager.live))
      return false;
    
    Pos posRed = DestroyTheCore.game.map.core;
    if (posRed == null) return false;
    
    for (Pos pos : new Pos[]{
      posRed, LocUtils.flip(posRed)
    }) {
      if (near(Pos.of(loc), pos, dist)) return true;
    }
    
    return false;
  }
  
  static public boolean nearSpawn(Location loc) {
    Pos pos = Pos.of(loc);
    
    for (Pos spawnRed : DestroyTheCore.game.map.spawnpoints) {
      for (Pos spawn : new Pos[]{
        spawnRed, LocUtils.flip(spawnRed)
      }) {
        int sx = pos.floorX();
        int sy = pos.floorY();
        int sz = pos.floorZ();
        int tx = spawn.floorX();
        int ty = spawn.floorY();
        int tz = spawn.floorZ();
        
        if (
          sx >= tx - 1
            && sx <= tx + 1
            && sy >= ty
            && sy <= ty + 2
            && sz >= tz - 1
            && sz <= tz + 1
        ) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  /** Flip X & Z, useful for red / green locations conversion */
  static public Pos flip(Pos oldPos, boolean flip) {
    Pos pos = oldPos.clone();
    
    if (flip) {
      pos.setYaw(pos.getYaw() + 180);
      pos.setX(-pos.getX() + (pos.getX() % 1 == 0 ? 0 : 1));
      pos.setZ(-pos.getZ() + (pos.getZ() % 1 == 0 ? 0 : 1));
    }
    
    return pos;
  }
  
  static public Pos flip(Pos pos) {
    return flip(pos, true);
  }
  
  /** Flip if {@code side} is green */
  static public Pos selfSide(Pos pos, Game.Side side) {
    return flip(pos, side.equals(Game.Side.GREEN));
  }
  
  static public Pos selfSide(Pos pos, Player pl) {
    return selfSide(pos, DestroyTheCore.game.getPlayerData(pl).side);
  }
  
  /** Flip if {@code side} is red */
  static public Pos enemySide(Pos pos, Game.Side side) {
    return flip(pos, !side.equals(Game.Side.GREEN));
  }
  
  static public Pos enemySide(Pos pos, Player pl) {
    return enemySide(pos, DestroyTheCore.game.getPlayerData(pl).side);
  }
  
  /** If a block is in their own half of the map */
  static public boolean canAccess(Player pl, Block block) {
    if (DestroyTheCore.game.map.core == null) return true;
    if (
      !LocUtils.inLive(block.getLocation())
    ) return true;
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    double selfDistSq = LocUtils.toBlockCenter(
      block.getLocation()
    ).distanceSquared(
      LocUtils.live(
        LocUtils.selfSide(DestroyTheCore.game.map.core, data.side)
      )
    );
    double enemyDistSq = LocUtils.toBlockCenter(
      block.getLocation()
    ).distanceSquared(
      LocUtils.live(
        LocUtils.enemySide(DestroyTheCore.game.map.core, data.side)
      )
    );
    
    return selfDistSq <= enemyDistSq;
  }
}
