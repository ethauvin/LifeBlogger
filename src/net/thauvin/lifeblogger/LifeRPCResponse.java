/*
 * @(#)LifeRPCResponse.java
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

import thinlet.Thinlet;

import java.io.IOException;
import java.io.InputStream;


/**
 * The <code>LifeRPCResponse</code> class uses the Thinlet DOM parser to process a XML-RCP response.
 *
 * @author <a href="http://www.thauvin.net/erik/">Erik C. Thauvin</a>
 * @version $Revision$, $Date$
 *
 * @created Jul 21, 2004
 * @since 1.0
 */
public class LifeRPCResponse extends Thinlet
{
	private final InputStream _inputStream;
	private String _response;

	/**
	 * Creates a new LifeRPCResponse object.
	 *
	 * @param inputStream The input stream.
	 */
	public LifeRPCResponse(InputStream inputStream)
	{
		_inputStream = inputStream;
	}

	/**
	 * Returns the XML-RPC response/fault string.
	 *
	 * @return The response string.
	 */
	public final String getResponse()
	{
		return _response;
	}

	/**
	 * Parses and validates the XML-RPC response.
	 *
	 * @return <code>true</code> is the response is valid, <code>false</code> if it is a fault.
	 *
	 * @throws IOException If an error occurs while processing the response.
	 */
	public final boolean isValidResponse()
								  throws IOException
	{
		try
		{
			final Object dom = parseDOM(_inputStream);
			final Object params = getDOMNode(dom, "params", 0);

			if (params != null)
			{
				final Object param = getDOMNode(params, "param", 0);
				final Object value = getDOMNode(param, "value", 0);
				final Object struct = getDOMNode(value, "struct", 0);

				if (struct == null)
				{
					final Object string = getDOMNode(value, "string", 0);

					if (string == null)
					{
						_response = getDOMText(value);
					}
					else
					{
						_response = getDOMText(string);
					}
				}
				else
				{
					final Object member = getDOMNode(struct, "member", 0);
					final Object url = getDOMNode(member, "value", 0);
					final Object string = getDOMNode(url, "string", 0);

					if (string == null)
					{
						_response = getDOMText(url);
					}
					else
					{
						_response = getDOMText(string);
					}
				}

				return true;
			}
			else
			{
				final Object fault = getDOMNode(dom, "fault", 0);
				final Object value = getDOMNode(fault, "value", 0);
				final Object struct = getDOMNode(value, "struct", 0);
				Object member = getDOMNode(struct, "member", 0);

				if (getDOMCount(struct, "member") > 1)
				{
					member = getDOMNode(struct, "member", 1);
				}

				final Object error = getDOMNode(member, "value", 0);
				final Object string = getDOMNode(error, "string", 0);

				if (string != null)
				{
					_response = getDOMText(string);
				}
				else
				{
					throw new IOException("Could not parse the XML-RPC error response.");
				}

				return false;
			}
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				_inputStream.close();
			}
			catch (IOException ignore)
			{
				; // Do nothing
			}
		}
	}
}
