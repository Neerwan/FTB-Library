package com.feed_the_beast.ftbl.api.item;

import com.latmod.lib.EnumNameMap;
import net.minecraft.util.IStringSerializable;

/**
 * Created by LatvianModder on 28.08.2016.
 */
public enum ToolType implements IStringSerializable
{
    PICK,
    SHOVEL,
    AXE,
    WRENCH;

    private String name;

    ToolType()
    {
        name = EnumNameMap.createName(this);
    }

    @Override
    public String getName()
    {
        return name;
    }
}
