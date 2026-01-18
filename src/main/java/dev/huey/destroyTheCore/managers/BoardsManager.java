package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import fr.mrmicky.fastboard.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.function.Function;

public class BoardsManager {
  
  final Map<UUID, FastBoard> boards = new HashMap<>();
  
  /** Call this whenever you change any of the stuff on the board */
  public void refresh(Player pl) {
    FastBoard board = boards.get(pl.getUniqueId());
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    boolean inGame = data.side != Game.Side.SPECTATOR;
    
    List<String> lines = new ArrayList<>();
    
    lines.addAll(
      List.of(
        "",
        TextUtils.$r(
          "board.team",
          List.of(Placeholder.component("side", data.side.titleComp()))
        )
      )
    );
    
    if (inGame) {
      lines.add(
        TextUtils.$r(
          "board.role",
          List.of(Placeholder.unparsed("role", data.role.name))
        )
      );
    }
    
    if (DestroyTheCore.game.isPlaying) {
      Game.Side firstSide = inGame ? data.side : Game.Side.RED;
      SideData side1 = DestroyTheCore.game.getSideData(firstSide),
        side2 = DestroyTheCore.game.getSideData(firstSide.opposite());
      String col1 = firstSide == Game.Side.RED ? "§c" : "§a",
        col2 = firstSide == Game.Side.RED ? "§a" : "§c";
      
      Function<SideData, String> coreInvulnDisplay = sideData -> {
        if (!sideData.isInvuln()) return "";
        return " §7[%s§7]".formatted(
          CoreUtils.formatTime(Math.ceilDiv(sideData.invulnTicks, 20), "§f")
        );
      };
      
      lines.addAll(
        List.of(
          "",
          TextUtils.$r(
            "board.phase",
            List.of(
              Placeholder.component(
                "index",
                Component.text(DestroyTheCore.game.phase.index + 1)
              ),
              Placeholder.component(
                "title",
                DestroyTheCore.game.phase.displayName().color(null)
              )
            )
          ),
          TextUtils.$r(
            "board.countdown",
            List.of(
              Placeholder.unparsed(
                "time",
                CoreUtils.formatTime(
                  Math.ceilDiv(DestroyTheCore.game.phaseTimer, 20),
                  "§e"
                )
              )
            )
          ),
          "",
          TextUtils.$r(
            "board.health",
            List.of(
              Placeholder.unparsed(
                "a",
                col1 + side1.coreHealth + coreInvulnDisplay.apply(side1)
              ),
              Placeholder.unparsed(
                "b",
                col2 + side2.coreHealth + coreInvulnDisplay.apply(side2)
              )
            )
          )
        )
      );
      
      if (DestroyTheCore.game.isInTruce()) {
        lines.add(
          TextUtils.$r(
            "board.truce",
            List.of(
              Placeholder.unparsed(
                "time",
                CoreUtils.formatTime(
                  Math.ceilDiv(DestroyTheCore.game.truceTimer, 20),
                  "§e"
                )
              )
            )
          )
        );
      }
      
      lines.add("");
      
      if (inGame) {
        lines.addAll(
          List.of(
            TextUtils.$r(
              "board.sin",
              List.of(
                Placeholder.component("value", Component.text(data.respawnTime))
              )
            ),
            TextUtils.$r(
              "board.kd",
              List.of(
                Placeholder.component("kills", Component.text(data.kills)),
                Placeholder.component("deaths", Component.text(data.deaths))
              )
            ),
            TextUtils.$r(
              "board.attacks",
              List.of(
                Placeholder.component("value", Component.text(data.coreAttacks))
              )
            ),
            TextUtils.$r(
              "board.ores",
              List.of(
                Placeholder.component(
                  "value",
                  Component.text(
                    data.ores.values().stream().reduce(0, Integer::sum)
                  )
                )
              )
            ),
            ""
          )
        );
      }
    }
    else {
      Stats stats = DestroyTheCore.game.stats.get(pl.getUniqueId());
      
      lines.addAll(
        List.of(
          "",
          TextUtils.$r(
            "board.wins",
            List.of(
              Placeholder.component("wins", Component.text(stats.wins)),
              Placeholder.component("all", Component.text(stats.games)),
              Placeholder.unparsed(
                "ratio",
                stats.games == 0 ? "0.0" : CoreUtils.toFixed(
                  100D * stats.wins / stats.games)
              )
            )
          ),
          TextUtils.$r(
            "board.kd",
            List.of(
              Placeholder.component("kills", Component.text(stats.kills)),
              Placeholder.component("deaths", Component.text(stats.deaths))
            )
          ),
          TextUtils.$r(
            "board.attacks",
            List.of(
              Placeholder.component("value", Component.text(stats.coreAttacks))
            )
          ),
          TextUtils.$r(
            "board.ores",
            List.of(
              Placeholder.component(
                "value",
                Component.text(
                  stats.ores.values().stream().reduce(0, Integer::sum)
                )
              )
            )
          ),
          ""
        )
      );
    }
    
    board.updateLines(lines);
  }
  
  public void refresh() {
    for (Player p : Bukkit.getOnlinePlayers()) refresh(p);
  }
  
  public void onUITick() {
    for (Player pl : Bukkit.getOnlinePlayers()) {
      if (boards.containsKey(pl.getUniqueId())) refresh(pl);
    }
  }
  
  public void onPlayerJoin(PlayerJoinEvent ev) {
    Player pl = ev.getPlayer();
    
    FastBoard board = new FastBoard(pl);
    board.updateTitle(TextUtils.$r("board.title"));
    
    this.boards.put(pl.getUniqueId(), board);
    refresh(pl);
  }
  
  public void onPlayerQuit(PlayerQuitEvent ev) {
    Player player = ev.getPlayer();
    
    FastBoard board = this.boards.remove(player.getUniqueId());
    if (board != null) board.delete();
  }
}
