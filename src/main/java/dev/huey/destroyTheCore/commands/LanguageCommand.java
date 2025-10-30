package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.managers.TranslationsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class LanguageCommand extends Subcommand {
  public LanguageCommand() {
    super("language");
    addArgument(
      "locale",
      () -> TranslationsManager.availableLocaleTags
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(
        pl,
        "Current locale is " + DestroyTheCore.translationsManager.currentLocale
          .toLanguageTag().toLowerCase(),
        NamedTextColor.AQUA
      );
      return;
    }
    
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    String tag = args.getFirst();
    
    if (!TranslationsManager.availableLocaleTags.contains(tag)) {
      PlayerUtils.prefixedSend(
        pl,
        "Unsupported locale!",
        NamedTextColor.RED
      );
      return;
    }
    
    Locale locale = Locale.forLanguageTag(tag);
    if (locale == null) return;
    
    DestroyTheCore.translationsManager.currentLocale = locale;
    
    PlayerUtils.prefixedNotice(
      Component.text("Set language to " + tag)
        .color(NamedTextColor.GREEN)
    );
    PlayerUtils.prefixedNotice(
      Component.text("We recommend restarting the server!")
        .color(NamedTextColor.AQUA)
    );
  }
}
