package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class AntiCheatManager {
  
  static public Component prefix;
  
  public enum Cheat {
    QUIZ_SPEED("reaction-time", 40),
    SPAM_BREAK("spam-break"),
    AUTO_CLICK("auto-click", 25),
    ATTACK_RANGE("attack-range");
    
    final String translateKey;
    final int reportThreshold;
    
    Cheat(String translateKey, int reportThreshold) {
      this.translateKey = translateKey;
      this.reportThreshold = reportThreshold;
    }
    
    Cheat(String translateKey) {
      this(translateKey, 0);
    }
    
    public Component getMessage() {
      return TextUtils.$("anti-cheat.reasons." + translateKey);
    }
    
    public void kick(Player pl) {
      pl.kick(
        prefix.append(getMessage())
      );
    }
  }
  
  public void init() {
    prefix = TextUtils.$("anti-cheat.prefix");
  }
  
  TextColor getYellowToRed(int value) {
    int clamped = Math.max(0, Math.min(100, value));
    int green = (int) (255 * (1 - (clamped / 100.0)));
    return TextColor.color(255, green, 0);
  }
  
  Map<UUID, EnumMap<Cheat, Integer>> scores = new HashMap<>();
  
  public void track(Player pl, Cheat cheat, int offset) {
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    UUID id = pl.getUniqueId();
    
    if (!scores.containsKey(id)) {
      scores.put(id, new EnumMap<>(Cheat.class));
    }
    EnumMap<Cheat, Integer> map = scores.get(id);
    
    int score = Math.max(0, map.getOrDefault(cheat, 0) + offset);
    map.put(cheat, score);
    
    if (offset > 0 && score > cheat.reportThreshold) {
      PlayerUtils.notice(
        prefix.append(
          TextUtils.$(
            "anti-cheat.admin-notice",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl)),
              Placeholder.component("cheat", cheat.getMessage()),
              Placeholder.component(
                "value",
                Component.text(score).color(getYellowToRed(score))
              )
            )
          )
        )
      );
    }
    
    if (score >= 100 && !PlayerUtils.isAdmin(pl)) {
      cheat.kick(pl);
      clear(pl);
    }
  }
  
  public void clear(Player pl) {
    scores.remove(pl.getUniqueId());
  }
  
  // public void kickIfCheat(Player pl) {
  //   if (PlayerUtils.isAdmin(pl)) return;
  //
  //   UUID id = pl.getUniqueId();
  //
  //   if (!scores.containsKey(id)) return;
  //   EnumMap<Cheat, Integer> map = scores.get(id);
  //
  //   boolean kicked = false;
  //   for (Map.Entry<Cheat, Integer> entry : map.entrySet()) {
  //     if (entry.getValue() >= 100) {
  //       entry.getKey().kick(pl);
  //       kicked = true;
  //     }
  //   }
  //
  //   if (kicked) {
  //     clear(pl);
  //   }
  // }
}
