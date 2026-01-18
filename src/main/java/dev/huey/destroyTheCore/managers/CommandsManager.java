package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.commands.*;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandsManager implements TabCompleter, CommandExecutor {
  
  public List<Subcommand> subcommands;
  
  public void init() {
    subcommands = List.of(
      new ShoutCommand(),
      new NightVisionCommand(),
      new ReloadCommand(),
      new SaveCommand(),
      new JoinTeamCommand(),
      new SetRoleCommand(),
      new ShuffleTeamCommand(),
      new RejoinCommand(),
      new ReviveCommand(),
      new EditCommand(),
      new WarpCommand(),
      new SnapCommand(),
      new GiveCommand(),
      new StopCommand(),
      new ResetCommand(),
      new WorldCommand(),
      new NextPhaseCommand(),
      new LanguageCommand(),
      new MapCommand(),
      new ShopCommand(),
      new HelpCommand()
    );
  }
  
  public List<String> checkCompletion(List<String> fullList, String lastArg) {
    if (fullList.contains(lastArg)) return List.of();
    
    return fullList.stream().filter(s -> s.contains(lastArg)).toList();
  }
  
  @Override
  public List<String> onTabComplete(
    CommandSender sender, Command command, String label, String[] args
  ) {
    String lastArg = args[args.length - 1];
    
    if (command.getName().equals("dtc")) {
      if (args.length == 1) {
        return checkCompletion(
          subcommands.stream().map(c -> c.name).toList(),
          lastArg
        );
      }
      
      return checkCompletion(
        complete(args[0], List.of(args).subList(1, args.length)),
        lastArg
      );
    }
    
    return checkCompletion(complete(command.getName(), List.of(args)), lastArg);
  }
  
  @Override
  public boolean onCommand(
    CommandSender sender, Command command, String label, String[] args
  ) {
    if (!(sender instanceof Player pl)) return true;
    
    if (command.getName().equals("dtc")) {
      if (args.length < 1) {
        PlayerUtils.prefixedSend(
          pl,
          Component.join(
            JoinConfiguration.noSeparators(),
            TextUtils.$("general.title").color(NamedTextColor.GOLD),
            Component.text(" "),
            Component.text("v"),
            Component.text(DestroyTheCore.version).color(NamedTextColor.YELLOW)
          ).colorIfAbsent(NamedTextColor.GRAY)
        );
        return true;
      }
      
      dispatch(pl, args[0], List.of(args).subList(1, args.length));
    }
    else {
      dispatch(pl, command.getName(), List.of(args));
    }
    
    return true;
  }
  
  public List<String> complete(String name, List<String> args) {
    Optional<Subcommand> findSubcommand = subcommands.stream().filter(
      c -> c.name.equals(name)).findAny();
    
    if (findSubcommand.isEmpty()) return List.of();
    if (findSubcommand.get().arguments.size() < args.size()) return List.of();
    
    return findSubcommand.get().arguments.get(
      args.size() - 1).completionsSupplier.get();
  }
  
  public void dispatch(Player pl, String name, List<String> args) {
    for (Subcommand subcommand : subcommands) {
      if (name.equals(subcommand.name)) {
        subcommand.execute(pl, args);
        return;
      }
    }
    
    PlayerUtils.prefixedSend(pl, TextUtils.$("command.not-found"));
  }
}
