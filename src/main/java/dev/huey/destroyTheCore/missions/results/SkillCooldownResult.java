package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillCooldownResult extends Mission.Result {
  public SkillCooldownResult() {
    super("skill-cooldown");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    announce(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      p.setCooldown(
        Material.KNOWLEDGE_BOOK,
        p.getCooldown(Material.KNOWLEDGE_BOOK) + 120 * 20
      );
    }
  }
}
