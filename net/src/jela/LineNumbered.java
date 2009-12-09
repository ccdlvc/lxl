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
package lxl.jela;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * 
 * 
 * @author J. Pritchard
 */
public final class LineNumbered
    extends PrintWriter
{

    public LineNumbered(String text){
        super(new CharArrayWriter());
        int newlines = 0;
        StringTokenizer strtok = new StringTokenizer(text,"\n", true);
        for (int lno = 1; strtok.hasMoreTokens(); ){

            String line = strtok.nextToken();
            if ("\n".equals(line)){
                newlines += 1;
                if (1 < newlines){

                    if (lno < 10)
                        this.printf("   %d    \n",lno);
                    else if (lno < 100)
                        this.printf("  %d    \n",lno);
                    else if (lno < 1000)
                        this.printf(" %d    \n",lno);
                    else
                        this.printf("%d    \n",lno);

                    lno++;
                }
            }
            else {
                newlines = 0;

                if (lno < 10)
                    this.printf("   %d    %s\n",lno,line);
                else if (lno < 100)
                    this.printf("  %d    %s\n",lno,line);
                else if (lno < 1000)
                    this.printf(" %d    %s\n",lno,line);
                else
                    this.printf("%d    %s\n",lno,line);

                lno++;
            }
        }
    }

    public String toString(){
        CharArrayWriter out = (CharArrayWriter)this.out;
        return out.toString();        
    }
}
