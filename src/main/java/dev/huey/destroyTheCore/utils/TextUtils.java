package dev.huey.destroyTheCore.utils;

import dev.huey.destroyTheCore.DestroyTheCore;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtils {
  /** Remove legacy color codes from a string */
  static public String stripColor(String text) {
    return text.replaceAll("§[0-9a-fklmnor]", "");
  }
  
  static final LegacyComponentSerializer serializer = LegacyComponentSerializer
    .builder().character(
      LegacyComponentSerializer.SECTION_CHAR
    ).hexColors().build();
  
  static public String miniToRawCodes(String input) {
    return serializer.serialize(MiniMessage.miniMessage().deserialize(input));
  }
  
  static public String miniToCodes(
    String input, List<TagResolver> placeholders
  ) {
    if (input == null) return null;
    
    Component component = MiniMessage.miniMessage().deserialize(
      input,
      placeholders.toArray(new TagResolver[0])
    ).colorIfAbsent(
      NamedTextColor.GRAY
    ).decorationIfAbsent(
      TextDecoration.ITALIC,
      TextDecoration.State.FALSE
    );
    return serializer.serialize(component);
  }
  
  static public Component translate(
    String key, List<TagResolver> placeholders
  ) {
    return DestroyTheCore.translationsManager.get(key, placeholders);
  }
  
  static public Component $(String key, List<TagResolver> placeholders) {
    return translate(key, placeholders);
  }
  
  static public Component $(String key) {
    return $(key, List.of());
  }
  
  static public String translateRaw(String key) {
    return DestroyTheCore.translationsManager.getRaw(key);
  }
  
  static public String $r(String key, List<TagResolver> placeholders) {
    return miniToCodes(translateRaw(key), placeholders);
  }
  
  static public String $r(String key) {
    return $r(key, List.of());
  }
}
