/*
 * @(#)LifeAction.java
 *
 * Copyright (C) 2004 by Erik C. Thauvin (erik@thauvin.net)
 * All rights reserved.
 *
 * $Id$
 *
 */
package net.thauvin.lifeblogger;

import java.awt.*;

import java.io.IOException;


/**
 * The <code>LifeAction</code> class provides the base functionality for all actions.
 *
 * @author Erik C. Thauvin
 * @version $Revision$, $Date$
 *
 * @created Jul 24, 2004
 * @since 1.0
 */
public abstract class LifeAction extends Thread
{
	/**
	 * The Thinlet instance.
	 */
	private final LifeBlogger _thinlet;

	/**
	 * The Transfer dialog.
	 */
	private final Object _dialog;

	/**
	 * The host name.
	 */
	private final String _host;

	/**
	 * The login name.
	 */
	private final String _login;

	/**
	 * The password.
	 */
	private final String _password;

	/**
	 * Creates a new LifeAction object.
	 *
	 * @param thinlet The Thinlet instance.
	 * @param host The host.
	 * @param login The login name.
	 * @param password The password.
	 *
	 * @throws IOException If an error occurs while creating the object.
	 */
	protected LifeAction(LifeBlogger thinlet, String host, String login, String password)
				  throws IOException
	{
		_thinlet = thinlet;
		_host = host;
		_login = login;
		_password = password;
		_dialog = _thinlet.parse("transfer.xml");
	}

	/**
	 * Performs the action.
	 *
	 * @see Thread#run()
	 */
	public abstract void run();

	/**
	 * Returns the Transfer dialog.
	 *
	 * @return The dialog.
	 */
	protected final Object getDialog()
	{
		return _dialog;
	}

	/**
	 * Returns the host name.
	 *
	 * @return The host.
	 */
	protected final String getHost()
	{
		return _host;
	}

	/**
	 * Returns the login name.
	 *
	 * @return The login.
	 */
	protected final String getLogin()
	{
		return _login;
	}

	/**
	 * Returns the password.
	 *
	 * @return The password.
	 */
	protected final String getPassword()
	{
		return _password;
	}

	/**
	 * Returns the Thinlet instance.
	 *
	 * @return The Thinlet.
	 */
	protected final LifeBlogger getThinlet()
	{
		return _thinlet;
	}

	/**
	 * Displays an alert message.
	 *
	 * @param message The message to display.
	 */
	protected final void alert(String message)
	{
		Toolkit.getDefaultToolkit().beep();

		getThinlet().setIcon(getThinlet().find(getDialog(), "iconlbl"), "icon", getThinlet().getIcon("/icon/error.gif"));
		getThinlet().setString(getThinlet().find(getDialog(), "message"), "text", message);
		getThinlet().setBoolean(getThinlet().find(getDialog(), "closebtn"), "enabled", true);
	}
}
