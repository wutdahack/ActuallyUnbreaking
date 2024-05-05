package wutdahack.actuallyunbreaking.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wutdahack.actuallyunbreaking.ActuallyUnbreaking;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "hurtAndBreak", at = @At(value = "HEAD"))
    private void makeUnbreakable(int amount, RandomSource random, @Nullable ServerPlayer player, Runnable onBroken, CallbackInfo cir) {

        if (ActuallyUnbreaking.instance.config.useUnbreakableTag) {

            int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, (ItemStack) (Object) this);

            if (ActuallyUnbreaking.instance.config.useOnlyUnbreakableAtLevel) {
                if (unbreakingLevel == ActuallyUnbreaking.instance.config.onlyUnbreakableAtLevel) {
                    actuallyUnbreaking$addUnbreakableTag((ItemStack) (Object) this);
                }
            } else if (ActuallyUnbreaking.instance.config.useUnbreakableAtLevel) {
                if (unbreakingLevel >= ActuallyUnbreaking.instance.config.unbreakableAtLevel) {
                    actuallyUnbreaking$addUnbreakableTag((ItemStack) (Object) this);
                }
            } else if (ActuallyUnbreaking.instance.config.maxLevelOnly) {
                if (unbreakingLevel >= Enchantments.UNBREAKING.getMaxLevel()) {
                    actuallyUnbreaking$addUnbreakableTag((ItemStack) (Object) this);
                }
            } else if (unbreakingLevel > 0) {
                actuallyUnbreaking$addUnbreakableTag((ItemStack) (Object) this);
            }
        }
    }

    @Unique
    private void actuallyUnbreaking$addUnbreakableTag(ItemStack item) {

        int mendingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, (ItemStack) (Object) this);

        item.set(DataComponents.UNBREAKABLE, new Unbreakable(true)); // add the unbreakable tag
        item.setDamageValue(0); // set item damage to 0 to remove the tool's durability bar

        EnchantmentHelper.updateEnchantments(item, (mutable) -> mutable.set(Enchantments.UNBREAKING, 0)); // removing unbreaking

        if (mendingLevel > 0) {
            EnchantmentHelper.updateEnchantments(item, (mutable -> mutable.set(Enchantments.MENDING, 0))); // removing mending
        }
    }
}
