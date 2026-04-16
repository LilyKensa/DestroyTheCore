package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.managers.ConfigManager;
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
    addArgument("map", () -> DTC.configManager.availableTemplates);
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
            Placeholder.unparsed("map", DTC.worldsManager.mapName)
          )
        )
      );
      return;
    }
    
    if (DTC.game.isPlaying) {
      PlayerUtils.send(pl, TextUtils.$("commands.map.bad-time"));
      return;
    }
    
    String mapName = args.getFirst();
    File file = new File(
      Bukkit.getWorldContainer(),
      ConfigManager.templateWorldPrefix + mapName
    );
    if (!file.exists() || !file.isDirectory()) {
      PlayerUtils.send(pl, TextUtils.$("commands.map.not-found"));
      return;
    }
    
    DTC.worldsManager.mapName = mapName;
    
    DTC.configManager.save();
    DTC.configManager.load();
    
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
