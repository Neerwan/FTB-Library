package com.feed_the_beast.ftbl.api;

import com.feed_the_beast.ftbl.api.events.ReloadType;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 11.08.2016.
 */
public abstract class FTBLibAPI
{
    private static FTBLibAPI API;

    public static void setAPI(FTBLibAPI api)
    {
        API = api;
    }

    public static FTBLibAPI get()
    {
        return API;
    }

    public abstract boolean hasServer();

    public abstract IConfigManager configManager();

    public abstract IFTBLibRegistries getRegistries();

    public abstract IPackModes getPackModes();

    public abstract ISharedData getSharedData(Side side);

    @Nullable
    public abstract IUniverse getUniverse();

    public abstract void addServerCallback(int timer, Runnable runnable);

    public abstract void reload(ICommandSender sender, ReloadType type);

    public abstract void openGui(ResourceLocation guiID, EntityPlayerMP ep, @Nullable NBTTagCompound data);

    public abstract void sendNotification(@Nullable EntityPlayerMP ep, INotification n);
}