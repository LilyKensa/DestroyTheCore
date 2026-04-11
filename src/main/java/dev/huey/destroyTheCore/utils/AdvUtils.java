package dev.huey.destroyTheCore.utils;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import org.bukkit.entity.Player;

public class AdvUtils {
  static public void grant(Player pl, Advancement adv) {
    if (!adv.isGranted(pl)) adv.displayToastToPlayer(pl);
    adv.grant(pl);
  }
  
  static public void progress(Player pl, Advancement adv, Integer number) {
    adv.incrementProgression(pl, number);
  }
  
  static public void progress(Player pl, Advancement adv) {
    progress(pl, adv, 1);
  }
}
