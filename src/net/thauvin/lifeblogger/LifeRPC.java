/*
 * @(#)LifeRPC.java
 *
 * Copyright (C) 2005 by Erik C. Thauvin (erik@thauvin.net)
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

import org.apache.xmlrpc.Base64;
import org.apache.xmlrpc.XmlRpcClient;

import java.io.*;

import java.util.Hashtable;
import java.util.Vector;


/**
 * The <code>LifeRPC</code> class implements the blogger.newPost, metaWeblog.newPost, metaWeblog.newMediaObject and
 * ta.Entry.Update XML-RPC methods.
 *
 * @author  Erik C. Thauvin
 * @version $Revision$, $Date$
 * @created Apr 13, 2005
 * @since   1.0
 */
public class LifeRPC
{
	/**
	 * Disables the default constructor.
	 *
	 * @throws UnsupportedOperationException if the constructor is called.
	 */
	private LifeRPC()
			 throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Illegal constructor call.");
	}

	/**
	 * Implements the blogger.newPost XML-RPC call.
	 *
	 * @param  url         The XML-RPC URL endpoint.
	 * @param  blogID      The blog ID.
	 * @param  login       The login username.
	 * @param  password    The login password.
	 * @param  description The blog description's text/description.
	 *
	 * @return The post ID.
	 *
	 * @throws Exception If an error occurred while posting.
	 */
	public static String bloggerNewPost(String url, String blogID, String login, String password, String description)
								 throws Exception
	{
		final XmlRpcClient xmlrpc = new XmlRpcClient(url);
		final Vector params = new Vector(0);

		// Set the API key
		params.add("0a6afffffffaffffffb8ffffff8569474cffffffc778500c03ffffffecffffff876116565a27283bffffffda56");

		params.add(blogID);
		params.add(login);
		params.add(password);
		params.add(description);

		// Set the publish flag
		params.add(Boolean.valueOf(true));

		return ((String) xmlrpc.execute("blogger.newPost", params));

	}

	/**
	 * Implements blogger.newPost XML-RPC call.
	 *
	 * @param  url      The XML-RPC URL endpoint.
	 * @param  blogID   The blog ID.
	 * @param  login    The login username.
	 * @param  password The login password.
	 * @param  filename The name of the object to store.
	 * @param  mimeType The object's MIME type.
	 * @param  file     The file to store.
	 *
	 * @return The URL of the new media object.
	 *
	 * @throws Exception If an error occurred while upload the new media object.
	 */
	public static String metaWeblogNewMediaObject(String url, String blogID, String login, String password,
												  String filename, String mimeType, File file)
										   throws Exception
	{
		final XmlRpcClient xmlrpc = new XmlRpcClient(url);
		final Vector params = new Vector(0);

		final InputStream is = new FileInputStream(file);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
		final BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

		final byte[] bytes = new byte[1024];
		int len = 0;

		while ((len = bufferedInputStream.read(bytes)) > 0)
		{
			byteArrayOutputStream.write(bytes, 0, len);
		}

		is.close();
		bufferedInputStream.close();

		params.add(blogID);
		params.add(login);
		params.add(password);

		final Hashtable media = new Hashtable(0);

		media.put("name", filename);
		media.put("type", mimeType);
		media.put("bits", byteArrayOutputStream.toByteArray());

		params.add(media);

		final Hashtable response = (Hashtable) xmlrpc.execute("metaWeblog.newMediaObject", params);

		return ((String) response.get("url"));
	}

	/**
	 * Implements the metaWeblog.newPost XML-RPC call.
	 *
	 * @param  url         The XML-RPC URL endpoint.
	 * @param  blogID      The blog ID.
	 * @param  login       The login username.
	 * @param  password    The login password.
	 * @param  title       The blog description's title.
	 * @param  description The blog description's text/description.
	 *
	 * @return The post ID.
	 *
	 * @throws Exception If an error occurred while posting.
	 */
	public static String metaWeblogNewPost(String url, String blogID, String login, String password, String title,
										   String description)
									throws Exception
	{
		final XmlRpcClient xmlrpc = new XmlRpcClient(url);
		final Vector params = new Vector(0);

		params.add(blogID);
		params.add(login);
		params.add(password);

		final Hashtable blogEntry = new Hashtable(0);
		blogEntry.put("title", title);
		blogEntry.put("description", description);
		params.add(blogEntry);

		// Set the publish flag
		params.add(Boolean.valueOf(true));

		return ((String) xmlrpc.execute("metaWeblog.newPost", params));

	}

	/**
	 * Implements the metaWeblog.newPost XML-RPC call.
	 *
	 * @param  url         The XML-RPC URL endpoint.
	 * @param  blogID      The blog ID.
	 * @param  login       The login username.
	 * @param  password    The login password.
	 * @param  title       The blog description's title.
	 * @param  description The blog description's text/description.
	 * @param  file        The file location.
	 * @param  fileType    The file type.
	 *
	 * @return The post ID.
	 *
	 * @throws Exception If an error occurred while posting.
	 */
	public static String taEntryUpdate(String url, String blogID, String login, String password, String title,
									   String description, String file, String fileType)
								throws Exception
	{
		final XmlRpcClient xmlrpc = new XmlRpcClient(url);
		final Vector params = new Vector(0);

		final InputStream is = new FileInputStream(file);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
		final BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

		final byte[] bytes = new byte[1024];
		int len = 0;

		while ((len = bufferedInputStream.read(bytes)) > 0)
		{
			byteArrayOutputStream.write(bytes, 0, len);
		}

		is.close();
		bufferedInputStream.close();

		final byte[] encodedBytes = Base64.encode(byteArrayOutputStream.toByteArray());

		// Set the API key
		params.add("TA113805");

		params.add(login);
		params.add(password);
		params.add(blogID);

		// Set the entry ID
		params.add("0");

		params.add(title);
		params.add(description);

		// Set the category ID
		params.add("0");

		params.add(new String(encodedBytes));
		params.add(fileType);

		return ((String) xmlrpc.execute("ta.entry.Update", params));
	}
}
