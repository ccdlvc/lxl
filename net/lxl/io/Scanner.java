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
package lxl.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A scanner that processes patterns verbatum, doesn't process
 * whitespace delimiters under the covers.
 * 
 * @see Reader
 * @author jdp
 */
public class Scanner
    extends Object
{
    public final static Charset UTF8 = Charset.forName("UTF-8");

    private final static int BUF_CAP = 0x200;


    private Readable source;

    private CharBuffer buffer;

    private boolean open;

    private int next;


    public Scanner(Readable source){
        super();
        if (null != source){
            this.source = source;
            this.buffer = CharBuffer.allocate(BUF_CAP);
            this.buffer.limit(0);
            this.open = true;
            this.read();
        }
        else
            throw new IllegalArgumentException();
    }
    public Scanner(InputStream source){
        this(new InputStreamReader(source,UTF8));
    }
    public Scanner(InputStream source, Charset cs){
        this(new InputStreamReader(source,cs));
    }
    public Scanner(File source)
        throws java.io.FileNotFoundException
    {
        this(source,UTF8);
    }
    public Scanner(File source, Charset cs)
        throws java.io.FileNotFoundException
    {
        this(Channels.newReader((new FileInputStream(source).getChannel()),cs.newDecoder(),BUF_CAP));
    }
    public Scanner(String source){
        this(new StringReader(source));
    }
    public Scanner(ReadableByteChannel source){
        this(Channels.newReader(source,UTF8.newDecoder(),BUF_CAP));
    }
    public Scanner(ReadableByteChannel source, Charset cs){
        this(Channels.newReader(source,cs.newDecoder(),BUF_CAP));
    }


    public void close(){
        if (this.open){
            this.open = false;
            if (this.source instanceof Closeable)
                try {
                    ((Closeable)this.source).close();
                }
                catch (IOException ignore){
                }
        }
    }
    public String getNext(Pattern pattern){

        MatchResult match = this.getNextResult(pattern);
        if (null != match)
            return match.group();
        else
            return null;
    }
    public MatchResult getNextResult(Pattern pattern){

        CharBuffer buf = this.buffer;

        int next = this.next;
        if (0 != next){
            this.next = 0;
            /*
             * Change buffer state after previous match result has
             * been consumed.
             */
            buf.position(next);
            buf.compact().flip();
        }

        Matcher matcher;
        do {
            matcher = pattern.matcher(buf);

            if (matcher.lookingAt()){

                if (matcher.hitEnd()){

                    buf = this.read();

                    if (null == buf)
                        return null;
                }
                else {
                    this.next = matcher.end();

                    return matcher;
                }
            }
            else if (matcher.hitEnd()){

                buf = this.read();

                if (null == buf)
                    return null;
            }
            else {
                return null;
            }
        }
        while (true);
    }

    private CharBuffer read(){

        CharBuffer buf = this.buffer;

        if (this.open){

            if (buf.limit() == buf.capacity()){
                CharBuffer copier = CharBuffer.allocate(buf.capacity()+BUF_CAP);
                copier.put(buf);
                copier.flip();
                buf = copier;
                this.buffer = buf;
            }
            /*
             * flip to tail for fill
             */
            int tail = buf.limit();
            buf.position(tail);
            buf.limit(buf.capacity());

            int read;
            try {
                int bufRemaining = buf.remaining(); 
                /*
                 * fill
                 */
                if (-1 == (read = source.read(buf)))
                    this.close();
            }
            catch (IOException ioe) {

                read = -1;

                this.close();
            }
            if (-1 != read){
                /*
                 * flip to head for consumer
                 */
                buf.limit(read);
                buf.position(0);
            }
            return buf;
        }
        else
            return null;
    }


    public static void main(String[] argv){
        if (1 < argv.length){
            File in = new File(argv[0]);
            if (in.isFile()){
                try {
                    Scanner scanner = new Scanner(in);
                    try {
                        for (int argi = 1, argc = argv.length; argi < argc; argi++){
                            String re = argv[argi];
                            Pattern p = Pattern.compile(re);
                            String s = scanner.getNext(p);
                            while (null != s){
                                System.out.println(re+" >>> "+s);

                                s = scanner.getNext(p);
                            }
                        }
                    }
                    finally {
                        scanner.close();
                    }
                }
                catch (Exception exc){
                    exc.printStackTrace();
                    System.exit(1);
                }
            }
            else {
                System.err.println("File not found '"+in.getPath()+"'.");
                System.exit(1);
            }
        }
        else {
            System.err.println("Usage:           Scanner <file.in>{1} <re>{1,N} ");
            System.err.println();
            System.err.println("Description:             Test scanner.");
            System.err.println();
            System.exit(1);
        }
    }
}
