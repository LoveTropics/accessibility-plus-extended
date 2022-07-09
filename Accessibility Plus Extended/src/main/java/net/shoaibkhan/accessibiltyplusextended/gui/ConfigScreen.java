package net.shoaibkhan.accessibiltyplusextended.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.resources.language.I18n;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;

public class ConfigScreen extends CottonClientScreen {
    public ConfigScreen(GuiDescription description, String titleKey) {
        super(description);
        NarratorPlus.narrate(I18n.get("gui.apextended.config." + titleKey));
    }
}
