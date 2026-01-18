package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.io.File;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MapCommand extends Subcommand {
  
  public MapCommand() {
    super("map");
    addArgument("map", () -> List.of("<map>"));
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (args.isEmpty()) {
      PlayerUtils.send(
        pl,
        TextUtils.$(
          "commands.map.info",
          List.of(
            Placeholder.unparsed("map", DestroyTheCore.worldsManager.mapName)
          )
        )
      );
      return;
    }
    
    if (DestroyTheCore.game.isPlaying) {
      PlayerUtils.send(pl, TextUtils.$("commands.map.bad-time"));
      return;
    }
    
    String mapName = args.getFirst();
    File file = new File(Bukkit.getWorldContainer(), "template-" + mapName);
    if (!file.exists() || !file.isDirectory()) {
      PlayerUtils.send(pl, TextUtils.$("commands.map.not-found"));
      return;
    }
    
    DestroyTheCore.worldsManager.mapName = mapName;
    
    DestroyTheCore.configManager.save();
    DestroyTheCore.configManager.load();
    
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.map.announce",
        List.of(
          Placeholder.unparsed("map", mapName),
          Placeholder.component("player", PlayerUtils.getName(pl))
        )
      )
    );
  }
}
