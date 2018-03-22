package me.El_Chupe.packetlib;

public enum PacketTypeOld {
    PacketHandshakingInListener,
    PacketHandshakingInSetProtocol,
    PacketListenerPlayIn,
    PacketLoginInEncryptionBegin,
    PacketLoginInListener,
    PacketLoginInStart,
    PacketLoginOutDisconnect,
    PacketLoginOutEncryptionBegin,
    PacketLoginOutListener,
    PacketLoginOutSetCompression,
    PacketLoginOutSuccess,
    PacketPlayInAbilities,
    PacketPlayInAdvancements,
    PacketPlayInArmAnimation,
    PacketPlayInAutoRecipe,
    PacketPlayInBlockDig,
    PacketPlayInBlockPlace,
    PacketPlayInBoatMove,
    PacketPlayInChat,
    PacketPlayInClientCommand,
    PacketPlayInCloseWindow,
    PacketPlayInCustomPayload,
    PacketPlayInEnchantItem,
    PacketPlayInEntityAction,
    PacketPlayInFlying,
    PacketPlayInHeldItemSlot,
    PacketPlayInKeepAlive,
    PacketPlayInLook,
    PacketPlayInPositionLook,
    PacketPlayInRecipeDisplayed,
    PacketPlayInResourcePackStatus,
    PacketPlayInSetCreativeSlot,
    PacketPlayInSettings,
    PacketPlayInSpectate,
    PacketPlayInSteerVehicle,
    PacketPlayInTabComplete,
    PacketPlayInTeleportAccept,
    PacketPlayInTransaction,
    PacketPlayInUpdateSign,
    PacketPlayInUseEntity,
    PacketPlayInUseItem,
    PacketPlayInVehicleMove,
    PacketPlayInWindowClick,
    PacketPlayOutAbilities,
    PacketPlayOutAdvancements,
    PacketPlayOutAnimation,
    PacketPlayOutAttachEntity,
    PacketPlayOutAutoRecipe,
    PacketPlayOutBed,
    PacketPlayOutBlockAction,
    PacketPlayOutBlockBreakAnimation,
    PacketPlayOutBlockChange,
    PacketPlayOutBoss,
    PacketPlayOutCamera,
    PacketPlayOutChat,
    PacketPlayOutCloseWindow,
    PacketPlayOutCollect,
    PacketPlayOutCombatEvent,
    PacketPlayOutCustomPayload,
    PacketPlayOutCustomSoundEffect,
    PacketPlayOutEntity,
    PacketPlayOutEntityDestroy,
    PacketPlayOutEntityEffect,
    PacketPlayOutEntityEquipment,
    PacketPlayOutEntityHeadRotation,
    PacketPlayOutEntityLook,
    PacketPlayOutEntityMetadata,
    PacketPlayOutEntityStatus,
    PacketPlayOutEntityTeleport,
    PacketPlayOutEntityVelocity,
    PacketPlayOutExperience,
    PacketPlayOutExplosion,
    PacketPlayOutGameStateChange,
    PacketPlayOutHeldItemSlot,
    PacketPlayOutKeepAlive,
    PacketPlayOutKickDisconnect,
    PacketPlayOutLogin,
    PacketPlayOutMap,
    PacketPlayOutMapChunk,
    PacketPlayOutMount,
    PacketPlayOutMultiBlockChange,
    PacketPlayOutMultiBlockChange$MultiBlockChangeInfo,
    PacketPlayOutNamedEntitySpawn,
    PacketPlayOutNamedSoundEffect,
    PacketPlayOutOpenSignEditor,
    PacketPlayOutOpenWindow,
    PacketPlayOutPlayerInfo,
    PacketPlayOutPlayerListHeaderFooter,
    PacketPlayOutPosition,
    PacketPlayOutRecipes,
    PacketPlayOutRelEntityMove,
    PacketPlayOutRelEntityMoveLook,
    PacketPlayOutRemoveEntityEffect,
    PacketPlayOutResourcePackSend,
    PacketPlayOutRespawn,
    PacketPlayOutScoreboardDisplayObjective,
    PacketPlayOutScoreboardObjective,
    PacketPlayOutScoreboardScore,
    PacketPlayOutScoreboardTeam,
    PacketPlayOutSelectAdvancementTab,
    PacketPlayOutServerDifficulty,
    PacketPlayOutSetCooldown,
    PacketPlayOutSetSlot,
    PacketPlayOutSpawnEntity,
    PacketPlayOutSpawnEntityExperienceOrb,
    PacketPlayOutSpawnEntityLiving,
    PacketPlayOutSpawnEntityPainting,
    PacketPlayOutSpawnEntityWeather,
    PacketPlayOutSpawnPosition,
    PacketPlayOutStatistic,
    PacketPlayOutTabComplete,
    PacketPlayOutTileEntityData,
    PacketPlayOutTitle,
    PacketPlayOutTransaction,
    PacketPlayOutUnloadChunk,
    PacketPlayOutUpdateAttributes,
    PacketPlayOutUpdateHealth,
    PacketPlayOutUpdateTime,
    PacketPlayOutVehicleMove,
    PacketPlayOutWindowData,
    PacketPlayOutWindowItems,
    PacketPlayOutWorldBorder,
    PacketPlayOutWorldEvent,
    PacketPlayOutWorldParticles,
    PacketStatusInPing,
    PacketStatusInStart,
    PacketStatusOutListener,
    PacketStatusOutPong,
    PacketStatusOutServerInfo,
    PacketPlayInPosition;

    static PacketTypeOld fromClass(Class<?> clazz) {
        return valueOf(clazz.getSimpleName());
    }

    static PacketTypeOld fromObject(Object object) {
        return fromClass(object.getClass());
    }

    Class<?> getPacketClass() {
        return ReflectionUtils.getClass("net.minecraft.server."+name());
    }
}