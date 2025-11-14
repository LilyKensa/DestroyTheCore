package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class SetRoleCommand extends Subcommand {
  public SetRoleCommand() {
    super("role");
    addArgument("role", () ->
      DestroyTheCore.rolesManager.roles.values().stream()
        .map(s -> s.id.name().toLowerCase()).toList()
    );
    addArgument("player", () ->
      Bukkit.getOnlinePlayers().stream()
        .map(Player::getName).toList());
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.role.unclear"));
    }
    
    RolesManager.RoleKey key;
    try {
      key = RolesManager.RoleKey.valueOf(args.getFirst().toUpperCase());
    }
    catch (IllegalArgumentException ignored) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.role.role-not-found"));
      return;
    }
    
    Role role = DestroyTheCore.rolesManager.roles.get(key);
    
    Player target;
    
    if (args.size() >= 2) {
      if (!PlayerUtils.isAdmin(pl)) {
        PlayerUtils.prefixedSend(pl, TextUtils.$("commands.role.only-self"));
        return;
      }
      
      target = Bukkit.getPlayer(args.get(1));
      if (target == null) {
        PlayerUtils.prefixedSend(pl, TextUtils.$("commands.role.player-not-found"));
        return;
      }
      PlayerUtils.prefixedBroadcast(TextUtils.$("commands.role.made-other", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.component("target", PlayerUtils.getName(target)),
        Placeholder.unparsed("role", role.name)
      )));
    }
    else {
      if (
        !PlayerUtils.isAdmin(pl) &&
          DestroyTheCore.worldsManager.checkLiveWorld(pl.getLocation())
      ) {
        PlayerUtils.prefixedSend(pl, TextUtils.$("commands.role.only-lobby"));
        return;
      }
      
      target = pl;
      PlayerUtils.prefixedBroadcast(TextUtils.$("commands.role.made-self", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.unparsed("role", role.name)
      )));
    }
    
    DestroyTheCore.rolesManager.setRole(target, role);
    DestroyTheCore.game.enforceTeam(target);
    DestroyTheCore.boardsManager.refresh(target);
  }
}
