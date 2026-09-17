package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public class SetTeamCommand extends Subcommand {
  
  public SetTeamCommand() {
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
    
    Game.Side side = Arrays.stream(Game.Side.values()).filter(
      s -> s.id.equals(
        args.getFirst()
      )
    ).findAny().orElse(null);
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
        !PlayerUtils.isAdmin(pl) &&
          LocUtils.inLive(
            pl.getLocation()
          )
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
    
    DTC.game.getPlayerData(target).join(side);
    DTC.game.enforceDisplay(target);
    
    DTC.boardsManager.refresh(target);
    
    if (LocUtils.inLive(target)) {
      PlayerUtils.refreshSpectatorAbilities(target);
      PlayerUtils.refreshAllSpectatorVisibilities();
    }
    
    for (ItemStack item : target.getInventory().getContents()) {
      if (item == null || item.getType().isAir()) continue;
      if (!(item.getItemMeta() instanceof LeatherArmorMeta meta)) continue;
      if (!DTC.itemsManager.isGen(item)) continue;
      if (
        !item.getPersistentDataContainer().get(
          ItemGen.dataNamespace,
          PersistentDataType.STRING
        ).startsWith("STARTER")
      ) continue;
      
      CoreUtils.dyeTeamColor(item, side);
    }
  }
}
