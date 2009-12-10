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
package lxl.net;

/**
 * OS name and version info.
 * 
 * @author jdp
 */
public final class Os {

    public final static String Darwin = "Mac OS X";
    public final static String Windows = "Windows";
    public final static String Linux = "Linux";
    public final static String SunOS = "SunOS";

    public final static String Name = System.getProperty("os.name");

    public final static int VersionMajor, VersionMinor;
    static {

        String string = System.getProperty("os.version");

        java.util.StringTokenizer strtok = new java.util.StringTokenizer(string,"._- \t");
        String major = strtok.nextToken();
        VersionMajor = Integer.parseInt(major);
        String minor = strtok.nextToken();
        VersionMinor = Integer.parseInt(minor);
    }
    public final static String Version = String.valueOf(VersionMajor)+'.'+String.valueOf(VersionMinor);

    public final static String Arch = System.getProperty("os.arch");

    public final static String Full = Name+' '+Version+' '+Arch;

    public final static boolean IsDarwin = (Os.Darwin.equals(Name));
    public final static boolean IsWindows = (Name.startsWith(Os.Windows));
    public final static boolean IsLinux = (Os.Linux.equals(Name));
    public final static boolean IsSunOS = (Os.SunOS.equals(Name));

    public final static int DARWIN = 1;
    public final static int WINDOWS = 2;
    public final static int LINUX = 3;
    public final static int SUNOS = 4;

    public final static int THIS;
    static {
        if (IsDarwin)
            THIS = DARWIN;
        else if (IsWindows)
            THIS = WINDOWS;
        else if (IsLinux)
            THIS = LINUX;
        else if (IsSunOS)
            THIS = SUNOS;
        else
            THIS = 0;
    }

    public static boolean Is(String os, String arch){
        switch (THIS){
        case DARWIN:
            return (os.equals(Name) && arch.equals(Arch));
        case WINDOWS:
            return (os.startsWith(Os.Windows) && arch.equals(Arch));
        case LINUX:
        case SUNOS:
        default:
            return (os.equals(Name) && arch.equals(Arch));
        }
    }
    public static boolean Is(String os){
        switch (THIS){
        case DARWIN:
            return (os.equals(Name));
        case WINDOWS:
            return (os.startsWith(Os.Windows));
        case LINUX:
        case SUNOS:
        default:
            return (os.equals(Name));
        }
    }
}
