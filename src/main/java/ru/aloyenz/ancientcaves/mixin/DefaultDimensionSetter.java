package ru.aloyenz.ancientcaves.mixin;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.aloyenz.ancientcaves.AncientCaves;
import ru.aloyenz.ancientcaves.world.AncientCavesWorldProvider;

@Mixin(value = DimensionType.class)
public class DefaultDimensionSetter {

    @Shadow @Final private int id;

    @Inject(method = "createDimension", at = @At("HEAD"), cancellable = true)
    public void createDimension(CallbackInfoReturnable<WorldProvider> cir) {
        if (!AncientCaves.generateNormalWorld) {
            if (id == DimensionType.OVERWORLD.getId()) {
                cir.setReturnValue(new AncientCavesWorldProvider());
            }
        }
    }
}
