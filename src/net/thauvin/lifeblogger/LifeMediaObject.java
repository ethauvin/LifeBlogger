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
 * @author  <a href="http://www.thauvin.net/erik/">Erik C. Thauvin</a>
 * @version $Revision$, $Date$
 * @created Jul 21, 2004
 * @since   1.0
 */
public class LifeMediaObject extends LifeBlog
{
	private final String _blogID;
	private final String _mimeType;

	/**
	 * Creates a new LifeMediaObject object.
	 *
	 * @param  thinlet  The Thinlet instance.
	 * @param  url      The XML-RPC URL endpoint.
	 * @param  blogID   The blog ID.
	 * @param  login    The login username.
	 * @param  password The login password.
	 * @param  filename The name of the object to store.
	 * @param  mimeType The object's MIME type.
	 * @param  file     The file to store.
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
		getThinlet().add(getDialog());

		try
		{
			final String url = LifeRPC.metaWeblogNewMediaObject(getHost(), _blogID, getLogin(), getPassword(),
																getFilename(), _mimeType, getFile());
			getThinlet().closeDialog(getDialog());
			getThinlet().postDialog(url, getFilename(), true);
		}
		catch (Exception e)
		{
			alert(e.getMessage());
		}
	}
}
