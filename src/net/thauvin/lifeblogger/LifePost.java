/*
 * @(#)LifePost.java
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

import java.io.DataOutputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;


/**
 * The <code>LifePost</code> class posts a new blog entry using the blogger.newPost() XML-RPC method.
 *
 * @author <a href="http://www.thauvin.net/erik/">Erik C. Thauvin</a>
 * @version $Revision$, $Date$
 *
 * @created Jul 21, 2004
 * @since 1.0
 */
public class LifePost extends LifeAction
{
	private final String _blogEntry;
	private final String _blogID;
	private final boolean _publish;

	/**
	 * Creates a new LifePost object.
	 *
	 * @param thinlet The Thinlet instance.
	 * @param url The MetaWeblog XML-RPC URL.
	 * @param blogID The blog ID.
	 * @param login The MetaWeblog login username.
	 * @param password The MetaWeblog login password.
	 * @param blogEntry The blog entry.
	 * @param publish DOCUMENT ME!
	 *
	 * @throws IOException If an error occurs while creating the object.
	 */
	public LifePost(LifeBlogger thinlet, String url, String blogID, String login, String password, String blogEntry,
					boolean publish)
			 throws IOException
	{
		super(thinlet, url, login, password);

		_blogEntry = blogEntry;
		_blogID = blogID;
		_publish = publish;
	}

	/**
	 * Performs the action.
	 *
	 * @see Thread#run()
	 */
	public final void run()
	{
		try
		{
			getThinlet().add(getDialog());

			final URL url = new URL(getHost());

			if (!"http".equalsIgnoreCase(url.getProtocol()))
			{
				throw new IOException("Unsupported URL protocol: " + url.getProtocol());
			}

			final StringBuffer request =
				new StringBuffer("<?xml version=\"1.0\"?><methodCall><methodName>blogger.newPost</methodName><params><param><value><string>0a6afffffffaffffffb8ffffff8569474cffffffc778500c03ffffffecffffff876116565a27283bffffffda56</string></value></param><param><value><string>").append(_blogID)
																																																																					  .append("</string></value></param><param><value><string>")
																																																																					  .append(getLogin())
																																																																					  .append("</string></value></param><param><value><string>")
																																																																					  .append(getPassword())
																																																																					  .append("</string></value></param><param><value><string>")
																																																																					  .append(textToXML(_blogEntry))
																																																																					  .append("</string></value></param><param><value><boolean>")
																																																																					  .append(String.valueOf(_publish))
																																																																					  .append("</boolean></value></param></params></methodCall>");
			final URLConnection urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Length", String.valueOf(request.length()));
			urlConn.setRequestProperty("Content-Type", "text/xml");

			final DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
			dos.write(request.toString().getBytes());
			dos.flush();
			dos.close();

			//System.out.println(request);
			final LifeRPCResponse xmlrpc = new LifeRPCResponse(urlConn.getInputStream());

			if (xmlrpc.isValidResponse())
			{
				//getThinlet().closeDialog(getDialog());
				getThinlet().setIcon(getThinlet().find(getDialog(), "iconlbl"), "icon",
									 getThinlet().getIcon("/icon/info.gif"));
				getThinlet().setString(getThinlet().find(getDialog(), "message"), "text",
									   "Post successful. (ID " + xmlrpc.getResponse() + ')');
				getThinlet().setBoolean(getThinlet().find(getDialog(), "closebtn"), "enabled", true);
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
	}

	/**
	 * Converts a character to XML entity.
	 *
	 * @param ch The character to convert.
	 *
	 * @return The converted string.
	 */
	private String charToXML(char ch)
	{
		final int c;

		// Convert left bracket
		if (ch == '<')
		{
			return ("&lt;");
		}

		// Convert ampersand
		else if (ch == '&')
		{
			return ("&amp;");
		}

		// High/Low-ASCII character
		else if ((ch >= 128) || (ch < 32))
		{
			c = (int) ch;

			return ("&#" + c + ';');
		}

		// No conversion
		else
		{
			// Return character as string
			return (String.valueOf(ch));
		}
	}

	/**
	 * Converts a text string to XML entities.
	 *
	 * @param text The string to convert.
	 *
	 * @return The converted string.
	 */
	private String textToXML(String text)
	{
		final StringBuffer html = new StringBuffer();

		// Loop thru each characters of the text
		for (int i = 0; i < text.length(); i++)
		{
			// Convert character to XML
			html.append(charToXML(text.charAt(i)));
		}

		// Return XML string
		return html.toString();
	}
}
