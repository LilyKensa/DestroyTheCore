package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class JoinTeamCommand extends Subcommand {
  
  public JoinTeamCommand() {
    super("team");
    addArgument(
      "team",
      () -> Arrays.stream(Game.Side.values()).map(s -> s.id).toList()
    );
    addArgument(
      "player",
      () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.join.unclear"));
    }
    
    Game.Side side = Arrays.stream(Game.Side.values()).filter(s -> s.id.equals(
      args.getFirst())).findAny().orElse(null);
    if (side == null) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.join.side-not-found"));
      return;
    }
    
    Player target;
    
    if (args.size() >= 2) {
      if (!PlayerUtils.isAdmin(pl)) {
        PlayerUtils.prefixedSend(pl, TextUtils.$("commands.join.only-self"));
        return;
      }
      
      target = Bukkit.getPlayer(args.get(1));
      if (target == null) {
        PlayerUtils.prefixedSend(
          pl,
          TextUtils.$("commands.join.player-not-found")
        );
        return;
      }
      PlayerUtils.prefixedBroadcast(
        TextUtils.$(
          "commands.join.made-other",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("target", PlayerUtils.getName(target)),
            Placeholder.component("side", side.titleComp())
          )
        )
      );
    }
    else {
      if (
        !PlayerUtils.isAdmin(pl) && DestroyTheCore.worldsManager.checkLiveWorld(
          pl.getLocation())
      ) {
        PlayerUtils.prefixedSend(pl, TextUtils.$("commands.join.only-lobby"));
        return;
      }
      
      target = pl;
      PlayerUtils.prefixedBroadcast(
        TextUtils.$(
          "commands.join.made-self",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("side", side.titleComp())
          )
        )
      );
    }
    
    DestroyTheCore.game.getPlayerData(target).join(side);
    DestroyTheCore.game.enforceTeam(target);
    
    PlayerUtils.refreshSpectatorAbilities(target);
    PlayerUtils.hideSpectators();
    
    DestroyTheCore.boardsManager.refresh(target);
  }
}
