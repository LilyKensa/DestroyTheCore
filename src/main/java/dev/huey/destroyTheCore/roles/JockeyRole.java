package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JockeyRole extends Role
{

    public JockeyRole()
    {
        super(RolesManager.RoleKey.JOCKEY);
        addInfo(Material.LEAD);
        addFeature();
        addExclusiveItem(
                Material.LEAD
        );
        addSkill(60 * 20);
    }

    public void onTick(Player pl)
    {
        if (pl.isInsideVehicle() && pl.getVehicle() instanceof Horse)
        {
            pl.addPotionEffect(new PotionEffect(
                    PotionEffectType.STRENGTH,
                    3,
                    0,
                    true,
                    false
            ));
            pl.addPotionEffect(new PotionEffect(
                    PotionEffectType.RESISTANCE,
                    3,
                    1,
                    true,
                    false
            ));
        }else
        {
            pl.addPotionEffect(new PotionEffect(
                    PotionEffectType.WEAKNESS,
                    3,
                    0,
                    true,
                    false
            ));
        }
    }

    public void useSkill(Player pl)
    {
        for (Entity entity : pl.getNearbyEntities(5, 5, 5))
        {
            if (entity instanceof Horse horse)
            {
                horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_HORSE_ARMOR));
                horse.addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION,
                        5,
                        255,
                        true,
                        false
                ));
            }else
            {
                pl.getLocation().getWorld().spawn(pl.getLocation(), Horse.class, myhorse ->
                {
                    myhorse.setTamed(true);
                    myhorse.setOwner(pl);
                    myhorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                    myhorse.addPassenger(pl);
                });
            }
        }

    }

}
