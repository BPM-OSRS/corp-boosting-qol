package net.runelite.client.plugins.corpboostingqol;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class CorpBoostingQOLCombatOverlay extends Overlay
{
	private final CorpBoostingQOLPlugin plugin;
	private final CorpBoostingQOLConfig config;

	@Inject
	public CorpBoostingQOLCombatOverlay(CorpBoostingQOLPlugin plugin, CorpBoostingQOLConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		// DYNAMIC keeps the full-screen fill behaviour for the combat flash.
		// It cannot be meaningfully "dragged" because it covers the whole viewport,
		// so DYNAMIC is correct here. The warning overlay uses TOP_LEFT and is draggable.
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!config.combatOverlayEnabled())
		{
			return null;
		}

		if (!plugin.inCorpCave || plugin.inCombat)
		{
			return null;
		}

		Rectangle bounds = g.getClipBounds();
		if (bounds == null)
		{
			return null;
		}

		g.setColor(config.combatOverlayColor());
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		return null;
	}
}