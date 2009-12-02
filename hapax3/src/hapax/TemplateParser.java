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

import lxl.List;

import static lxl.hapax.TemplateNodeType.*;

import java.text.MessageFormat;

/**
 * This parser turns strings containing the contents of a template into a list
 * of TemplateNodes.
 * 
 * @author dcoker
 * @author jdp
 */
public final class TemplateParser
    extends Object
{

    public static List<TemplateNode> Parse(Template source, List<TemplateNode> list)
        throws TemplateParserException
    {
        if (source.hasTemplateSourceHapax()){
            list.clear();
            TemplateParserReader input = new TemplateParserReader(source);
            TemplateNode node = null;
            TemplateNodeType nodeType;
            while (true) {
                switch (nodeType = Next(input)) {
                case SectionOpen:
                    node = ParseOpenSection(nodeType,input);
                    break;
                case SectionClose:
                    node = ParseCloseSection(nodeType,input);
                    break;
                case Variable:
                    node = ParseVariable(nodeType,input);
                    break;
                case Text:
                    node = ParseTextNode(nodeType,input);
                    break;
                case Include:
                    node = ParseInclude(nodeType,input);
                    break;
                case Comment:
                    node = ParseComment(nodeType,input);
                    break;
                case EOF:
                    ParserClose(nodeType,list);
                    return list;
                default:
                    throw new TemplateParserException("Internal error parsing template.");
                }
                if (null != node)
                    list.add(node);
            }
        }
        return list;
    }


    private static TemplateNodeType Next(TemplateParserReader input) {

        int inlen = input.length();
        switch (inlen){
        case 0:
            return EOF;
        case 1:
        case 2:
        case 3:
        case 4:
            return Text;
        default:
            if ('{' == input.charAt(0) && '{' == input.charAt(1)){

                switch (input.charAt(2)){
                case '{':
                    do {
                        input.next();
                    }
                    while ('{' == input.charAtTest(2));

                    return Text;
                case '#':
                    return SectionOpen;
                case '/':
                    return SectionClose;
                case '>':
                    return Include;
                case '=':
                    return Variable;
                case '!':
                    return Comment;
                default:
                    return Variable;
                }
            }
            return Text;
        }
    }

    /**
     * Terminal scan
     */
    private static void ParserClose(TemplateNodeType nodeType, List<TemplateNode> template)
        throws TemplateParserException
    {
        for (int cc = 0, count = template.size(); cc < count; cc++){

            TemplateNode node = template.get(cc);

            node.setOffset(cc);

            switch (node.getNodeType()){

            case SectionOpen:

                node.setOffsetCloseRelative(DistanceToClose(template,cc,node));

                break;

            default:
                break;
            }
        }
    }

    private static TemplateNode ParseTextNode(TemplateNodeType nodeType, TemplateParserReader input) {
        int lno = input.lineNumber();
        int next_braces = input.indexOf("{{");
        if (next_braces == -1) {

            String text = input.truncate();

            return (new TemplateNode(nodeType,lno,text));
        }
        else {
            String text = input.delete(0, next_braces);
            if (text.length() > 0)
                return (new TemplateNode(nodeType,lno,text));
            else
                return null;
        }
    }

    private static TemplateNode ParseInclude(TemplateNodeType nodeType, TemplateParserReader input)
        throws TemplateParserException
    {
        int lno = input.lineNumber();
        String consumed = ParseClose(input);
        String token = consumed.substring(3,consumed.length()-2).trim();
        return (new TemplateNode(nodeType,lno,token));
    }
    private static TemplateNode ParseVariable(TemplateNodeType nodeType, TemplateParserReader input)
        throws TemplateParserException
    {
        int lno = input.lineNumber();
        String token;
        String consumed = ParseClose(input);
        if ('=' == consumed.charAt(2))
            token = consumed.substring(3,consumed.length()-2).trim();
        else
            token = consumed.substring(2,consumed.length()-2).trim();
        return (new TemplateNode(nodeType,lno,token));
    }
    private static TemplateNode ParseCloseSection(TemplateNodeType nodeType, TemplateParserReader input)
        throws TemplateParserException
    {
        int lno = input.lineNumber();
        String consumed = ParseClose(input);
        String token = consumed.substring(3,consumed.length()-2).trim();
        return (new TemplateNode(nodeType,lno,token));
    }
    private static TemplateNode ParseOpenSection(TemplateNodeType nodeType, TemplateParserReader input)
        throws TemplateParserException
    {
        int lno = input.lineNumber();
        String consumed = ParseClose(input);
        String token = consumed.substring(3,consumed.length()-2).trim();
        return (new TemplateNode(nodeType,lno,token));
    }
    private static TemplateNode ParseComment(TemplateNodeType nodeType, TemplateParserReader input)
        throws TemplateParserException
    {
        int lno = input.lineNumber();
        String consumed = ParseClose(input);
        String token = consumed.substring(3,consumed.length()-2).trim();
        return (new TemplateNode(nodeType,lno,token));
    }

    private static String ParseClose(TemplateParserReader input)
        throws TemplateParserException
    {
        int close_braces = input.indexOf("}}");
        if (-1 == close_braces)
            throw new TemplateParserException("Unexpected or malformed input: " + input+" at "+input.lineNumber());
        else {
            int end = close_braces+2;
            return input.delete(0, end);
        }
    }

    private final static int DistanceToClose(List<TemplateNode> template, int node_ofs, TemplateNode node)
        throws TemplateParserException
    {
        int stack = DistanceToCloseStackInit, ofs = (node_ofs+1), length = template.size();
        String sectionName = node.getNodeContent();
        for (; ofs < length; ofs++) {

            TemplateNode tp = template.get(ofs);

            switch (tp.getNodeType()){
            case SectionOpen:
                stack += 1;
                break;
            case SectionClose:
                if (DistanceToCloseStackInit == stack){

                    String tpName = tp.getNodeContent();

                    if (tpName.equals(sectionName))

                        return (ofs-node_ofs);

                    else {

                        String msg = MessageFormat.format("Mismatched close tag: expecting a close tag for \"{0}\", but got close tag for \"{1}\" at line {2}.", 
                                                          sectionName,
                                                          tpName,
                                                          tp.getLineNumber());
                        throw new TemplateParserException(msg);
                    }
                }
                else
                    stack--;
                break;
            default:
                break;
            }
        }
        return -1;
    }

    private final static int DistanceToCloseStackInit = 0;
}
