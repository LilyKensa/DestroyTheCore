package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoodEffectResult extends Mission.Result {
  
  static public PotionEffect getEffect(
    PotionEffectType type, int amplifier, int seconds
  ) {
    return new PotionEffect(type, seconds * 20, amplifier, false, true);
  }
  
  static public PotionEffect getEffect(PotionEffectType type, int amplifier) {
    return getEffect(type, amplifier, 90);
  }
  
  static public final List<PotionEffect> effects = List.of(
    getEffect(PotionEffectType.ABSORPTION, 2),
    getEffect(PotionEffectType.FIRE_RESISTANCE, 0),
    getEffect(PotionEffectType.HASTE, 2),
    getEffect(PotionEffectType.LUCK, 1),
    getEffect(PotionEffectType.SATURATION, 1),
    getEffect(PotionEffectType.REGENERATION, 2),
    getEffect(PotionEffectType.RESISTANCE, 0),
    getEffect(PotionEffectType.JUMP_BOOST, 1),
    getEffect(PotionEffectType.STRENGTH, 1, 30)
  );
  
  public GoodEffectResult() {
    super("good-effect");
  }
  
  @Override
  public void forWinner(Game.Side side) {
    PotionEffect effect = RandomUtils.pick(effects);
    
    announce(
      side,
      List.of(
        Placeholder.component(
          "effect",
          Component.translatable(effect.getType().translationKey())
        )
      )
    );
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      p.addPotionEffect(effect);
    }
  }
}
