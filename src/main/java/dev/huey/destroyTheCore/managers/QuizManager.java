package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class QuizManager {
  
  static public class Quiz {
    
    Player pl;
    int startTime;
    
    int answer = RandomUtils.range(200, 1600) + 1;
    int a = RandomUtils.range(100, answer - 100) + 1, b = answer - a;
    
    boolean ended = false, correct = false;
    
    public Quiz(Player pl) {
      this.pl = pl;
      this.startTime = DestroyTheCore.ticksManager.ticksCount;
      send(
        pl,
        TextUtils.$(
          "quiz.question",
          List.of(
            Placeholder.component("a", Component.text(a)),
            Placeholder.component("b", Component.text(b))
          )
        )
      );
    }
    
    public boolean check(int attempt) {
      return attempt == answer;
    }
    
    public void update(int attempt) {
      if (ended) return;
      
      if (DestroyTheCore.ticksManager.ticksCount - startTime < 10) {
        PlayerUtils.kickAntiCheat(pl, "reaction-time");
        return;
      }
      
      ended = true;
      correct = check(attempt);
      
      PlayerData data = DestroyTheCore.game.getPlayerData(pl);
      
      if (correct) data.quizQuota--;
      
      send(
        pl,
        TextUtils.$(
          "quiz." + (correct ? "correct" : "wrong"),
          List.of(
            Placeholder.component("answer", Component.text(answer)),
            Placeholder.component("try", Component.text(data.quizQuota))
          )
        )
      );
      
      pl.playSound(
        pl.getLocation(),
        correct ? Sound.ENTITY_EXPERIENCE_ORB_PICKUP : Sound.ENTITY_ZOMBIE_HORSE_HURT,
        1, // Volume
        1 // Pitch
      );
    }
  }
  
  static public Component prefix;
  
  static public void send(Player pl, Component message) {
    if (prefix == null) prefix = TextUtils.$("quiz.prefix");
    PlayerUtils.send(pl, prefix.append(message));
  }
  
  Map<UUID, Quiz> quizzes = new HashMap<>();
  
  public void start(Player pl) {
    if (DestroyTheCore.game.getPlayerData(pl).quizQuota <= 0) return;
    
    quizzes.put(pl.getUniqueId(), new Quiz(pl));
  }
  
  public void discard(Player pl) {
    quizzes.remove(pl.getUniqueId());
  }
  
  public void onPlayerChat(Player pl, String content) {
    UUID id = pl.getUniqueId();
    
    if (!quizzes.containsKey(id)) return;
    if (!content.matches("^\\d+$")) return;
    
    CoreUtils.setTickOut(
      () -> quizzes.get(id).update(Integer.parseInt(content))
    );
  }
  
  public boolean find(Player pl, Predicate<Quiz> predicate) {
    UUID id = pl.getUniqueId();
    
    if (!quizzes.containsKey(id)) return false;
    return predicate.test(quizzes.get(id));
  }
  
  public boolean isEnded(Player pl) {
    return find(pl, q -> q.ended);
  }
  
  public boolean isCorrect(Player pl) {
    return find(pl, q -> q.correct);
  }
}
