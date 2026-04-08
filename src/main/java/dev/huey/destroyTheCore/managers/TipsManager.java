package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TipsManager {
  static Component prefix;
  
  class Tip {
    Component title;
    List<Component> contents;
    
    public Tip(Component title, List<Component> contents) {
      this.title = title;
      this.contents = contents;
    }
  }
  
  List<Tip> tips = new ArrayList<>();
  
  public void init() {
    prefix = TextUtils.$("tips.prefix");
    
    int index = 1;
    Component title;
    List<Component> contents = new ArrayList<>();
    while (true) {
      String key = "tips.tip-%d.title".formatted(index);
      if (!DestroyTheCore.translationsManager.has(key)) break;
      
      title = TextUtils.$(key);
      
      contents.clear();
      
      int contentIndex = 1;
      Component content;
      while (true) {
        String contentKey = "tips.tip-%d.content-%d".formatted(
          index,
          contentIndex
        );
        if (!DestroyTheCore.translationsManager.has(contentKey)) break;
        
        content = TextUtils.$(contentKey);
        
        contents.add(content);
        contentIndex++;
      }
      
      tips.add(new Tip(title, new ArrayList<>(contents)));
      index++;
    }
  }
  
  void send(Player pl, Component comp) {
    PlayerUtils.send(pl, prefix.append(comp));
  }
  
  void sendTip(Player pl, Tip tip) {
    pl.sendMessage(Component.empty());
    send(pl, tip.title);
    for (Component content : tip.contents) {
      send(pl, content);
    }
    pl.sendMessage(Component.empty());
  }
  
  public void sendRandomToAll() {
    Tip tip = RandomUtils.pick(tips);
    if (tip == null) return;
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      sendTip(p, tip);
    }
  }
  
  public void onTipTick() {
    if (DestroyTheCore.game.isPlaying) return;
    
    sendRandomToAll();
  }
}
