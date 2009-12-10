/*
 * Hapax3
 * Copyright (c) 2007 Doug Coker
 * Copyright (c) 2009 John Pritchard
 * 
 * The MIT License
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lxl.hapax;

import java.nio.CharBuffer;

/**
 * Line number string reader
 *
 * @author jdp
 */
public final class TemplateParserReader
    extends java.lang.Object
    implements java.lang.CharSequence
{

    protected Template source;

    private volatile CharBuffer buffer;

    private volatile int lno = 1;

    private volatile int advance;


    public TemplateParserReader(Template source){
        super();
        if (null != source){
            this.source = source;
            this.buffer = source.getTemplateSourceHapax();
        }
        else
            throw new IllegalArgumentException();
    }

    public Template getSource(){
        return this.source;
    }
    public int lineNumber(){
        return this.lno;
    }
    public int length(){
        CharBuffer buf = this.buffer;
        if (null != buf)
            return buf.remaining();
        else
            return 0;
    }
    public boolean next(){
        if (this.advance < this.length()){
            this.advance += 1;
            return true;
        }
        else
            return false;
    }
    public char charAt(int idx){

        idx += this.advance;

        CharBuffer buf = this.buffer;
        if (null != buf)
            return buf.get(idx);
        else
            throw new IndexOutOfBoundsException(String.valueOf(idx)+":{0}");
    }
    public char charAtTest(int idx){

        idx += this.advance;

        CharBuffer buf = this.buffer;
        if (null != buf)
            return buf.get(idx);
        else
            return 0;
    }
    public int indexOf(String s){
        if (null != s && 0 != s.length()){
            char[] search = s.toCharArray();
            CharBuffer buf = this.buffer;
            if (null != buf){
                int sc = 0;
                int scc = search.length;
                int start = -1;

                int idx = this.advance;
                int idxc = buf.remaining();

                for (; idx < idxc; idx++){

                    if (buf.get(idx) == search[sc++]){

                        if (-1 == start)
                            start = idx;

                        if (sc >= scc)
                            return start;
                        else
                            continue;
                    }
                    else {
                        start = -1;
                        sc = 0;
                        continue;
                    }
                }
            }
            return -1;
        }
        else
            throw new IllegalArgumentException(s);
    }
    /**
     * @param start Offset index, inclusive
     * @param end Offset index, exclusive
     */
    public String delete(int start, int end){
        if (null != this.buffer){
            char[] buf = this.buffer.array();
            int buflen = buf.length;
            if ((-1 < start && start < buflen)&&(start <= end && end <= buflen)){
                if (start == end)
                    return "";
                else {
                    int relen = (end-start);
                    char[] re = new char[relen];
                    System.arraycopy(buf,start,re,0,relen);
                    int nblen = (buflen-relen);
                    if (0 == nblen)
                        this.buffer = null;
                    else {
                        int term = (buflen-1);
                        char[] nb = new char[nblen];
                        if (0 == start){
                            /*
                             * Copy buffer tail to new buffer
                             */
                            System.arraycopy(buf,end,nb,0,nblen);
                            this.buffer = CharBuffer.wrap(nb);
                        }
                        else if (term == end){
                            /*
                             * Copy buffer head to new buffer
                             */
                            System.arraycopy(buf,0,nb,0,nblen);
                            this.buffer = CharBuffer.wrap(nb);
                        }
                        else {
                            /*
                             * Copy buffer head & tail to new buffer
                             */
                            int nbalen = start;
                            int nbblen = buflen-end;
                            System.arraycopy(buf,0,nb,0,nbalen);
                            System.arraycopy(buf,end,nb,nbalen,nbblen);
                            this.buffer = CharBuffer.wrap(nb);
                        }
                    }
                    return this.lines(CharBuffer.wrap(re),0,relen);
                }
            }
            else
                throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{"+buf.length+'}');
        }
        else
            throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{0}");
    }
    /**
     * Copy out, counting lines, init buffer.
     */
    public String truncate(){
        CharBuffer buf = this.buffer;
        this.buffer = null;
        return this.lines(buf,0,-1);
    }
    /**
     * Copy out.
     */
    public CharSequence subSequence(int start, int end){
        CharBuffer buf = this.buffer;
        if (null != buf)
            return buf.subSequence(start,end);
        else
            throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{0}");
    }
    public String toString(){

        CharBuffer buf = this.buffer;
        if (null != buf)
            return buf.toString();
        else
            return "";
    }
    /**
     * Copy out, counting lines, init buffer.
     */
    protected String lines(CharBuffer re, int ofs, int len){

        this.advance = 0;

        if (-1 == len)
            len = ((null != re)?(re.remaining()):(0));

        this.lno += CountLines(re,ofs,len);

        return new String(re.array(),ofs,len);
    }
    /*
     */
    protected static int CountLines(CharSequence rec, int ofs, int len){
        int num = 0;

        for (; ofs < len; ofs++){
            if ('\n' == rec.charAt(ofs))
                num += 1;
        }
        return num;
    }
}
