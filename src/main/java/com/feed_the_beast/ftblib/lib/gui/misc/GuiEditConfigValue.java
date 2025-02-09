package com.feed_the_beast.ftblib.lib.gui.misc;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.ConfigValueInstance;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.TextBox;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiEditConfigValue extends GuiBase
{
	private final ConfigValueInstance inst;
	private final ConfigValue value;
	private final IConfigValueEditCallback callback;

	private final Button buttonCancel, buttonAccept;
	private final TextBox textBox;

	public GuiEditConfigValue(ConfigValueInstance val, IConfigValueEditCallback c)
	{
		setSize(230, 54);
		inst = val;
		value = inst.getValue().copy();
		callback = c;

		int bsize = width / 2 - 10;

		buttonCancel = new SimpleTextButton(this, I18n.format("gui.cancel"), Icon.EMPTY)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				callback.onCallback(inst.getValue(), false);
			}

			@Override
			public boolean renderTitleInCenter()
			{
				return true;
			}
		};

		buttonCancel.setPosAndSize(8, height - 24, bsize, 16);

		buttonAccept = new SimpleTextButton(this, I18n.format("gui.accept"), Icon.EMPTY)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();

				if (value.setValueFromString(Minecraft.getMinecraft().player, textBox.getText(), false))
				{
					callback.onCallback(value, true);
				}

				if (getGui().parent instanceof GuiBase)
				{
					getGui().parent.closeContextMenu();
				}
			}

			@Override
			public WidgetType getWidgetType()
			{
				return inst.getCanEdit() && textBox.isTextValid() ? super.getWidgetType() : WidgetType.DISABLED;
			}

			@Override
			public boolean renderTitleInCenter()
			{
				return true;
			}
		};

		buttonAccept.setPosAndSize(width - bsize - 8, height - 24, bsize, 16);

		textBox = new TextBox(this)
		{
			@Override
			public boolean allowInput()
			{
				return inst.getCanEdit();
			}

			@Override
			public boolean isValid(String txt)
			{
				return value.setValueFromString(Minecraft.getMinecraft().player, txt, true);
			}

			@Override
			public void onTextChanged()
			{
				textBox.textColor = value.getColor();
			}

			@Override
			public void onEnterPressed()
			{
				if (inst.getCanEdit())
				{
					buttonAccept.onClicked(MouseButton.LEFT);
				}
			}
		};

		textBox.setPosAndSize(8, 8, width - 16, 16);
		textBox.setText(value.getString());
		textBox.setCursorPosition(textBox.getText().length());
		textBox.setFocused(true);
	}

	public GuiEditConfigValue(String name, ConfigValue val, IConfigValueEditCallback c)
	{
		this(new ConfigValueInstance(name, ConfigGroup.DEFAULT, val), c);
	}

	@Override
	public void addWidgets()
	{
		add(buttonCancel);
		add(buttonAccept);
		add(textBox);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		GuiScreen screen = getPrevScreen();
		return screen != null && screen.doesGuiPauseGame();
	}
}