package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import fr.mrmicky.fastboard.FastBoard;
import java.util.*;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BoardsManager {
  
  final Map<UUID, FastBoard> boards = new HashMap<>();
  
  /** Call this whenever you change any of the stuff on the board */
  public void refresh(Player pl) {
    FastBoard board = boards.get(pl.getUniqueId());
    
    PlayerData data = DTC.game.getPlayerData(pl);
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
    
    if (DTC.game.isPlaying) {
      Game.Side firstSide = inGame ? data.side : Game.Side.RED;
      SideData side1 = DTC.game.getSideData(firstSide),
        side2 = DTC.game.getSideData(firstSide.opposite());
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
                Component.text(DTC.game.phase.index + 1)
              ),
              Placeholder.component(
                "title",
                DTC.game.phase.displayName().color(null)
              )
            )
          ),
          TextUtils.$r(
            "board.countdown",
            List.of(
              Placeholder.unparsed(
                "time",
                CoreUtils.formatTime(
                  Math.ceilDiv(DTC.game.phaseTimer, 20),
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
      
      if (DTC.game.isInTruce()) {
        lines.add(
          TextUtils.$r(
            "board.truce",
            List.of(
              Placeholder.unparsed(
                "time",
                CoreUtils.formatTime(
                  Math.ceilDiv(DTC.game.truceTimer, 20),
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
    else { // Not playing
      Stats stat = DTC.game.getStats(pl);
      
      double levelRatio = Math.min(
        Math.max(0, (double) stat.exp / stat.maxExp),
        1
      );
      
      lines.addAll(
        List.of(
          "",
          TextUtils.$r(
            "board.wins",
            List.of(
              Placeholder.component("wins", Component.text(stat.wins)),
              Placeholder.component("all", Component.text(stat.games)),
              Placeholder.unparsed(
                "ratio",
                stat.games == 0 ? "0.0"
                  : CoreUtils.toFixed(
                    100D * stat.wins / stat.games
                  )
              )
            )
          ),
          TextUtils.$r(
            "board.kd",
            List.of(
              Placeholder.component("kills", Component.text(stat.kills)),
              Placeholder.component("deaths", Component.text(stat.deaths))
            )
          ),
          TextUtils.$r(
            "board.attacks",
            List.of(
              Placeholder.component("value", Component.text(stat.coreAttacks))
            )
          ),
          TextUtils.$r(
            "board.ores",
            List.of(
              Placeholder.component(
                "value",
                Component.text(
                  stat.ores.values().stream().reduce(0, Integer::sum)
                )
              )
            )
          ),
          TextUtils.$r(
            "board.skills",
            List.of(
              Placeholder.component(
                "value",
                Component.text(stat.skills)
              )
            )
          ),
          "",
          TextUtils.$r(
            "board.exp",
            List.of(
              Placeholder.component("levels", Component.text(stat.levels)),
              Placeholder.component(
                "bar",
                Component.join(
                  JoinConfiguration.noSeparators(),
                  Component.text("|".repeat((int) (levelRatio * 8)))
                    .color(NamedTextColor.AQUA),
                  Component.text(
                    "|".repeat(8 - (int) (levelRatio * 8))
                  )
                )
              ),
              Placeholder.component(
                "exp",
                Component.text(stat.exp)
              ),
              Placeholder.component(
                "max",
                Component.text(stat.maxExp)
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
