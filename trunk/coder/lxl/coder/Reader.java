/*
 * Syntelos-X
 * Copyright (C) 2009 John Pritchard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package lxl.coder;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * 
 * @see Class
 * @author jdp
 */
public final class Reader
    extends lxl.io.Scanner
{

    private Comment comment;


    public Reader(Readable source){
        super(source);
    }
    public Reader(InputStream source){
        super(source);
    }
    public Reader(InputStream source, Charset cs){
        super(source,cs);
    }
    public Reader(File source)
        throws java.io.FileNotFoundException
    {
        super(source);
    }
    public Reader(File source, Charset cs)
        throws java.io.FileNotFoundException
    {
        super(source,cs);
    }
    public Reader(String source){
        super(source);
    }
    public Reader(ReadableByteChannel source){
        super(source);
    }
    public Reader(ReadableByteChannel source, Charset cs){
        super(source,cs);
    }


    public void comment(Jump jump){
        if (null == this.comment)
            this.comment = jump.comment;
    }
    public Comment comment()
        throws IOException, Syntax
    {
        Comment comment = this.comment;
        if (null != comment)
            this.comment = null;
        else {
            try {
                comment = new Comment(this);
                this.comment = comment;
            }
            catch (Jump to){
            }
        }
        return comment;
    }
    public String getNext(Pattern pattern){
        this.comment = null;
        return super.getNext(pattern);
    }
    public MatchResult getNextResult(Pattern pattern){
        this.comment = null;
        return super.getNextResult(pattern);
    }
}
