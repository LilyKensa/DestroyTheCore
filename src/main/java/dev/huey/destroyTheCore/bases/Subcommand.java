package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.managers.CommandsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class Subcommand {
  
  static public class Argument {
    
    public String name;
    public Supplier<List<String>> completionsSupplier;
    
    public Argument(String name, Supplier<List<String>> completionsSupplier) {
      this.name = name;
      this.completionsSupplier = completionsSupplier;
    }
  }
  
  public String name;
  public List<Argument> arguments = new ArrayList<>();
  
  public Subcommand(String name) {
    this.name = name;
  }
  
  /**
   * Add an argument for tab-completion, the name is currently useless
   * 
   * @see CommandsManager#checkCompletion
   */
  public void addArgument(
    String name, Supplier<List<String>> completionsSupplier
  ) {
    arguments.add(new Argument(name, completionsSupplier));
  }
  
  /** @implNote Required - The command callback */
  public void execute(Player pl, List<String> args) {
    PlayerUtils.prefixedSend(
      pl,
      "This command isn't implemented yet!",
      NamedTextColor.RED
    );
  }
}
