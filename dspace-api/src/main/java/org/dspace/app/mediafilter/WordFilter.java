/*
 * WordFilter.java
 *
 * Version: $Revision$
 *
 * Date: $Date$
 *
 * Copyright (c) 2002-2009, The DSpace Foundation.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the DSpace Foundation nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.app.mediafilter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import org.textmining.extraction.TextExtractor;
import org.textmining.extraction.word.WordTextExtractor;
import org.textmining.extraction.word.WordTextExtractorFactory;

/*
 * 
 * to do: helpful error messages - can't find mediafilter.cfg - can't
 * instantiate filter - bitstream format doesn't exist
 *  
 */
public class WordFilter extends MediaFilter
{

    private static Logger log = Logger.getLogger(WordFilter.class);

    public String getFilteredName(String oldFilename)
    {
        return oldFilename + ".txt";
    }

    /**
     * @return String bundle name
     *  
     */
    public String getBundleName()
    {
        return "TEXT";
    }

    /**
     * @return String bitstreamformat
     */
    public String getFormatString()
    {
        return "Text";
    }

    /**
     * @return String description
     */
    public String getDescription()
    {
        return "Extracted text";
    }

    /**
     * @param source
     *            source input stream
     * 
     * @return InputStream the resulting input stream
     */
    public InputStream getDestinationStream(InputStream source)
            throws Exception
    {
        // get input stream from bitstream
        // pass to filter, get string back
        try  
        {
            WordTextExtractorFactory factory = new WordTextExtractorFactory();
            TextExtractor e = factory.textExtractor(source);
            String extractedText = e.getText();

            // if verbose flag is set, print out extracted text
            // to STDOUT
            if (MediaFilterManager.isVerbose)
            {
                System.out.println(extractedText);
            }

            // generate an input stream with the extracted text
            byte[] textBytes = extractedText.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(textBytes);

            return bais; // will this work? or will the byte array be out of scope?
        } 
        catch (IOException ioe)
        {
             System.out.println("Invalid Word Format");
             log.error("Error detected - Word File format not recognized: " + ioe.getMessage(), ioe); 
        }
        return null;
    }
}
