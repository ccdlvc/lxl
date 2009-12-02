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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

/**
 * The template renderer is the MT-SAFE state of a template prepared
 * for rendering.
 *
 * @author dcoker
 * @author jdp
 */
public final class TemplateRenderer 
    extends Object
{
    private final List<TemplateNode> template;
    private final TemplateLoader context;


    public TemplateRenderer(TemplateLoader context, Template source)
        throws TemplateException
    {
        super();
        if (null != context){
            this.context = context;
            if (null != source){
                List<TemplateNode> template = source.getTemplateTargetHapax();
                if (null == template){
                    template = TemplateParser.Parse(source,new lxl.ArrayList<TemplateNode>());
                    source.setTemplateTargetHapax(template);
                }
                this.template = template;
            }
            else
                throw new IllegalArgumentException();
        }
        else
            throw new IllegalArgumentException();
    }


    public void render(TemplateDataDictionary dict, PrintWriter writer)
        throws TemplateException
    {
        try {
            Render(this.context, this.template, dict, writer);
        }
        finally {
            dict.renderComplete();
        }
    }
    public String renderToString(TemplateDataDictionary dict)
        throws TemplateException
    {
        try {
            StringWriter buffer = new StringWriter();

            Render(this.context, this.template, dict, (new PrintWriter(buffer)));

            return buffer.toString();
        }
        finally {
            dict.renderComplete();
        }
    }
    public final static void Render(TemplateLoader context, List<TemplateNode> template, 
                                    TemplateDataDictionary dict, PrintWriter writer)
        throws TemplateException
    {
        Render(context,template,0,(template.size()-1),dict,writer);
    }
    private final static void Render(TemplateLoader context, List<TemplateNode> template, 
                                     int start, int end, TemplateDataDictionary dict, 
                                     PrintWriter writer)
        throws TemplateException
    {
        for (int position = start; position <= end; position++) {

            TemplateNode node = template.get(position);

            switch (node.getNodeType()){

            case SectionOpen:

                position = RenderSection(context, template, dict, position, node, writer);
                break;

            case SectionClose:
                break;

            case Variable:
                position = RenderVariable(context, template, dict, position, node, writer);
                break;

            case Text:
                position = RenderText(context, template, dict, position, node, writer);
                break;

            case Include:
                position = RenderInclude(context, template, dict, position, node, writer);
                break;

            default:
                throw new IllegalStateException("Bug at node "+node.getNodeType()+'['+node.getLineNumber()+",'"+node.getNodeContent()+"']");
            }
        }
    }
    private final static int RenderSection(TemplateLoader context, List<TemplateNode> template,
                                            TemplateDataDictionary dict, int open, TemplateNode node,
                                            PrintWriter writer)
        throws TemplateException
    {
        int next = (open + 1);
        int close = (open + node.getOffsetCloseRelative());
        TemplateName sectionName = new TemplateName(node.getNodeContent());

        if (close >= next && close < template.size()){

            List<TemplateDataDictionary> data = dict.getSection(sectionName);

            if (null != data){

                if (data.size() == 0) {

                    TemplateSpecialIterator.Define(dict,sectionName,0,1);
                    /*
                     * Once
                     */
                    Render(context, template, next, close, dict, writer);
                }
                else {
                    /*
                     * Repeat
                     */
                    for (int cc = 0, count = data.size(); cc < count; cc++){

                        TemplateDataDictionary child = data.get(cc);

                        TemplateSpecialIterator.Define(child,sectionName,cc,count);

                        Render(context, template, next, close, child, writer);
                    }
                }
            }
            return close;
        }
        else
            throw new TemplateException("Missing close tag for section '"+sectionName+"' at line "+node.getLineNumber()+".");
    }
    private final static int RenderVariable(TemplateLoader context, List<TemplateNode> template,
                                            TemplateDataDictionary dict, int pos, TemplateNode node,
                                            PrintWriter writer)
        throws TemplateException
    {
        TemplateName variableName = new TemplateName(node.getNodeContent());
        String variableValue = dict.getVariable(variableName);

        if (null != variableValue && 0 != variableValue.length())
            writer.write(variableValue);

        return pos;
    }
    private final static int RenderText(TemplateLoader context, List<TemplateNode> template,
                                        TemplateDataDictionary dict, int pos, TemplateNode node,
                                        PrintWriter writer)
        throws TemplateException
    {
        String text = node.getNodeContent();

        if (null != text && 0 != text.length())
            writer.write(text);

        return pos;
    }
    private final static int RenderInclude(TemplateLoader context, List<TemplateNode> template,
                                           TemplateDataDictionary dict, int pos, TemplateNode node,
                                           PrintWriter writer)
        throws TemplateException
    {
        TemplateName sectionName = new TemplateName(node.getNodeContent());

        List<TemplateDataDictionary> section = dict.getSection(sectionName);

        if (null != section){

            TemplateName filename = ResolveName(dict,sectionName);

            TemplateRenderer renderer = context.getTemplate(filename);
            if (null != renderer){

                if (section.size() == 0) {

                    TemplateSpecialIterator.Define(dict,sectionName,0,1);
                    /*
                     * Once
                     */
                    renderer.render(dict, writer);
                }
                else {
                    /*
                     * Repeat
                     */
                    for (int cc = 0, count = section.size(); cc < count; cc++){

                        TemplateDataDictionary child = section.get(cc);

                        TemplateSpecialIterator.Define(child,sectionName,cc,count);

                        renderer.render(child, writer);
                    }
                }
            }
        }
        return pos;
    }
    public static TemplateName ResolveName(TemplateDataDictionary dict, TemplateName name)
        throws TemplateException
    {
        String redirect = dict.getVariable(name);

        if (null != redirect && 0 != redirect.length())
            return new TemplateName(redirect);
        else
            return name;
    }
}
