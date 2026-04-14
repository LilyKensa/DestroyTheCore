package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.AttrUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PauseCommand extends Subcommand {
  public PauseCommand() {
    super("pause");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!DTC.game.isPlaying) return;
    
    DTC.game.paused = !DTC.game.paused;
    
    Bukkit.getServer().getServerTickManager()
      .setFrozen(DTC.game.paused);
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      AttrUtils.set(
        p,
        Attribute.GRAVITY,
        DTC.game.paused ? 0 : 0.08
      );
    }
    
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.pause." + DTC.game.paused,
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl))
        )
      )
    );
  }
}
