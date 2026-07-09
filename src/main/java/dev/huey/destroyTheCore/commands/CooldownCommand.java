package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CooldownCommand extends Subcommand {
  
  public CooldownCommand() {
    super("cooldown");
    addArgument(
      "player",
      () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    Player target;
    
    if (!args.isEmpty()) {
      target = Bukkit.getPlayer(args.getFirst());
      if (target == null) {
        PlayerUtils.prefixedSend(
          pl,
          TextUtils.$("commands.cooldown.player-not-found")
        );
        return;
      }
      PlayerUtils.prefixedBroadcast(
        TextUtils.$(
          "commands.cooldown.made-other",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("target", PlayerUtils.getName(target))
          )
        )
      );
    }
    else {
      target = pl;
      PlayerUtils.prefixedBroadcast(
        TextUtils.$(
          "commands.cooldown.made-self",
          List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
        )
      );
    }
    
    PlayerUtils.setSkillCooldown(target, 0);
  }
}
