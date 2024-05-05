package wutdahack.actuallyunbreaking.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import wutdahack.actuallyunbreaking.ActuallyUnbreaking;

@Mixin(MendingEnchantment.class)
public abstract class MendingEnchantmentMixin extends Enchantment {

    private MendingEnchantmentMixin(Enchantment.EnchantmentDefinition definition) {
        super(definition);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if (ActuallyUnbreaking.instance.config.useUnbreakableTag) {
            return !stack.has(DataComponents.UNBREAKABLE); // item is only acceptable if it doesn't have the unbreakable tag
        } else {
            return super.canEnchant(stack);
        }
    }
}
