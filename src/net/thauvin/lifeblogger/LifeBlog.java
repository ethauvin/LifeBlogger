/*
 * @(#)LifeBlog.java
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

import java.io.File;
import java.io.IOException;


/**
 * The <code>LifeBlog</code> abstract class provides the base functionality for file transfer-based ({@link LifeFTP},
 * {@link LifeMediaObject}, etc.) actions.
 *
 * @author Erik C. Thauvin
 * @version $Revision$, $Date$
 *
 * @created Jul 20, 2004
 * @since 1.0
 */
public abstract class LifeBlog extends LifeAction
{
	/**
	 * The file to upload/store.
	 */
	private final File _file;

	/**
	 * The file name.
	 */
	private final String _filename;

	/**
	 * The path/location.
	 */
	private final String _path;

	/**
	 * Creates a new LifeBlog object.
	 *
	 * @param thinlet The Thinlet instance.
	 * @param host The host.
	 * @param login The login name.
	 * @param password The password.
	 * @param path The path/location.
	 * @param filename The name of the file to upload/store.
	 * @param file The file to upload.
	 *
	 * @throws IOException If an error occurs while creating the object.
	 */
	protected LifeBlog(LifeBlogger thinlet, String host, String login, String password, String path, String filename,
					   File file)
				throws IOException
	{
		super(thinlet, host, login, password);

		_path = path;
		_filename = filename;
		_file = file;
	}

	/**
	 * Returns the file.
	 *
	 * @return The file.
	 */
	protected final File getFile()
	{
		return _file;
	}

	/**
	 * Returns the file name.
	 *
	 * @return The file name.
	 */
	protected final String getFilename()
	{
		return _filename;
	}

	/**
	 * Returns the path/location.
	 *
	 * @return The path.
	 */
	protected final String getPath()
	{
		return _path;
	}
}
