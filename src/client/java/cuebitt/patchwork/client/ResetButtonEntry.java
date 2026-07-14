package cuebitt.patchwork.client;

import java.util.List;
import java.util.Optional;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

/**
 * A Cloth Config list entry that renders a clickable button on the value side, used to fire an
 * action such as resetting the per-trowel placement modes.
 *
 * <p>Cloth Config v15 no longer ships a button builder, so this lightweight entry draws a Minecraft
 * {@link Button} (registered as a child so it receives clicks) and forwards presses to the supplied
 * runnable.
 */
public class ResetButtonEntry extends AbstractConfigListEntry<Component> {
  private final Component name;
  private final Runnable onClick;
  private final Button button;

  public ResetButtonEntry(Component name, Component buttonText, Runnable onClick) {
    super(name, false);
    this.name = name;
    this.onClick = onClick;
    this.button = Button.builder(buttonText, b -> onClick.run()).bounds(0, 0, 0, 0).build();
  }

  @Override
  public void render(
      GuiGraphics gui,
      int index,
      int y,
      int x,
      int entryWidth,
      int entryHeight,
      int mouseX,
      int mouseY,
      boolean hovered,
      float delta) {
    Rectangle area = getEntryArea(x, y, entryWidth, entryHeight);

    int color = getPreferredTextColor();
    gui.drawString(
        Minecraft.getInstance().font,
        name,
        area.x + 11,
        area.y + area.height / 2 - 4,
        color,
        false);

    int buttonWidth = Math.min(120, area.width / 2 - 10);
    int buttonHeight = 20;
    int buttonX = area.x + area.width - buttonWidth - 10;
    int buttonY = area.y + (area.height - buttonHeight) / 2;
    button.setPosition(buttonX, buttonY);
    button.setWidth(buttonWidth);
    button.setHeight(buttonHeight);

    button.render(gui, mouseX, mouseY, delta);
  }

  @Override
  public List<? extends GuiEventListener> children() {
    return List.of(button);
  }

  @Override
  public boolean isRequiresRestart() {
    return false;
  }

  @Override
  public void setRequiresRestart(boolean requiresRestart) {}

  @Override
  public Component getFieldName() {
    return name;
  }

  @Override
  public Optional<Component> getDefaultValue() {
    return Optional.empty();
  }

  @Override
  public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
    return List.of();
  }

  @Override
  public Component getValue() {
    return name;
  }
}
