/*--
 * Copyright 2010 René M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.io;

/**
 * A source reader.
 *
 * @author René M. de Bloois
 */
public interface SourceReader
{
	/**
	 * Close the reader.
	 */
	void close();

	/**
	 * Reads the next line from the source. The current line number is incremented.
	 *
	 * @return The next line or null of there are no more lines to be read.
	 */
	String readLine();

	/**
	 * Returns the current line number. The current line number is the line that is about to be read with
	 * {@link #readLine()} or is being read with {@link #read()}.
	 *
	 * @return The current line number.
	 */
	// TODO Should line number be a long?
	int getLineNumber();

	/**
	 * Reads a character. Carriage return characters (\r) are filtered out:
	 * <ul>
	 * <li>\r\n becomes \n (Microsoft)</li>
	 * <li>\r without \n becomes \n (Apple)</li>
	 * </ul>
	 * The line number is incremented when it returns a \n.
	 *
	 * @return A character. A carriage return is never returned.
	 */
	int read();

	/**
	 * Reads a character. The line number is incremented when it finds a \r or a \n that is not preceded by a \r.
	 *
	 * @return A character.
	 */
	int readRaw();

	/**
	 * Rewinds one character. The line number is decremented appropriately.
	 */
	void rewind();

	/**
	 * Mark the current read position.
	 */
	void mark();

	/**
	 * Reset the read position to the marked position.
	 */
	void reset();

	/**
	 * Record the characters being read from the current position.
	 */
	void record();

	/**
	 * Return the string that has been recorded, and stop recording.
	 *
	 * @return The string that has been recorded.
	 */
	String getRecorded();

	/**
	 * @return The underlying resource.
	 */
	Resource getResource();

	/**
	 * @return The current location.
	 */
	SourceLocation getLocation();

	/**
	 * @return The location of the last read character.
	 */
	SourceLocation getLastLocation();

	/**
	 * Returns the character encoding of the source.
	 *
	 * @return The character encoding of the source.
	 */
	String getEncoding();
}
