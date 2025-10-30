package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.utils.PlayerUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
  
  public void addArgument(String name, Supplier<List<String>> completionsSupplier) {
    arguments.add(new Argument(name, completionsSupplier));
  }
  
  public void execute(Player pl, List<String> args) {
    PlayerUtils.prefixedSend(pl, "This command isn't implemented yet!", NamedTextColor.RED);
  }
}
