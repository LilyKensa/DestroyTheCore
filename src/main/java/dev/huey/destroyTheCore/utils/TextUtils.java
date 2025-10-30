package dev.huey.destroyTheCore.utils;

import dev.huey.destroyTheCore.DestroyTheCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class TextUtils {
  static final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
    .character(LegacyComponentSerializer.SECTION_CHAR)
    .hexColors()
    .build();
  
  public static String miniToRawCodes(String input) {
    return serializer.serialize(
      MiniMessage.miniMessage().deserialize(input)
    );
  }
  public static String miniToCodes(String input, List<TagResolver> placeholders) {
    if (input == null) return null;
    
    Component component = MiniMessage.miniMessage()
      .deserialize(input, placeholders.toArray(new TagResolver[0]))
      .colorIfAbsent(NamedTextColor.GRAY)
      .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    return serializer.serialize(component);
  }
  
  public static Component translate(String key, List<TagResolver> placeholders) {
    return DestroyTheCore.translationsManager.get(key, placeholders);
  }
  public static Component $(String key, List<TagResolver> placeholders) {
    return translate(key, placeholders);
  }
  public static Component $(String key) {
    return $(key, List.of());
  }
  
  public static String translateRaw(String key) {
    return DestroyTheCore.translationsManager.getRaw(key);
  }
  public static String $r(String key, List<TagResolver> placeholders) {
    return miniToCodes(translateRaw(key), placeholders);
  }
  public static String $r(String key) {
    return $r(key, List.of());
  }
}
