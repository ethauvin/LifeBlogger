/*
 * @(#)LifeFTP.java
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

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * The <code>LifeFTP</code> class stores/uploads a file to a FTP server.
 *
 * @author Erik C. Thauvin
 * @version $Revision$, $Date$
 *
 * @created Jul 20, 2004
 * @since 1.0
 */
public class LifeFTP extends LifeBlog
{
	/**
	 * Creates a new LifeFTP object.
	 *
	 * @param thinlet The Thinlet object.
	 * @param host The FTP host.
	 * @param login The FTP login username.
	 * @param password The FTP login password.
	 * @param path The FTP path to upload to.
	 * @param filename The name of the file to upload.
	 * @param file The file to upload.
	 *
	 * @throws IOException If an error occurs while creating the object.
	 */
	public LifeFTP(LifeBlogger thinlet, String host, String login, String password, String path, String filename,
				   File file)
			throws IOException
	{
		super(thinlet, host, login, password, path, filename, file);
	}

	/**
	 * Performs the FTP transfer.
	 *
	 * @see Thread#run()
	 */
	public final void run()
	{
		boolean success = false;

		try
		{
			getThinlet().add(getDialog());

			final FTPClient ftp = new FTPClient();
			ftp.connect(getHost());
			ftp.login(getLogin(), getPassword());

			success = FTPReply.isPositiveCompletion(ftp.getReplyCode());

			if (!success)
			{
				alert("Invalid login and/or password.");
			}
			else
			{
				if (getPath().length() > 0)
				{
					ftp.changeWorkingDirectory(getPath());

					success = FTPReply.isPositiveCompletion(ftp.getReplyCode());

					if (!success)
					{
						alert("Could not access specified path.");
					}
				}

				if (success)
				{
					final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(getFile()));

					ftp.storeFile(getFilename(), bis);

					bis.close();

					success = FTPReply.isPositiveCompletion(ftp.getReplyCode());

					if (!success)
					{
						alert("An error occurred: " + ftp.getReplyString());
					}
					else
					{
						getThinlet().closeDialog(getDialog());
					}
				}
			}

			ftp.disconnect();
		}
		catch (IOException e)
		{
			getThinlet().closeDialog(getDialog());
			getThinlet().showException(e);
		}
	}
}
