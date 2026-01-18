package dev.huey.destroyTheCore.items.assistance;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;

public class SkillCooldownAssistGen extends ItemGen {
  public SkillCooldownAssistGen() {
    super(
      ItemsManager.ItemKey.SKILL_COOLDOWN_ASSIST,
      Material.COMPASS
    );
  }
}
