/*
 * @(#)LifeBlogger.java
 *
 * Copyright (c) 2004, Erik C. Thauvin (http://www.thauvin.net/erik/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the authors nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $Id$
 *
 */
package net.thauvin.lifeblogger;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

import java.awt.*;

import java.io.*;

import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.Properties;

import javax.swing.*;


/**
 * The <code>LifeBlogger</code> class uploads/posts Lifeblog's favorite data to a blog.
 *
 * @author Erik C. Thauvin
 * @version $Revision$, $Date$
 *
 * @created Jul 19, 2004
 * @since 1.0
 */
public class LifeBlogger extends Thinlet
{
	private static final String DRIVER = "SQLite.JDBCDriver";
	private static final String PREFS =
		System.getProperty("user.home") + File.separator + ReleaseInfo.getProject() + ".properties";
	private static final String JDBC_PREFIX = "jdbc:sqlite:/";
	private static final String DATABASE = "\\DataBase\\NokiaLifeblogDataBase.db";
	private final Properties _prefs = new Properties();
	private File _homeDir = new File(System.getProperty("user.home") + "\\My Documents\\NokiaLifeblogData");
	private String _action;

	/**
	 * Creates a new LifeBlogger object.
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	private LifeBlogger()
				 throws IOException
	{
		setFont(new Font("SansSerif", Font.PLAIN, 11));

		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(PREFS);
			_prefs.load(fis);
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException ignore)
				{
					; // Do nothing
				}
			}
		}

		_homeDir = new File(_prefs.getProperty("home", _homeDir.getAbsolutePath()));
		_action = _prefs.getProperty("via", "ftp");

		try
		{
			Class.forName(DRIVER);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		final Object main = parse("main.xml");
		setBoolean(find(main, _action), "selected", true);
		add(main);
	}

	/**
	 * The main program.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{
		try
		{
			new FrameLauncher("LifeBlogger",
							  (new ImageIcon(LifeBlogger.class.getResource("/icon/icon.gif"))).getImage(),
							  new LifeBlogger(), 400, 400, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sets the blog action.
	 *
	 * @param action The action
	 */
	public final void setAction(String action)
	{
		_action = action;
		_prefs.put("via", _action);
		savePrefs();
	}

