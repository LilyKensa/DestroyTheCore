package dev.huey.destroyTheCore;

import dev.huey.destroyTheCore.managers.*;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

public final class DestroyTheCore extends JavaPlugin {
  static public DestroyTheCore instance;
  static public String version;
  
  static public Component prefix;
  
  static public TranslationsManager translationsManager;
  static public ConfigManager configManager;
  static public ToolsManager toolsManager;
  static public CommandsManager commandsManager;
  static public EventsManager eventsManager;
  static public DamageManager damageManager;
  static public InventoriesManager inventoriesManager;
  static public ItemsManager itemsManager;
  static public WorldsManager worldsManager;
  static public RecipesManager recipesManager;
  static public RolesManager rolesManager;
  static public QuizManager quizManager;
  static public MissionsManager missionsManager;
  static public GUIManager guiManager;
  static public BoardsManager boardsManager;
  static public TicksManager ticksManager;
  
  static public Game game = new Game();
  
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
    worldsManager = new WorldsManager();
    recipesManager = new RecipesManager();
    rolesManager = new RolesManager();
    quizManager = new QuizManager();
    missionsManager = new MissionsManager();
    guiManager = new GUIManager();
    boardsManager = new BoardsManager();
    configManager = new ConfigManager();
    ticksManager = new TicksManager();
    
    for (String commandName : new String[] {
      "dtc",
      "rejoin", "night-vision", "shout", "shuffle-team",
      "warp", "skip", "edit", "reset", "revive", "language"
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
