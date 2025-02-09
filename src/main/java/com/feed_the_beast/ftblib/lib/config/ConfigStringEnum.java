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
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ConfigStringEnum extends ConfigValue implements IIteratingConfig
{
	public static final class StringEnumValue
	{
		public final int index;
		public final String id;
		public ITextComponent customName = null;
		public Color4I customColor = Icon.EMPTY;

		public StringEnumValue(int idx, String i)
		{
			index = idx;
			id = i;
		}

		public int hashCode()
		{
			return index;
		}

		public boolean equals(Object o)
		{
			return o == this || index == o.hashCode();
		}

		public String toString()
		{
			return id;
		}
	}

	private final List<StringEnumValue> values;
	private StringEnumValue value;

	public ConfigStringEnum(List<String> k, String v)
	{
		values = new ArrayList<>(k.size());

		for (int i = 0; i < k.size(); i++)
		{
			values.add(new StringEnumValue(i, k.get(i)));
		}

		value = get(v);
	}

	private ConfigStringEnum(ConfigStringEnum copyFrom)
	{
		values = new ArrayList<>(copyFrom.values.size());

		for (int i = 0; i < copyFrom.values.size(); i++)
		{
			StringEnumValue v0 = copyFrom.values.get(i);
			StringEnumValue v = new StringEnumValue(i, v0.id);
			v.customName = v0.customName == null ? null : v0.customName.createCopy();
			v.customColor = v0.customColor.copy();
			values.add(v);
		}

		setString(copyFrom.getString());
	}

	@Nullable
	public StringEnumValue get(String v)
	{
		if (v.isEmpty())
		{
			return null;
		}

		for (StringEnumValue value : values)
		{
			if (value.id.equals(v))
			{
				return value;
			}
		}

		return null;
	}

	@Override
	public String getID()
	{
		return ConfigEnum.ID;
	}

	public void setString(String v)
	{
		value = get(v);
	}

	@Override
	public String getString()
	{
		return value == null ? "" : value.id;
	}

	@Override
	public ITextComponent getStringForGUI()
	{
		return value == null ? new TextComponentString("null") : value.customName == null ? new TextComponentString(getString()) : value.customName.createCopy();
	}

	@Override
	public boolean getBoolean()
	{
		return true;
	}

	@Override
	public int getInt()
	{
		return value == null ? -1 : value.index;
	}

	@Override
	public ConfigStringEnum copy()
	{
		return new ConfigStringEnum(this);
	}

	@Override
	public Color4I getColor()
	{
		return value == null || value.customColor.isEmpty() ? ConfigEnum.COLOR : value.customColor;
	}

	@Override
	public void addInfo(ConfigValueInstance inst, List<String> list)
	{
		if (inst.getCanEdit() && !inst.getDefaultValue().isNull())
		{
			StringEnumValue value = get(inst.getDefaultValue().getString());
			ITextComponent component = value == null ? null : value.customName;
			list.add(TextFormatting.AQUA + "Default: " + TextFormatting.RESET + (component == null ? inst.getDefaultValue() : component.getFormattedText()));
		}
	}

	@Override
	public void onClicked(IOpenableGui gui, ConfigValueInstance inst, MouseButton button, Runnable callback)
	{
		if (values.size() > 16 || GuiBase.isCtrlKeyDown())
		{
			GuiButtonListBase g = new GuiButtonListBase()
			{
				@Override
				public void addButtons(Panel panel)
				{
					for (StringEnumValue v : values)
					{
						panel.add(new SimpleTextButton(panel, v.customName == null ? getString() : v.customName.getUnformattedText(), Icon.EMPTY)
						{
							@Override
							public void onClicked(MouseButton button)
							{
								GuiHelper.playClickSound();
								setString(v.id);
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
		List<String> l = new ArrayList<>(values.size());

		for (StringEnumValue v : values)
		{
			l.add(v.id);
		}

		return l;
	}

	@Override
	public ConfigValue getIteration(boolean next)
	{
		ConfigStringEnum c = copy();
		c.setString(value == null ? "" : c.values.get(MathUtils.mod(c.value.index + (next ? 1 : -1), c.values.size())).id);
		return c;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String key)
	{
		nbt.setString(key, getString());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String key)
	{
		setString(nbt.getString(key));
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(values.size());

		for (StringEnumValue v : values)
		{
			data.writeString(v.id);
			data.writeTextComponent(v.customName);
			data.writeInt(v.customColor.rgba());
		}

		data.writeString(getString());
	}

	@Override
	public void readData(DataIn data)
	{
		values.clear();
		int s = data.readVarInt();

		for (int i = 0; i < s; i++)
		{
			StringEnumValue v = new StringEnumValue(i, data.readString());
			v.customName = data.readTextComponent();
			v.customColor = Color4I.rgba(data.readInt());
			values.add(v);
		}

		setString(data.readString());
	}

	@Override
	public boolean setValueFromString(@Nullable ICommandSender sender, String string, boolean simulate)
	{
		setString(string);
		return true;
	}

	@Override
	public void setValueFromOtherValue(ConfigValue value)
	{
		setString(value.getString());
	}
}