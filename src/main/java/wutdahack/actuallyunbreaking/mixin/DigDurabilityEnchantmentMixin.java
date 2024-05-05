package wutdahack.actuallyunbreaking.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wutdahack.actuallyunbreaking.ActuallyUnbreaking;

@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentMixin extends Enchantment {


    private DigDurabilityEnchantmentMixin(Enchantment.EnchantmentDefinition definition) {
        super(definition);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {

        if (ActuallyUnbreaking.instance.config.mendingIncompatibility) {
            return !(other instanceof MendingEnchantment) && super.checkCompatibility(other); // mending with unbreaking is redundant
        } else {
            return super.checkCompatibility(other);
        }

    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if (ActuallyUnbreaking.instance.config.useUnbreakableTag) {
            return !stack.has(DataComponents.UNBREAKABLE); // item is only acceptable if it doesn't have the unbreakable tag
        } else {
            return super.canEnchant(stack);
        }
    }

    @Inject(method = "shouldIgnoreDurabilityDrop", at = @At(value = "HEAD"), cancellable = true)
    private static void makeUnbreakable(ItemStack stack, int level, RandomSource random, CallbackInfoReturnable<Boolean> cir) {

        if (!ActuallyUnbreaking.instance.config.useUnbreakableTag) {
            if (ActuallyUnbreaking.instance.config.useOnlyUnbreakableAtLevel) {
                if (level == ActuallyUnbreaking.instance.config.onlyUnbreakableAtLevel) {
                    stack.setDamageValue(0); // set item damage to 0 to remove the tool's durability bar
                    cir.setReturnValue(true);
                }
            }
            else if (ActuallyUnbreaking.instance.config.useUnbreakableAtLevel) {
                if (level >= ActuallyUnbreaking.instance.config.unbreakableAtLevel) {
                    stack.setDamageValue(0);
                    cir.setReturnValue(true);
                }
            }
            else if (ActuallyUnbreaking.instance.config.maxLevelOnly) {
                if (level >= Enchantments.UNBREAKING.getMaxLevel()) {
                    stack.setDamageValue(0);
                    cir.setReturnValue(true);
                }
            }
            else if (level > 0) {
                stack.setDamageValue(0);
                cir.setReturnValue(true);
            }
        }

    }
}
