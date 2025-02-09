package com.feed_the_beast.ftblib.lib.config;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.IOpenableGui;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ConfigEnum<E> extends ConfigValue implements IIteratingConfig
{
	public static final String ID = "enum";
	public static final Color4I COLOR = Color4I.rgb(0x0094FF);

	public static class SimpleEnum<T> extends ConfigEnum<T>
	{
		private final Supplier<T> get;
		private final Consumer<T> set;

		public SimpleEnum(NameMap<T> nm, Supplier<T> g, Consumer<T> s)
		{
			super(nm);
			get = g;
			set = s;
		}

		@Override
		public T getValue()
		{
			return get.get();
		}

		@Override
		public void setValue(T e)
		{
			set.accept(e);
		}
	}

	private final NameMap<E> nameMap;
	private E value;

	public ConfigEnum(NameMap<E> nm)
	{
		nameMap = nm;
		value = nm.defaultValue;
	}

	@Override
	public String getID()
	{
		return ID;
	}

	public NameMap<E> getNameMap()
	{
		return nameMap;
	}

	public E getValue()
	{
		return value;
	}

	public void setValue(E e)
	{
		value = e;
	}

	public void setValue(String value)
	{
		setValue(getNameMap().get(value));
	}

	@Override
	public String getString()
	{
		return getNameMap().getName(getValue());
	}

	@Override
	public ITextComponent getStringForGUI()
	{
		return getNameMap().getDisplayName(null, getValue());
	}

	@Override
	public boolean getBoolean()
	{
		return !isDefault();
	}

	@Override
	public int getInt()
	{
		return getNameMap().getIndex(getValue());
	}

	@Override
	public ConfigEnum<E> copy()
	{
		return new ConfigEnum<>(getNameMap().withDefault(getNameMap().get(getInt())));
	}

	@Override
	public Color4I getColor()
	{
		Color4I col = getNameMap().getColor(getValue());
		return col.isEmpty() ? COLOR : col;
	}

	@Override
	public void addInfo(ConfigValueInstance inst, List<String> list)
	{
		if (inst.getCanEdit() && !inst.getDefaultValue().isNull())
		{
			list.add(TextFormatting.AQUA + "Default: " + TextFormatting.RESET + getNameMap().getDisplayName(null, getNameMap().get(inst.getDefaultValue().getString())).getFormattedText());
		}
	}

	@Override
	public void onClicked(IOpenableGui gui, ConfigValueInstance inst, MouseButton button, Runnable callback)
	{
		if (getNameMap().values.size() > 16 || GuiBase.isCtrlKeyDown())
		{
			GuiButtonListBase g = new GuiButtonListBase()
			{
				@Override
				public void addButtons(Panel panel)
				{
					for (E v : getNameMap())
					{
						panel.add(new SimpleTextButton(panel, getNameMap().getDisplayName(Minecraft.getMinecraft().player, v).getUnformattedText(), Icon.EMPTY)
						{
							@Override
							public void onClicked(MouseButton button)
							{
								GuiHelper.playClickSound();
								setValue(v);
								gui.openGui();
								callback.run();
							}
						});
					}
				}
			};

			g.setHasSearchBox(true);
			g.openGui();
			return;
		}

		super.onClicked(gui, inst, button, callback);
	}

	@Override
	public List<String> getVariants()
	{
		return getNameMap().keys;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String key)
	{
		nbt.setString(key, getString());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String key)
	{
		setValue(nbt.getString(key));
	}

	@Override
	public void writeData(DataOut data)
	{
		NameMap<E> nameMap = getNameMap();

		data.writeVarInt(nameMap.size());

		for (Map.Entry<String, E> entry : nameMap.map.entrySet())
		{
			data.writeString(entry.getKey());
			data.writeTextComponent(nameMap.getDisplayName(null, entry.getValue()));
			data.writeInt(nameMap.getColor(entry.getValue()).rgba());
		}

		data.writeString(getString());
	}

	@Override
	public void readData(DataIn data)
	{
		throw new IllegalStateException("Can't read Abstract Enum property!");
	}

	@Override
	public ConfigValue getIteration(boolean next)
	{
		ConfigEnum<E> c = copy();
		c.setValue(next ? getNameMap().getNext(getValue()) : getNameMap().getPrevious(getValue()));
		return c;
	}

	@Override
	public boolean setValueFromString(@Nullable ICommandSender sender, String string, boolean simulate)
	{
		E val = getNameMap().getNullable(string);

		if (val != null)
		{
			if (!simulate)
			{
				setValue(val);
			}

			return true;
		}

		return false;
	}

	@Override
	public void setValueFromOtherValue(ConfigValue value)
	{
		setValue(value.getString());
	}

	@Override
	public void setValueFromJson(JsonElement json)
	{
		if (json.isJsonPrimitive())
		{
			setValue(json.getAsString());
		}
	}

	public boolean isDefault()
	{
		return getValue() == getNameMap().defaultValue;
	}
}