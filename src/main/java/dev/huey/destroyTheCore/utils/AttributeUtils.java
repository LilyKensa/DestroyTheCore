package dev.huey.destroyTheCore.utils;

import dev.huey.destroyTheCore.DestroyTheCore;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public class AttributeUtils {
  static public AttributeModifier addition(
    String name, EquipmentSlotGroup slot, double amount
  ) {
    return new AttributeModifier(
      new NamespacedKey(DestroyTheCore.instance, "add-%s-%g".formatted(name, amount)),
      amount,
      AttributeModifier.Operation.ADD_NUMBER,
      slot
    );
  }
  
  static public AttributeModifier multiply(
    String name, EquipmentSlotGroup slot, double ratio
  ) {
    return new AttributeModifier(
      new NamespacedKey(DestroyTheCore.instance, "multiply-%s-%g".formatted(name, ratio)),
      ratio - 1,
      AttributeModifier.Operation.ADD_SCALAR,
      slot
    );
  }
}
