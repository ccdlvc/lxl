/*
 * Copyright (c) 2009 John Pritchard, JBXML Project Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lxl.beans;

/**
 * @author jdp
 */
public class Relative
    extends Number
{
    public enum By {
        pc,
        pt,
        px,
        em;

        public final static By decode(String suffix){
            if (null != suffix && 0 < suffix.length()){
                switch (suffix.charAt(0)){
                case '%':
                    return pc;
                case 'e':
                    switch (suffix.charAt(1)){
                    case 'm':
                        return em;
                    default:
                        throw new IllegalArgumentException(suffix);
                    }
                case 'p':
                    switch (suffix.charAt(1)){
                    case 't':
                        return pt;
                    case 'x':
                        return px;
                    default:
                        throw new IllegalArgumentException(suffix);
                    }
                default:
                    throw new IllegalArgumentException(suffix);
                }
            }
            else
                throw new IllegalArgumentException(suffix);
        }
        public final static String encode(By by){
            if (null == by)
                return null;
            else {
                switch (by){
                case pc:
                    return "%";
                case pt:
                    return "pt";
                case px:
                    return "px";
                case em:
                    return "em";
                default:
                    throw new IllegalStateException(by.toString());
                }
            }
        }
    }


    protected final Component owner;

    protected final String property;

    protected float base;

    protected By by;


    public Relative(Component owner, String property){
        super();
        if (null != owner && null != property){
            this.owner = owner;
            this.property = property;
        }
        else
            throw new IllegalArgumentException();
    }
    public Relative(Component owner, String property, String s){
        this(owner,property);
        if (null != s)
            this.fromString(s);
    }


    public Component getBean(){
        return this.owner;
    }
    public String getName(){
        return this.property;
    }
    public float floatValue(){
        if (By.px == this.by)
            return this.base;
        else
            throw new UnsupportedOperationException();
    }
    public float floatValue(Number parentValue){
        if (By.pc == this.by){
            if (null != parentValue){

                float pvf = ((Number)parentValue).floatValue();

                return ((this.base/100f) * pvf);
            }
            else
                throw new IllegalArgumentException("Parent value of '"+this.property+"' is null.");
        }
        else if (By.px == this.by)
            return this.base;
        else
            throw new UnsupportedOperationException();
    }
    public int intValue(){
        return (int)this.floatValue();
    }
    public int intValue(Number parentValue){
        return (int)this.floatValue(parentValue);
    }
    public long longValue(){
        return this.intValue();
    }
    public long longValue(Number parentValue){
        return this.intValue(parentValue);
    }
    public double doubleValue(){
        return this.floatValue();
    }
    public double doubleValue(Number parentValue){
        return this.floatValue(parentValue);
    }
    

    public float getBase(){
        return this.base;
    }
    public void setBase(float base){
        this.base = base;
    }
    public void setBase(String base){
        if (null != base)
            this.base = Float.parseFloat(base);
        else
            throw new IllegalArgumentException();
    }
    public boolean byPercent(){
        return (By.pc == this.by);
    }
    public boolean byPoint(){
        return (By.pt == this.by);
    }
    public boolean byPixel(){
        return (By.px == this.by);
    }
    public boolean byEm(){
        return (By.em == this.by);
    }
    public boolean isByPercent(){
        return (By.pc == this.by);
    }
    public boolean isByPoint(){
        return (By.pt == this.by);
    }
    public boolean isByPixel(){
        return (By.px == this.by);
    }
    public boolean isByEm(){
        return (By.em == this.by);
    }
    public By getBy(){
        return this.by;
    }
    public void setBy(By by){
        if (null != by)
            this.by = by;
        else
            throw new IllegalArgumentException();
    }
    public void setBy(String by){
        if (null != by)
            this.by = By.decode(by);
        else
            throw new IllegalArgumentException();
    }
    public void fromString(String value){
        if (null != value){
            for (int cc = (value.length()-1); cc > -1; cc--){
                switch (value.charAt(cc)){
                case '%':
                case 'e':
                case 'm':
                case 'p':
                case 't':
                case 'x':
                    break;
                default:
                    cc += 1;
                    String base = value.substring(0,cc);
                    this.base = Float.parseFloat(base);
                    String suffix = value.substring(cc);
                    this.by = By.decode(suffix);
                    return;
                }
            }
        }
        throw new IllegalArgumentException(value);
    }
    public String toString(){
        String base = String.valueOf(this.base);
        String suff = By.encode(this.by);
        if (null != suff)
            return (base+suff);
        else
            return base;
    }
}
