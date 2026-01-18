package dev.huey.destroyTheCore;

import dev.huey.destroyTheCore.managers.*;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

public final class DestroyTheCore extends JavaPlugin {
  
  public static DestroyTheCore instance;
  public static String version;
  
  public static Component prefix;
  
  public static TranslationsManager translationsManager;
  public static ConfigManager configManager;
  public static ToolsManager toolsManager;
  public static CommandsManager commandsManager;
  public static EventsManager eventsManager;
  public static DamageManager damageManager;
  public static InventoriesManager inventoriesManager;
  public static ItemsManager itemsManager;
  public static GlowManager glowManager;
  public static WorldsManager worldsManager;
  public static RecipesManager recipesManager;
  public static RolesManager rolesManager;
  public static QuizManager quizManager;
  public static MissionsManager missionsManager;
  public static GUIManager guiManager;
  public static BoardsManager boardsManager;
  public static TicksManager ticksManager;
  
  public static Game game = new Game();
  
  @Override
  public void onEnable() {
    instance = this;
    version = getDescription().getVersion();
    
    InvUI.getInstance().setPlugin(this);
    
    translationsManager = new TranslationsManager();
    toolsManager = new ToolsManager();
    commandsManager = new CommandsManager();
    eventsManager = new EventsManager();
    damageManager = new DamageManager();
    inventoriesManager = new InventoriesManager();
    itemsManager = new ItemsManager();
    glowManager = new GlowManager();
    worldsManager = new WorldsManager();
    recipesManager = new RecipesManager();
    rolesManager = new RolesManager();
    quizManager = new QuizManager();
    missionsManager = new MissionsManager();
    guiManager = new GUIManager();
    boardsManager = new BoardsManager();
    configManager = new ConfigManager();
    ticksManager = new TicksManager();
    
    for (String commandName : new String[]{"dtc", "rejoin", "night-vision", "shout", "shuffle-team", "warp", "skip", "edit", "reset", "revive", "language",
    }) {
      PluginCommand command = getCommand(commandName);
      if (command == null) {
        CoreUtils.error("Command not found: " + commandName);
      }
      else {
        command.setTabCompleter(commandsManager);
        command.setExecutor(commandsManager);
      }
    }
    
    getServer().getPluginManager().registerEvents(eventsManager, this);
    
    worldsManager.init();
    configManager.init(); // After worlds
    translationsManager.init(); // After config, before others
    toolsManager.init();
    commandsManager.init();
    itemsManager.init();
    glowManager.init();
    recipesManager.init();
    rolesManager.init();
    guiManager.init();
    ticksManager.init();
    
    game.init();
    
    prefix = TextUtils.$("general.plugin-prefix");
    
    CoreUtils.log("Enabled");
  }
  
  @Override
  public void onDisable() {
    configManager.save();
    
    CoreUtils.log("Disabled");
  }
}
