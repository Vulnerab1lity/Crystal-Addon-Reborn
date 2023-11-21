package com.crystaldevs.crystal.mixin.crystal;

import com.crystaldevs.crystal.mixin.mixininterface.IMeshAccessor;
import meteordevelopment.meteorclient.renderer.Mesh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mesh.class)
public interface MeshMixin extends IMeshAccessor {
    @Accessor(remap = false)
    int getIndicesCount();

    @Accessor(remap = false)
    long getIndicesPointer();

    @Accessor(remap = false)
    void setIndicesCount(int count);
}
