package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldCommand extends Subcommand {
  
  public WorldCommand() {
    super("world");
    addArgument(
      "world",
      () -> Bukkit.getWorlds().stream().map(World::getName).toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(
        pl,
        TextUtils.$(
          "commands.world.query",
          List.of(Placeholder.unparsed("world", pl.getWorld().getName()))
        )
      );
      return;
    }
    
    World world = Bukkit.getWorld(args.getFirst());
    
    if (world == null) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.world.not-found"));
      return;
    }
    
    pl.teleport(world.getSpawnLocation().toBlockLocation().add(0.5, 0.25, 0.5));
  }
}
