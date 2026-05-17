package com.corpboostingqol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class CorpBoostingQOLWarningOverlay extends Overlay
{
	// Matches RuneLite's notification panel aesthetic:
	// near-black semi-transparent background, yellow for warnings, red for actionable errors.
	private static final Color BOX_BG        = new Color(0, 0, 0, 200);
	private static final Color BORDER_COLOR  = new Color(30, 30, 30, 255);
	private static final Color TEXT_COLOR = Color.WHITE;

	private static final int   BOX_WIDTH  = 280;
	private static final int   ROW_HEIGHT = 26;
	private static final int   PADDING_X  = 10;
	private static final int   ARC        = 4;

	private final CorpBoostingQOLPlugin plugin;
	private final CorpBoostingQOLConfig config;

	@Inject
	public CorpBoostingQOLWarningOverlay(CorpBoostingQOLPlugin plugin, CorpBoostingQOLConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Each entry: { text, "warn" | "critical" }
		List<String[]> rows = new ArrayList<>();

		if (plugin.inCorpCave && config.lunarsEnabled() && plugin.lunarsWarn)
		{
			rows.add(new String[]{"NOT ON LUNAR SPELLBOOK", "critical"});
		}

		if (plugin.inCorpCave && config.vengEnabled() && plugin.vengReady)
		{
			rows.add(new String[]{"CAST VENG", "warn"});
		}

		if (plugin.inCorpCave && config.quickPrayerEnabled() && plugin.quickPrayerWarn)
		{
			rows.add(new String[]{"QUICK PRAYERS OFF", "warn"});
		}

		if (config.bloodFuryEnabled())
		{
			if (plugin.bloodFuryNoData || plugin.bloodFuryCharges == -1)
			{
				rows.add(new String[]{"Blood Fury: inspect amulet", "critical"});
			}
			else if (plugin.bloodFuryWarn)
			{
				rows.add(new String[]{"Blood Fury: " + plugin.bloodFuryCharges + " charges", "warn"});
			}
		}

		if (config.runePouchEnabled() && plugin.runePouchWarnings != null)
		{
			for (String warning : plugin.runePouchWarnings)
			{
				rows.add(new String[]{warning, "warn"});
			}
		}

		if (config.suppliesEnabled() && plugin.supplyWarn)
		{
			rows.add(new String[]{config.supplyType().displayName + ": " + plugin.supplyCount + " left", "warn"});
		}

		if (config.tomeOfWaterEnabled())
		{
			if (plugin.tomeOfWaterNoData)
			{
				rows.add(new String[]{"Tome of Water: check tome", "critical"});
			}
			else if (plugin.tomeOfWaterWarn)
			{
				rows.add(new String[]{"Tome of Water: " + plugin.tomeOfWaterCharges + " charges", "warn"});
			}
		}

		if (config.serpHelmEnabled())
		{
			if (plugin.serpHelmNoData)
			{
				rows.add(new String[]{"Serp Helm: check helm", "critical"});
			}
			else if (plugin.serpHelmWarn)
			{
				rows.add(new String[]{"Serp Helm: " + plugin.serpHelmCharges + " charges", "warn"});
			}
		}

		if (config.toxicStaffEnabled())
		{
			if (plugin.toxicStaffNoData)
			{
				rows.add(new String[]{"Toxic Staff: check staff", "critical"});
			}
			else if (plugin.toxicStaffWarn)
			{
				rows.add(new String[]{"Toxic Staff: " + plugin.toxicStaffCharges + " charges", "warn"});
			}
		}

		if (rows.isEmpty())
		{
			return null;
		}

		Font font = FontManager.getRunescapeBoldFont().deriveFont(Font.PLAIN, 16f);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();

		int totalHeight = rows.size() * ROW_HEIGHT;

		// Background + border
		g.setColor(BOX_BG);
		g.fillRoundRect(0, 0, BOX_WIDTH, totalHeight, ARC, ARC);
		g.setColor(BORDER_COLOR);
		g.drawRoundRect(0, 0, BOX_WIDTH - 1, totalHeight - 1, ARC, ARC);

		for (int i = 0; i < rows.size(); i++)
		{
			String text = rows.get(i)[0];
			int rowY    = i * ROW_HEIGHT;

			// Divider between rows
			if (i > 0)
			{
				g.setColor(BORDER_COLOR);
				g.drawLine(0, rowY, BOX_WIDTH, rowY);
			}

			// Truncate with ellipsis if text overflows box width
			int maxWidth = BOX_WIDTH - PADDING_X * 2;
			while (fm.stringWidth(text) > maxWidth && text.length() > 1)
			{
				text = text.substring(0, text.length() - 4) + "...";
			}

			g.setColor(TEXT_COLOR);
			int ty = rowY + (ROW_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
			g.drawString(text, PADDING_X, ty);
		}

		return new Dimension(BOX_WIDTH, totalHeight);
	}
}
