package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
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
    if (!DestroyTheCore.game.isPlaying) return;
    
    DestroyTheCore.game.paused = !DestroyTheCore.game.paused;
    
    Bukkit.getServer().getServerTickManager()
      .setFrozen(DestroyTheCore.game.paused);
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.getAttribute(Attribute.GRAVITY)
        .setBaseValue(DestroyTheCore.game.paused ? 0 : 0.08);
    }
    
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.pause." + (DestroyTheCore.game.paused ? "yes" : "no"),
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl))
        )
      )
    );
  }
}
