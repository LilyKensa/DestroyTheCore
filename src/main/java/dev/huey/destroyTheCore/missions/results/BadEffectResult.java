package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BadEffectResult extends Mission.Result {
  
  static public PotionEffect getEffect(
    PotionEffectType type, int amplifier, int seconds
  ) {
    return new PotionEffect(type, seconds * 20, amplifier, false, true);
  }
  
  static public PotionEffect getEffect(PotionEffectType type, int amplifier) {
    return getEffect(type, amplifier, 60);
  }
  
  static public final List<PotionEffect> effects = List.of(
    getEffect(PotionEffectType.WEAKNESS, 1),
    getEffect(PotionEffectType.GLOWING, 0),
    getEffect(PotionEffectType.HUNGER, 9),
    getEffect(PotionEffectType.UNLUCK, 2),
    getEffect(PotionEffectType.BLINDNESS, 0, 30),
    getEffect(PotionEffectType.SLOWNESS, 1),
    getEffect(PotionEffectType.POISON, 0),
    getEffect(PotionEffectType.MINING_FATIGUE, 2),
    getEffect(PotionEffectType.NAUSEA, 2, 30),
    getEffect(PotionEffectType.WITHER, 2, 10)
  );
  
  public PotionEffect effect;
  
  public BadEffectResult() {
    super("bad-effect", false);
    effect = RandomUtils.pick(effects);
  }
  
  @Override
  public List<TagResolver> getExtraPlaceholers() {
    return List.of(
      Placeholder.component(
        "effect",
        Component.translatable(effect.getType().translationKey())
      )
    );
  }
  
  @Override
  public void forLoser(Game.Side side) {
    outro(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      p.addPotionEffect(effect);
    }
  }
}
