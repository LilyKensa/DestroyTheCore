package dev.huey.destroyTheCore.utils;

import dev.huey.destroyTheCore.DestroyTheCore;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

/**
 * Specifically made for {@link AttributeModifier}
 * Probably should move this to other places though
 */
public class AttributeUtils {
  
  static public AttributeModifier addition(
    String name, EquipmentSlotGroup slot, double amount
  ) {
    return new AttributeModifier(
      new NamespacedKey(
        DestroyTheCore.instance,
        "add-%s-%g".formatted(name, amount)
      ),
      amount,
      AttributeModifier.Operation.ADD_NUMBER,
      slot
    );
  }
  
  static public AttributeModifier multiply(
    String name, EquipmentSlotGroup slot, double ratio
  ) {
    return new AttributeModifier(
      new NamespacedKey(
        DestroyTheCore.instance,
        "multiply-%s-%g".formatted(name, ratio)
      ),
      ratio - 1,
      AttributeModifier.Operation.ADD_SCALAR,
      slot
    );
  }
  
  static public AttributeInstance instance(
    Attributable entity, Attribute attribute
  ) {
    return entity.getAttribute(attribute);
  }
  
  static public double get(Attributable entity, Attribute attribute) {
    return instance(entity, attribute).getValue();
  }
  
  static public void set(
    Attributable entity, Attribute attribute, double value
  ) {
    AttributeInstance instance = instance(entity, attribute);
    if (instance == null) return;
    
    instance.setBaseValue(value);
  }
}
