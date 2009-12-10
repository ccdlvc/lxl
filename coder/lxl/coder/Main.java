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

import lxl.hapax.AbstractData;
import lxl.hapax.Template;
import lxl.hapax.TemplateException;
import lxl.hapax.TemplateName;
import lxl.hapax.TemplateRenderer;

import lxl.io.Find;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lxl.List;

/**
 * Batch processor for generating java source files from templates and
 * odl files.
 * 
 * This package will generate the Java source code for a class modeled
 * in {@link lxl.coder.Class}.
 * 
 * This command line tool requires three file arguments in the
 * following positional order: <i>in templ xtm</i>; <i>in model
 * odl</i>; and <i>out source java</i>.
 * 
 * @see Class
 * @author jdp
 */
public class Main
    extends java.lang.Object
{

    /**
     * Generate output from template.
     * 
     * @return Output file
     */
    public final static File ProcessTemplate(TemplateRenderer template, File odl, File src)
        throws IOException, TemplateException, Syntax, ODStateException
    {
        try {
            Class clas = ClassDescriptorFor(odl);

            String parentClassName = OD.ClassName(clas);
            String packageName = OD.PackageName(clas.pack);
            File packagePath = new File(src,packageName.replace('.','/'));

            AbstractData top = new AbstractData();

            top.addBean("class",clas);
            top.addBean("package",clas.pack);
            top.addBeanList("imports",clas.imports);
            top.addBeanList("fields",clas.fields);
            top.addBeanList("methods",clas.methods);
            top.addBeanList("interfaces",clas.interfaces);

            File beanJava = new File(packagePath,parentClassName+".java");
            File beanJavaDir = beanJava.getParentFile();
            if (!beanJavaDir.exists()){
                if (!beanJavaDir.mkdirs())
                    throw new TemplateException("Unable to create directory containing output '"+beanJavaDir.getPath()+"'.");
            }
            PrintWriter out = new PrintWriter(new FileWriter(beanJava));
            try {
                OD.GenerateBeanSource(template, top, out);
                return beanJava;
            }
            finally {
                out.close();
            }
        }
        catch (Syntax syntax){

            throw new Syntax("In '"+odl+"'",syntax);
        }
    }
    /**
     * Run template using directories.
     * 
     * @return List of target products
     */
    public final static List<File> ProcessDirectories(File xtm, File odlDir, File src)
        throws IOException, TemplateException, Syntax, ODStateException
    {
        List<File> products = new lxl.ArrayList<File>();

        SInit(odlDir);

        TemplateRenderer template = new Template(xtm.toURL()).createTemplateRenderer();

        for (File odlFile: Files.values()){
            try {
                File beanJava = Main.ProcessTemplate(template,odlFile,src);

                products.add(beanJava);
            }
            catch (ODStateException ODStateException){
                throw new ODStateException("In '"+odlFile+"'",ODStateException);
            }
        }
        return products;
    }

    /*
     * Batch Cache 
     */
    private final static lxl.Map<String,Class> Classes = new lxl.Map<String,Class>(43);
    private final static lxl.Map<String,File> Files = new lxl.Map<String,File>(43);

    public final static String ClassName(File odl){
        String name = odl.getName();
        int idx = name.lastIndexOf('.');
        if (-1 != idx)
            return name.substring(0,idx);
        else
            return name;
    }
    public final static Class ClassDescriptorFor(File odl)
        throws IOException, Syntax
    {
        String name = ClassName(odl);
        Class desc;
        synchronized(Classes){
            desc = Classes.get(name);
        }
        if (null == desc){
            Reader odlReader = new Reader(odl);
            try {
                desc = (new Class(odlReader));
            }
            finally {
                odlReader.close();
            }
            synchronized(Classes){
                Classes.put(name,desc);
            }
        }
        return desc;
    }
    /**
     * @param name Class base name, typically the big table KIND name.
     */
    public final static Class ClassDescriptorFor(String name)
        throws IOException, Syntax
    {
        Class desc;
        synchronized(Classes){
            desc = Classes.get(name);
        }
        if (null == desc){

            File file = Files.get(name);
            if (null != file){
                Reader odlReader = new Reader(file);
                try {
                    desc = (new Class(odlReader));
                }
                catch (Syntax exc){
                    throw new Syntax("In '"+file+"'.",exc);
                }
                finally {
                    odlReader.close();
                }
                synchronized(Classes){
                    Classes.put(name,desc);
                }
            }
        }
        return desc;
    }

    /*
     *
     */

    public final static void usage(java.io.PrintStream out){
        out.println("Usage");
        out.println();
        out.println("  lxl.coder.Main &lt;template.file&gt; &lt;odl.dir&gt; &lt;src.dir%gt;");
        out.println();
        out.println("Description");
        out.println();
        out.println("     This process is for generating java output in the  ");
        out.println("     framework.  It will generate bean, validation, servlet and list");
        out.println("     types for the input  packages.");
        out.println();
    }

    public final static void main(String[] argv){
        if (3 == argv.length){
            File xtm = new File(argv[0]);
            File odl = new File(argv[1]);
            File src = new File(argv[2]);
            if (xtm.isFile() && odl.isDirectory() && src.isDirectory()){

                int rc = 0;
                try {
                    System.out.println("Template: "+xtm.getPath());
                    System.out.println("Source: "+odl.getPath());
                    System.out.println("Target: "+src.getPath());
                    List<File> products = Main.ProcessDirectories(xtm,odl,src);
                    for (File product : products){
                        System.out.println("Product: "+product.getPath());
                    }
                }
                catch (Throwable thrown){
                    thrown.printStackTrace();
                    rc = 1;
                }
                finally {
                    System.exit(rc);
                }
            }
            else {
                if (!xtm.isFile())
                    System.err.println("Error, file not found: '"+xtm.getPath()+"'.");

                if (!odl.isDirectory())
                    System.err.println("Error, directory not found: '"+odl.getPath()+"'.");

                if (!src.isDirectory())
                    System.err.println("Error, directory not found: '"+src.getPath()+"'.");
            }
        }
        else
            usage(System.err);

        System.exit(1);
        return;
    }

    public final static class FileFilter
        extends Object
        implements java.io.FileFilter
    {

        public FileFilter(){
            super();
        }

        public boolean accept(File file){
            if (file.isFile())
                return (file.getName().endsWith(".odl"));
            else
                return (!file.getName().equals(".svn"));
        }
    }
    public final static FileFilter FilesFilt = new FileFilter();

    public final static class FindClass
        extends Object
        implements java.io.FileFilter
    {
        private final String name;

        public FindClass(String name){
            super();
            this.name = name+".odl";
        }

        public boolean accept(File file){
            if (file.isFile())
                return (this.name.equals(file.getName()));
            else
                return (!file.getName().equals(".svn"));
        }
    }


    public static void SInit(File odlDir){
        Find find = new Find(FilesFilt,odlDir);
        while (find.hasNext()){
            File odl = find.next();
            String name = ClassName(odl);
            if (null == Files.get(name))
                Files.put(name,odl);
        }
    }
}