	/**
	 * Displays the about dialog.
	 */
	public final void about()
	{
		try
		{
			final Object about = parse("about.xml");
			setString(about, "text", "About " + ReleaseInfo.getProject());
			setString(find(about, "version"), "text",
					  "Version " + ReleaseInfo.getVersion() + " - Build " + ReleaseInfo.getBuildNumber());

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			setString(find(about, "date"), "text", sdf.format(ReleaseInfo.getBuildDate()));
			add(about);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Populates the table rows.
	 *
	 * @param thinlet The Thinlet object.
	 * @param table The table to populate.
	 * @param buttonsPanel The panel containing the buttons/label to update.
	 *
	 * @throws Exception If an error occurs while populate the table.
	 */
	public final void addTableRows(Thinlet thinlet, Object table, Object buttonsPanel)
							throws Exception
	{
		if (!_homeDir.exists())
		{
			final JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Select Nokia LifeBlog document directory:");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);

			final int res = fc.showOpenDialog(this);

			if (res == JFileChooser.APPROVE_OPTION)
			{
				_homeDir = fc.getSelectedFile();
				_prefs.put("home", _homeDir.getAbsolutePath());

				savePrefs();
			}
		}

		final Connection con = DriverManager.getConnection(JDBC_PREFIX + _homeDir.getAbsolutePath() + DATABASE);

		final Statement st = con.createStatement();
		final ResultSet rs =
			st.executeQuery("SELECT * FROM HooverObject WHERE MobileFavourite = 'true' ORDER BY TimeStamp DESC");

		Object row;
		Object cell;
		String ts;
		String name;

		boolean first = true;
		int found = 0;

		while (rs.next())
		{
			ts = rs.getString("TimeStamp");
			name = rs.getString("name");

			row = Thinlet.create("row");

			cell = Thinlet.create("cell");
			thinlet.setString(cell, "text", name);

			if (name.toLowerCase().endsWith("jpg"))
			{
				thinlet.setIcon(cell, "icon", getIcon("/icon/image.gif"));
			}
			else if (name.toLowerCase().endsWith("3gp"))
			{
				thinlet.setIcon(cell, "icon", getIcon("/icon/movie.gif"));
			}
			else
			{
				thinlet.setIcon(cell, "icon", getIcon("/icon/text.gif"));
			}

			thinlet.putProperty(cell, "oid", rs.getString("HooverObjectID"));
			thinlet.add(row, cell);

			cell = Thinlet.create("cell");
			thinlet.setString(cell, "text", ts.substring(0, ts.lastIndexOf(':')));
			thinlet.add(row, cell);

			if (first)
			{
				thinlet.setBoolean(row, "selected", true);
				first = false;
			}

			thinlet.add(table, row);

			found++;
		}

		thinlet.setString(find(buttonsPanel, "favslbl"), "text", "Favorites: " + found);

		st.close();
		rs.close();
		con.close();

		toggleButton(table, find(buttonsPanel, "blogbtn"));
	}

	/**
	 * Performs the blog action.
	 *
	 * @param table The table containing the selected item to perform the action on.
	 *
	 * @throws Exception If an error occurs while performing the action.
	 */
	public final void blog(Object table)
					throws Exception
	{
		final int selected = getSelectedIndex(table);

		if (selected != -1)
		{
			final Object row = getItem(table, selected);
			final String name = String.valueOf(getProperty(getItem(row, 0), "oid"));

			final Connection con = DriverManager.getConnection(JDBC_PREFIX + _homeDir.getAbsolutePath() + DATABASE);

			final Statement st = con.createStatement();
			final ResultSet rs = st.executeQuery("SELECT * FROM BinaryItem WHERE HooverObjectID = " + name);

			if (rs.next())
			{
				if ("ftp".equals(_action))
				{
					ftpDialog(_homeDir.getAbsolutePath() + "\\DataStore" + rs.getString("Pathname") +
							  rs.getString("Filename"));
				}
				else
				{
					mwDialog(_homeDir.getAbsolutePath() + "\\DataStore" + rs.getString("Pathname") +
							 rs.getString("Filename"), rs.getString("ObjectMimeType"));
				}
			}

			st.close();
			rs.close();
			con.close();
		}
	}

	/**
	 * Closes the given dialog.
	 *
	 * @param dialog The dialog to close.
	 */
	public final void closeDialog(Object dialog)
	{
		remove(dialog);
	}

	/**
	 * Extis the main program.
	 */
	public final void exit()
	{
		System.exit(0);
	}

	/**
	 * Preforms the FTP action.
	 *
	 * @param dialog The FTP dialog,
	 * @param ftpPanel The panel contaning the FTP data.
	 *
	 * @throws IOException If an error occurs while performing the action.
	 */
	public final void ftp(Object dialog, Object ftpPanel)
				   throws IOException
	{
		final String host = getString(find(ftpPanel, "host"), "text");
		final String login = getString(find(ftpPanel, "login"), "text");
		final String password = getString(find(ftpPanel, "password"), "text");
		final String path = getString(find(ftpPanel, "path"), "text");
		final String filename = getString(find(ftpPanel, "filename"), "text");

		if (host.length() <= 0)
		{
			alert("Please specify a host name.");
		}
		else if (login.length() <= 0)
		{
			alert("Please specify a login name.");
		}
		else if (filename.length() <= 0)
		{
			alert("Please specify a file name.");
		}
		else
		{
			_prefs.put("host", host);
			_prefs.put("login", login);
			_prefs.put("password", password);
			_prefs.put("path", path);

			savePrefs();

			closeDialog(dialog);

			final LifeFTP ftp =
				new LifeFTP(this, host, login, password, path, filename,
							new File(getString(find(ftpPanel, "file"), "text")));
			ftp.start();
		}
	}

	/**
	 * Preforms the MetaWeblog action.
	 *
	 * @param dialog The MetaWeblog dialog,
	 * @param mwPanel The panel contaning the MetaWeblog data.
	 *
	 * @throws IOException If an error occurs while performing the action.
	 */
	public final void metaWeblog(Object dialog, Object mwPanel)
						  throws IOException
	{
		final String host = getString(find(mwPanel, "host"), "text");
		final String login = getString(find(mwPanel, "login"), "text");
		final String password = getString(find(mwPanel, "password"), "text");
		final String filename = getString(find(mwPanel, "filename"), "text");
		final String blogID = getString(find(mwPanel, "blogid"), "text");

		if (host.length() <= 0)
		{
			alert("Please specify a XML-RPC URL.");
		}
		else if (login.length() <= 0)
		{
			alert("Please specify a login name.");
		}
		else if (password.length() <= 0)
		{
			alert("Please specify a password.");
		}
		else if (filename.length() <= 0)
		{
			alert("Please specify a file name.");
		}
		else if (blogID.length() <= 0)
		{
			alert("Please specify a blog ID.");
		}
		else
		{
			_prefs.put("mw-host", host);
			_prefs.put("mw-login", login);
			_prefs.put("mw-password", password);
			_prefs.put("mw-id", blogID);

			savePrefs();

			closeDialog(dialog);

			final LifeMediaObject mw =
				new LifeMediaObject(this, host, blogID, login, password, filename,
									String.valueOf(getProperty(find(mwPanel, "file"), "mtype")),
									new File(getString(find(mwPanel, "file"), "text")));
			mw.start();
		}
	}

	/**
	 * Toggles the given button based on the specified table selection.
	 *
	 * @param table The table.
	 * @param button The button.
	 */
	public final void toggleButton(Object table, Object button)
	{
		setBoolean(button, "enabled", getSelectedIndex(table) != -1);
	}

	/**
	 * Updates the table data.
	 *
	 * @param thinlet The Thinlet object.
	 * @param table The table to update.
	 * @param buttonsPanel The panel containing the buttons/label to update.
	 */
	public final void updateTable(Thinlet thinlet, Object table, Object buttonsPanel)
	{
		thinlet.removeAll(table);

		try
		{
			addTableRows(thinlet, table, buttonsPanel);
		}
		catch (Exception e)
		{
			showException(e);
		}
	}

	/**
	 * Displays an exception stacktrace.
	 *
	 * @param thr The exception.
	 */
	protected final void showException(Throwable thr)
	{
		final StringWriter writer = new StringWriter();
		thr.printStackTrace(new PrintWriter(writer));

		final String trace = writer.toString().replace('\r', ' ').replace('\t', ' ');
		String thrclass = thr.getClass().getName();
		thrclass = thrclass.substring(thrclass.lastIndexOf('.') + 1);

		try
		{
			final Object dialog = parse("exception.xml");
			setString(dialog, "text", thrclass);
			setString(find(dialog, "message"), "text", thr.getMessage());
			setString(find(dialog, "stacktrace"), "text", trace);

			Toolkit.getDefaultToolkit().beep();
			add(dialog);
			requestFocus(find(dialog, "closebtn"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Displays an alert.
	private void alert(String message)
	{
		Toolkit.getDefaultToolkit().beep();

		try
		{
			final Object alert = parse("alert.xml");
			setString(find(alert, "message"), "text", message);

			add(alert);
			requestFocus(find(alert, "closebtn"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Display the FTP dialog.
	private void ftpDialog(String file)
	{
		try
		{
			final Object ftp = parse("ftp.xml");
			setString(find(ftp, "file"), "text", file);
			setString(find(ftp, "filename"), "text", file.substring(file.lastIndexOf('\\') + 1));
			setString(find(ftp, "host"), "text", _prefs.getProperty("host", ""));
			setString(find(ftp, "login"), "text", _prefs.getProperty("login", "anonymous"));
			setString(find(ftp, "password"), "text", _prefs.getProperty("password", ""));
			add(ftp);
			requestFocus(find(ftp, "host"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Display the MetaWeblog dialog.
	private void mwDialog(String file, String mimeType)
	{
		try
		{
			final Object mw = parse("metaweblog.xml");
			setString(find(mw, "file"), "text", file);
			putProperty(find(mw, "file"), "mtype", mimeType);
			setString(find(mw, "filename"), "text", file.substring(file.lastIndexOf('\\') + 1));
			setString(find(mw, "host"), "text", _prefs.getProperty("mw-host", ""));
			setString(find(mw, "login"), "text", _prefs.getProperty("mw-login", "anonymous"));
			setString(find(mw, "password"), "text", _prefs.getProperty("mw-password", ""));
			setString(find(mw, "blogid"), "text", _prefs.getProperty("mw-id", ""));
			add(mw);
			requestFocus(find(mw, "host"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Saves the properties.
	private void savePrefs()
	{
		FileOutputStream out = null;

		try
		{
			out = new FileOutputStream(PREFS);
			_prefs.store(out, ReleaseInfo.getProject() + ' ' + ReleaseInfo.getVersion());
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException ignore)
				{
					; // Do nothing
				}
			}
		}
	}
}