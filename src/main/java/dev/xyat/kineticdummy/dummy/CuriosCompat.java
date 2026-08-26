package dev.xyat.kineticdummy.dummy;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.xyat.kineticdummy.dummy.config.DummyConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.UUID;

public class CuriosCompat {
    // 固定 UUID：防止每次读取或 tick 时产生无限叠加，保证只有这 107 个槽位
    private static final UUID DUMMY_CURIO_UUID = UUID.fromString("d8a086f6-427c-4749-b00e-3d0d8b512345");

    /**
     * 确保假人默认生成后自带 54 个原生的 "curio" 饰品槽。
     * 通过 Curios FlightAPI 添加永久槽位修正 (Permanent Slot Modifier) 实现。
     */
    public static void initDummySlots(LivingEntity entity) {
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            int extraSlots = DummyConfig.dummyCurioExtraSlots.get();
            if (extraSlots <= 0) return;

            // 使用固定 UUID 的永久槽位修正，避免重复添加导致槽位无限叠加。
            // 注意：Curios 会在 LivingTick 中处理这些槽位，槽位越多开销越高。
            Multimap<String, AttributeModifier> map = LinkedHashMultimap.create();
            map.put("curio", new AttributeModifier(DUMMY_CURIO_UUID, "Dummy Curio Slots", extraSlots, AttributeModifier.Operation.ADDITION));
            handler.addPermanentSlotModifiers(map);
        });
    }

    /**
     * 获取实体真实的饰品槽位总数
     */
    public static int getSlotCount(LivingEntity entity) {
        // 【已修复】：换用新 API
        return CuriosApi.getCuriosInventory(entity)
                .map(ICuriosItemHandler::getSlots)
                .orElse(0);
    }

    /**
     * 获取实体指定索引的饰品
     */
    public static ItemStack getCurioItem(LivingEntity entity, int index) {
        return CuriosApi.getCuriosInventory(entity)
                .filter(handler -> index >= 0 && index < handler.getSlots())
                .map(handler -> handler.getEquippedCurios().getStackInSlot(index))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * 设置实体指定索引的饰品
     */
    public static boolean setCurioItem(LivingEntity entity, int index, ItemStack stack) {
        return CuriosApi.getCuriosInventory(entity).map(handler -> {
            if (index < 0 || index >= handler.getSlots()) return false;
            handler.getEquippedCurios().setStackInSlot(index, stack);
            return true;
        }).orElse(false);
    }
}
