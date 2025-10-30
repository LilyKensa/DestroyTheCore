package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.utils.CoreUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TranslationsManager {
  static public final Key key = Key.key("destroy-the-core:translations");
  
  static public final List<Locale> availableLocales = List.of(Locale.US, Locale.TAIWAN);
  static public final List<String> availableLocaleTags = availableLocales.stream()
    .map(locale -> locale.toLanguageTag().toLowerCase()).toList();
  
  static public class Translator {
    public MiniMessage mm = MiniMessage.miniMessage();
    
    Map<Locale, Map<String, String>> store = new HashMap<>();
    
    public void register(Locale locale, String key, String value) {
      if (!store.containsKey(locale)) store.put(locale, new HashMap<>());
      Map<String, String> map = store.get(locale);
      map.put(key, value);
    }
    
    public boolean has(Locale locale, String key) {
      return store.containsKey(locale) && store.get(locale).containsKey(key);
    }
    
    public String get(Locale locale, String key) {
      String notFound = "%s:%s".formatted(locale.toLanguageTag().toLowerCase(), key);
      if (!store.containsKey(locale)) return notFound;
      Map<String, String> map = store.get(locale);
      if (!map.containsKey(key)) return notFound;
      return map.get(key);
    }
    
    public Component translate(Locale locale, String key, List<TagResolver> placeholders) {
      return mm.deserialize(get(locale, key), TagResolver.resolver(placeholders));
    }
  }
  
  public Locale currentLocale = Locale.US;
  public Translator translator = new Translator();
  
  public void init() {
    for (Locale locale : availableLocales)
      loadTranslations(locale);
  }
  
  void loadTranslations(Locale locale) {
    String tag = locale.toLanguageTag().toLowerCase();
    String path = "lang/%s.yml".formatted(tag);
    
    InputStream stream = DestroyTheCore.instance.getResource(path);
    if (stream == null) {
      CoreUtils.error("Could not load translation " + tag);
      return;
    }
    
    InputStreamReader reader = new InputStreamReader(
      stream,
      StandardCharsets.UTF_8
    );
    
    YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
    
    for (String key : config.getKeys(true)) {
      String value = config.getString(key);
      if (value == null) continue;
      
      translator.register(locale, key, value);
    }
  }
  
  public boolean has(String key) {
    return translator.has(currentLocale, key);
  }
  
  public String getRaw(String key) {
    return translator.get(currentLocale, key);
  }
  
  public Component get(String key, List<TagResolver> placeholders) {
    return translator.translate(currentLocale, key, placeholders)
      .colorIfAbsent(NamedTextColor.GRAY)
      .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
  }
}
