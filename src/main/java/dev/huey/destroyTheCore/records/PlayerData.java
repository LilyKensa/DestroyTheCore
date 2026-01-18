package dev.huey.destroyTheCore.records;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerData {
  
  /** Constants */
  public static final int minRespawnTime = 5, maxRespawnTime = 180,
    killPunishment = 2, corePunishment = 5,
    shoutCooldownDuration = 10 * 20, rrtDuration = 5 * 20; // Reduce respawn time
  
  public Player owner;
  public Game.Side side = Game.Side.SPECTATOR;
  public Role role = DestroyTheCore.rolesManager.roles.get(
    RolesManager.RoleKey.DEFAULT
  );
  public boolean alive = false;
  
  public int respawnTime = minRespawnTime, extraSkillReload = 0,
    rrtProgress = -20, shoutCooldown = 0, quizQuota = 10,
    lotteryShift = 0, killStreak = 0, kills = 0, deaths = 0, coreAttacks = 0;
  public Map<Material, Integer> ores = new HashMap<>();
  
  public PlayerData(Player owner) {
    this.owner = owner;
  }
  
  public PlayerData(Player owner, Game.Side side, Role role) {
    this(owner);
    this.side = side;
    this.role = role;
  }
  
  public void join(Game.Side side) {
    this.side = side;
  }
  
  public void setRole(Role role) {
    this.role = role;
  }
  
  public void setRespawnTime(int time) {
    this.respawnTime = Math.min(Math.max(minRespawnTime, time), maxRespawnTime);
    DestroyTheCore.game.enforceRTScore(owner);
  }
  
  public void addRespawnTime(int plus) {
    setRespawnTime(respawnTime + plus);
  }
  
  public void revive() {
    alive = true;
    if (DestroyTheCore.game.phase != null) setRespawnTime(
      DestroyTheCore.game.phase.minRespawnTime()
    );
  }
  
  public void kill() {
    alive = false;
    addDeath();
  }
  
  public void addKill() {
    kills++;
    killStreak++;
    addRespawnTime(killPunishment);
  }
  
  public void addDeath() {
    deaths++;
    killStreak = 0;
  }
  
  public void addCoreAttack() {
    coreAttacks++;
    addRespawnTime(corePunishment);
  }
  
  public void addOre(Material type) {
    ores.put(type, ores.getOrDefault(type, 0) + 1);
  }
  
  public boolean isGaming() {
    return alive && side != Game.Side.SPECTATOR;
  }
}
