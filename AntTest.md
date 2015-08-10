> # Build & Test #

> Run
```
 ant
```
> for unit tests on [Index](Index.md), [Map](Map.md) and [Coder](Coder.md).

> # last seen result: error #

```
      Running test
      java jar ./test-coder-1.0.3.jar
                      coder/test/templates/test.Main.xtm
                      coder/test/odl
                      coder/test/src
    
Template: coder/test/templates/test.Main.xtm
Source: coder/test/odl
Target: coder/test/src
lxl.coder.Syntax: In 'coder/test/odl/A.odl'
        at lxl.coder.Main.ProcessTemplate(Main.java:89)
        at lxl.coder.Main.ProcessDirectories(Main.java:108)
        at lxl.coder.Main.main(Main.java:216)
Caused by: lxl.coder.Syntax: Package statement not found.
        at lxl.coder.Package.<init>(Package.java:59)
        at lxl.coder.Class.<init>(Class.java:177)
        at lxl.coder.Main.ClassDescriptorFor(Main.java:144)
        at lxl.coder.Main.ProcessTemplate(Main.java:62)
        ... 2 more
Java Result: 1

```


> [\*ant](http://ant.apache.org/)