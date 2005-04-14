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

import java.io.IOException;


/**
 * The <code>LifePost</code> class posts a new blog entry using the blogger.newPost() XML-RPC method.
 *
 * @author  <a href="http://www.thauvin.net/erik/">Erik C. Thauvin</a>
 * @version $Revision$, $Date$
 * @created Jul 21, 2004
 * @since   1.0
 */
public class LifePost extends LifeAction
{
	private final String _blogID;
	private final String _entry;
	private final String _file;
	private final String _fileType;
	private final String _title;

	/**
	 * Creates a new LifePost object.
	 *
	 * @param  thinlet     The Thinlet instance.
	 * @param  url         The MetaWeblog XML-RPC URL.
	 * @param  blogID      The blog ID.
	 * @param  login       The MetaWeblog login username.
	 * @param  password    The MetaWeblog login password.
	 * @param  title       The post's title, if any.
	 * @param  description The blog description's text/description.
	 * @param  file        The file location, if any.
	 * @param  fileType    The file type, if any.
	 *
	 * @throws IOException If an error occurs while creating the object.
	 */
	public LifePost(LifeBlogger thinlet, String url, String blogID, String login, String password, String title,
					String description, String file, String fileType)
			 throws IOException
	{
		super(thinlet, url, login, password);

		_entry = description;
		_blogID = blogID;
		_title = title;
		_file = file;
		_fileType = fileType;
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
			final String response;

			if (_file.length() != 0)
			{
				response = LifeRPC.taEntryUpdate(getHost(), _blogID, getLogin(), getPassword(), _title, _entry, _file,
												 _fileType);
			}
			else if (_title != null)
			{
				response = LifeRPC.metaWeblogNewPost(getHost(), _blogID, getLogin(), getPassword(), _title, _entry);
			}
			else
			{
				response = LifeRPC.bloggerNewPost(getHost(), _blogID, getLogin(), getPassword(), _entry);
			}

			getThinlet().setIcon(getThinlet().find(getDialog(), "iconlbl"), "icon",
								 getThinlet().getIcon("/icon/info.gif"));
			getThinlet().setString(getThinlet().find(getDialog(), "message"), "text",
								   "Post successful. (ID " + response + ')');
			getThinlet().setBoolean(getThinlet().find(getDialog(), "closebtn"), "enabled", true);
		}
		catch (Exception e)
		{
			alert(e.getMessage());
		}

	}


}
