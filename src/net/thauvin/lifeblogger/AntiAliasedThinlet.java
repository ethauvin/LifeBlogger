/*
 * @(#)AntiAliasedThinlet.java
 *
 * Copyright (C) 2004 by Erik C. Thauvin (erik@thauvin.net)
 * All rights reserved.
 *
 * $Id$
 *
 */
package net.thauvin.lifeblogger;

import thinlet.Thinlet;

import java.awt.*;


/**
 * The <code>AntiAliasedThinlet</code> class implements an anti-aliased {@link thinlet.Thinlet Thinlet} component.
 *
 * @author Erik C. Thauvin
 * @version $Revision$, $Date$
 *
 * @created Nov 25, 2004
 * @since 1.0
 */
public class AntiAliasedThinlet extends Thinlet
{
	/**
	 * Creates a new AntiAliasedThinlet object.
	 */
	public AntiAliasedThinlet()
	{
		super();
	}

	/**
	 * Paints the components inside the graphics clip area.
	 *
	 * @param g The graphics clip area.
	 *
	 * @see thinlet.Thinlet#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g)
	{
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g);
	}
}
