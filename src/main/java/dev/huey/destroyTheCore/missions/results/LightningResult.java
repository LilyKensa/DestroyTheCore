package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LightningResult extends Mission.Result {
  
  public LightningResult() {
    super("lightning");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    announce(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      p.getWorld().strikeLightning(p.getLocation());
      
      p.addPotionEffect(
        new PotionEffect(PotionEffectType.NAUSEA, 30 * 20, 0, true, false)
      );
    }
  }
}
