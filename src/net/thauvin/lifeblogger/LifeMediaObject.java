/*
 * @(#)LifeMediaObject.java
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

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import java.io.*;

import java.net.URL;
import java.net.URLConnection;


/**
 * The <code>LifeMediaObject</code> class posts a new media object via the metaWeblog.newMediaObject XML-RPC call.
 *
 * @author Erik C. Thauvin
 * @version $Revision$, $Date$
 *
 * @created Jul 21, 2004
 * @since 1.0
 */
public class LifeMediaObject extends LifeBlog
{
	private final String _blogID;
	private final String _mimeType;

	/**
	 * Creates a new LifeMediaObject object.
	 *
	 * @param thinlet The Thinlet instance.
	 * @param url The MetaWeblog XML-RPC URL.
	 * @param blogID The blog ID.
	 * @param login The MetaWeblog login username.
	 * @param password The MetaWeblog login password.
	 * @param filename The name of the object to store.
	 * @param mimeType The object's MIME type.
	 * @param file The file to store.
	 *
	 * @throws IOException If an error occurs while creating the object.
	 */
	public LifeMediaObject(LifeBlogger thinlet, String url, String blogID, String login, String password,
						   String filename, String mimeType, File file)
					throws IOException
	{
		super(thinlet, url, login, password, "", filename, file);

		_mimeType = mimeType;
		_blogID = blogID;
	}

	/**
	 * Performs the action.
	 *
	 * @see Thread#run()
	 */
	public final void run()
	{
		FileInputStream fis = null;
		final BufferedReader input = null;

		try
		{
			getThinlet().add(getDialog());

			final URL url = new URL(getHost());

			if (!"http".equalsIgnoreCase(url.getProtocol()))
			{
				throw new IOException("Unsupported URL protocol: " + url.getProtocol());
			}

			// The following is a little hackish.
			// A better way would be to generate the request to a temporary file.
			final long len = getFile().length();

			if (len > Integer.MAX_VALUE)
			{
				throw new IOException("Sorry. The file is too large.");
			}

			fis = new FileInputStream(getFile());

			final byte[] bytes = new byte[(int) getFile().length()];

			int offset = 0;
			int numRead = 0;

			while ((offset < bytes.length) && ((numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0))
			{
				offset += numRead;
			}

			if (offset < bytes.length)
			{
				throw new IOException("Could not completely read file: " + getFile().getName());
			}

			final StringBuffer start =
				new StringBuffer("<?xml version=\"1.0\"?><methodCall><methodName>metaWeblog.newMediaObject</methodName><params><param><value><string>").append(_blogID)
																																					   .append("</string></value></param><param><value><string>")
																																					   .append(getLogin())
																																					   .append("</string></value></param><param><value><string>")
																																					   .append(getPassword())
																																					   .append("</string></value></param><param><value><struct><member><name>bits</name><value><base64>");
			final String bits = Base64.encodeBytes(bytes);

			final StringBuffer end =
				new StringBuffer("</base64></value></member><member><name>name</name><value><string>").append(getFilename())
																									  .append("</string></value></member><member><name>type</name><value><string>")
																									  .append(_mimeType)
																									  .append("</string></value></member></struct></value></param></params></methodCall>");

			final URLConnection urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Length", String.valueOf(start.length() + bits.length() + end.length()));
			urlConn.setRequestProperty("Content-Type", "text/xml");

			final DataOutputStream output = new DataOutputStream(urlConn.getOutputStream());
			output.write(start.toString().getBytes());
			output.flush();
			output.write(bits.getBytes());
			output.flush();
			output.write(end.toString().getBytes());
			output.flush();

			output.close();

			final LifeMediaObjResponse xmlrpc = new LifeMediaObjResponse(urlConn.getInputStream());

			if (xmlrpc.isValidResponse())
			{
				getThinlet().setIcon(getThinlet().find(getDialog(), "iconlbl"), "icon",
									 getThinlet().getIcon("/icon/info.gif"));
				getThinlet().setString(getThinlet().find(getDialog(), "message"), "text",
									   "The file can now be accessed at:\n\n" + xmlrpc.getResponse() +
									   "\n\nwhich has been copied to the clipboard.");
				getThinlet().setBoolean(getThinlet().find(getDialog(), "closebtn"), "enabled", true);

				final StringSelection ss = new StringSelection(xmlrpc.getResponse());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
			}
			else
			{
				alert(xmlrpc.getResponse());
			}
		}
		catch (IOException e)
		{
			getThinlet().closeDialog(getDialog());
			getThinlet().showException(e);
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					; // Do nothing
				}
			}

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
	}
}