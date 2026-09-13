package dev.huey.destroyTheCore.wrapped;

import dev.huey.destroyTheCore.bases.Wrapped;
import dev.huey.destroyTheCore.records.Pos;
import org.bukkit.entity.Player;

public class WPlayer extends Wrapped<Player> {

  public WPlayer(Player pl) {
    w = pl;
  }

  public Pos pos() {
    return Pos.of(w);
  }
}
