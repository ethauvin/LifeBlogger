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

import java.io.*;

import java.net.URL;
import java.net.URLConnection;


/**
 * The <code>LifeMediaObject</code> class posts a new media object via the metaWeblog.newMediaObject XML-RPC method.
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
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Base64.OutputStream out = null;

		try
		{
			getThinlet().add(getDialog());

			final URL url = new URL(getHost());

			if (!"http".equalsIgnoreCase(url.getProtocol()))
			{
				throw new IOException("Unsupported URL protocol: " + url.getProtocol());
			}

			final File tmpFile = File.createTempFile(ReleaseInfo.getProject(), ".b64");
			tmpFile.deleteOnExit();

			fis = new FileInputStream(getFile());
			fos = new FileOutputStream(tmpFile);
			bos = new BufferedOutputStream(fos);
			out = new Base64.OutputStream(bos, Base64.ENCODE | Base64.DONT_BREAK_LINES);

			final byte[] buf = new byte[1024];
			int len;

			while ((len = fis.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}

			fis.close();

			final StringBuffer start =
				new StringBuffer("<?xml version=\"1.0\"?><methodCall><methodName>metaWeblog.newMediaObject</methodName><params><param><value><string>").append(_blogID)
																																					   .append("</string></value></param><param><value><string>")
																																					   .append(getLogin())
																																					   .append("</string></value></param><param><value><string>")
																																					   .append(getPassword())
																																					   .append("</string></value></param><param><value><struct><member><name>bits</name><value><base64>");

			final StringBuffer end =
				new StringBuffer("</base64></value></member><member><name>name</name><value><string>").append(getFilename())
																									  .append("</string></value></member><member><name>type</name><value><string>")
																									  .append(_mimeType)
																									  .append("</string></value></member></struct></value></param></params></methodCall>");

			final URLConnection urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Length",
									   String.valueOf(start.length() + tmpFile.length() + end.length()));
			urlConn.setRequestProperty("Content-Type", "text/xml");

			final DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
			dos.write(start.toString().getBytes());
			dos.flush();

			fis = new FileInputStream(tmpFile);

			while ((len = fis.read(buf)) > 0)
			{
				dos.write(buf, 0, len);
				dos.flush();
			}

			fis.close();

			dos.write(end.toString().getBytes());
			dos.flush();

			dos.close();

			final LifeRPCResponse xmlrpc = new LifeRPCResponse(urlConn.getInputStream());

			if (xmlrpc.isValidResponse())
			{
				getThinlet().closeDialog(getDialog());
				getThinlet().postDialog(xmlrpc.getResponse(), getFilename());
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

			if (bos != null)
			{
				try
				{
					bos.close();
				}
				catch (IOException ignore)
				{
					; // Do nothing
				}
			}

			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException ignore)
				{
					; // Do nothing
				}
			}

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
