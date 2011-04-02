/*
 * Copyright (c) 2008 VMware, Inc.
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * Java reflection as bean properties via the {@link lxl.Dictionary}
 * interface.  
 * 
 * The "bean" convention represents as bean property named 'foo' via
 * the methods 'getFoo' and 'setFoo' on the Java object class.  The
 * type of 'foo' is the type of the value returned by 'getFoo'.
 *
 * @author gbrown
 * @author jdp
 */
public class Reflector
    extends Object
    implements lxl.Dictionary<String, Object>
{

    public static final String GET_PREFIX = "get";
    public static final String IS_PREFIX = "is";
    public static final String SET_PREFIX = "set";


    private class PropertyIteratorKeys
        implements Iterator<String>,
                   Iterable<String>
    {
        private Method[] methods = null;

        int i = 0;
        private String nextProperty = null;

        public PropertyIteratorKeys() {
            Class<?> type = bean.getClass();
            methods = type.getMethods();
            nextProperty();
        }

        public boolean hasNext() {
            return (nextProperty != null);
        }

        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String nextProperty = this.nextProperty;
            nextProperty();

            return nextProperty;
        }

        private void nextProperty() {
            nextProperty = null;

            while (i < methods.length
                && nextProperty == null) {
                Method method = methods[i++];

                if (method.getParameterTypes().length == 0
                    && (method.getModifiers() & Modifier.STATIC) == 0) {
                    String methodName = method.getName();

                    String prefix = null;
                    if (methodName.startsWith(GET_PREFIX)) {
                        prefix = GET_PREFIX;
                    } else {
                        if (methodName.startsWith(IS_PREFIX)) {
                            prefix = IS_PREFIX;
                        }
                    }

                    if (prefix != null) {
                        int propertyOffset = prefix.length();
                        nextProperty = Character.toLowerCase(methodName.charAt(propertyOffset))
                            + methodName.substring(propertyOffset + 1);
                    }

                }
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator<String> iterator(){
            return this;
        }
    }

    private class PropertyIteratorValues
        implements Iterator<Object>,
                   Iterable<Object>
    {
        private Method[] methods = null;

        int i = 0;
        private Object nextProperty = null;

        public PropertyIteratorValues() {
            Class<?> type = bean.getClass();
            methods = type.getMethods();
            nextProperty();
        }

        public boolean hasNext() {
            return (nextProperty != null);
        }

        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Object nextProperty = this.nextProperty;
            nextProperty();

            return nextProperty;
        }

        private void nextProperty() {
            nextProperty = null;

            while (i < methods.length
                && nextProperty == null) {
                Method method = methods[i++];

                if (method.getParameterTypes().length == 0
                    && (method.getModifiers() & Modifier.STATIC) == 0) {
                    String methodName = method.getName();

                    boolean getter = false;
                    if (methodName.startsWith(GET_PREFIX)) {
                        getter = true;
                    } else {
                        if (methodName.startsWith(IS_PREFIX)) {
                            getter = true;
                        }
                    }

                    if (getter) {
                        try {
                            nextProperty = method.invoke(bean);
                        }
                        catch (Exception ignore){

                            nextProperty = null;
                        }
                    }
                }
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator<Object> iterator(){
            return this;
        }
    }

    /**
     * Upcase the first character in the input string, downcase the
     * remainder of the input string.
     */
    public final static java.lang.String Camel00(java.lang.String name){
        if (null == name)
            return "";
        else {
            int len = name.length();
            if (1 > len)
                return "";
            else if (1 == len)
                return name.toUpperCase();
            else
                return (Character.toUpperCase(name.charAt(0))+name.substring(1).toLowerCase());
        }
    }
    public final static java.lang.String Camel0(java.lang.String name){
        String[] strtok = name.split(".");
        int count = strtok.length;
        if (2 > count)
            return Camel00(name);
        else {
            java.lang.StringBuilder strbuf = new java.lang.StringBuilder();
            for (int cc = 0; cc < count; cc++){
                java.lang.String tok = strtok[cc];
                tok = Camel00(tok);
                if (0 < cc)
                    strbuf.append('.');
                strbuf.append(tok);
            }
            return strbuf.toString();
        }
    }
    /**
     * Convert any string into Class Name "camel" case.  The first
     * character is upcased, and characters following hyphen are
     * upcased and the hyphen dropped.
     */
    public final static java.lang.String ClassCamel(java.lang.String name){
        String[] strtok = name.split("-");
        int count = strtok.length;
        if (2 > count)
            return Camel0(name);
        else {
            java.lang.StringBuilder strbuf = new java.lang.StringBuilder();
            for (int cc = 0; cc < count; cc++){
                java.lang.String tok = strtok[cc];
                tok = Camel0(tok);
                strbuf.append(tok);
            }
            return strbuf.toString();
        }
    }
    /**
     * Convert any string into Field Name "camel" case.  Characters
     * following hyphen are upcased and the hyphen is dropped.
     */
    public final static java.lang.String FieldCamel(java.lang.String name){
        String[] strtok = name.split("-");
        int count = strtok.length;
        if (2 > count)
            return name;
        else {
            java.lang.StringBuilder strbuf = new java.lang.StringBuilder();
            for (int cc = 0; cc < count; cc++){
                java.lang.String tok = strtok[cc];
                if (0 < cc)
                    tok = Camel0(tok);
                strbuf.append(tok);
            }
            return strbuf.toString();
        }
    }


    protected Object bean;

    public final boolean isComponent;

    protected Map<String,Relative> relatives;


    public Reflector(Object bean) {
        super();
        if (null != bean){
            this.bean = bean;
            this.isComponent = (bean instanceof Component);
        }
        else
            throw new IllegalArgumentException();
    }


    public lxl.Dictionary<String,Object> cloneDictionary(){
        try {
            Reflector clone = (Reflector)super.clone();
            return clone;
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }
    public void destroy(){
        this.bean = null;
        Map<String,Relative> relatives = this.relatives;
        if (null != relatives){
            this.relatives = null;
            relatives.clear();
        }
    }
    public boolean hasRelative(String name){
        Map<String,Relative> relatives = this.relatives;
        if (null == relatives)
            return false;
        else
            return (null != relatives.get(name));
    }
    public Number getRelativeValue(String name, Number value){
        Relative relative = this.getRelative(name);
        if (null != relative){
            try {
                return relative.floatValue(value);
            }
            catch (RuntimeException x2){
            }
        }
        return null;
    }
    public Relative getRelative(String name){
        Map<String,Relative> relatives = this.relatives;
        if (null == relatives)
            return null;
        else
            return relatives.get(name);
    }
    public Relative setRelative(String name, String value){
        Map<String,Relative> relatives = this.relatives;
        if (null == relatives){
            relatives = new HashMap<String,Relative>();
            this.relatives = relatives;
        }
        Relative rel = relatives.get(name);
        if (null != rel){
            if (null != value)
                rel.fromString(value);
            return rel;
        }
        else if (this.bean instanceof Component){
            rel = this.newRelative( (Component)this.bean, name, value);
            relatives.put(name,rel);
            return rel;
        }
        else
            return null;
    }

    protected Relative newRelative(Component bean, String name, String value){

        return (new Relative(bean, name, value));
    }

    public void dropRelative(String name){
        Map<String,Relative> relatives = this.relatives;
        if (null != relatives)
            relatives.remove(name);
    }
    /**
     * @return May be null
     */
    public Iterator<String> relatives(){
        Map<String,Relative> relatives = this.relatives;
        if (null != relatives)
            return relatives.keySet().iterator();
        else
            return null;
    }

    public Object getBean() {
    	return bean;
    }
    public Object get(Object key) {
        if (null != key){
            Method getterMethod = this.getGetterMethod( (String)key);
            if (getterMethod != null) {
                try {
                    return getterMethod.invoke(bean, new Object[] {});
                }
                catch (IllegalAccessException exception) {
                    return null;
                }
                catch(InvocationTargetException exception) {
                }
            }
            return null;
        }
        else
            throw new IllegalArgumentException();
    }
    public Object put(String key, Object value) {
        if (null != key) {

            Class lvalueType = null, rvalueType = ((null != value)?(value.getClass()):(null));

            Method getterMethod = this.getGetterMethod(key);

            if (null != getterMethod){
                lvalueType = getterMethod.getReturnType();
            }

            if (null == lvalueType){
                rvalueType = lvalueType;
            }

            if (null == lvalueType && null == rvalueType)
                throw new PropertyNotFoundException("Unable to determine property type for \"" + key + "\" on bean \""+bean.getClass().getName()+"\".  Define a getter method or avoid setting null.");
            else {
                Method setterMethod = this.getSetterMethod(key, rvalueType);
                if (setterMethod == null){
                    setterMethod = this.getSetterMethod(key, lvalueType);
                    if (null == setterMethod)
                        throw new PropertyNotFoundException("Setter method for property \"" +lvalueType.getName() +' '+ key + "\" not found on bean \""+bean.getClass().getName()+"\".");
                    else {
                        if (lvalueType.isAssignableFrom(rvalueType)){
                            try {
                                setterMethod.invoke(bean, new Object[] {value});
                            }
                            catch(IllegalAccessException exception) {
                                throw new IllegalArgumentException(exception);
                            } 
                            catch(InvocationTargetException exc) {
                
                                Throwable cause = exc.getCause();
                                if (this.isComponent && value instanceof String && cause instanceof NumberFormatException){
                                    try {
                                        Relative rel = this.setRelative(key,(String)value);
                                        if (rel.byPixel()){
                                            this.dropRelative(key);
                                            try {
                                                setterMethod.invoke(bean, new Object[] {rel.floatValue()});
                                            }
                                            catch(IllegalAccessException exc2){
                                            } 
                                            catch(InvocationTargetException exc2){
                                            }
                                        }
                                    }
                                    catch (RuntimeException not){
                                    }
                                }
                                throw new IllegalArgumentException(cause);
                            }
                        }
                        else if (lxl.List.class.isAssignableFrom(lvalueType)){
                            lxl.List list = (lxl.List)this.get(key);
                            if (null == list){
                                list = new lxl.ArrayList();
                                try {
                                    setterMethod.invoke(bean, new Object[] {list});
                                }
                                catch(IllegalAccessException exc){
                                    throw new IllegalArgumentException(exc);
                                } 
                                catch(InvocationTargetException exc){
                                    Throwable cause = exc.getCause();
                                    throw new IllegalArgumentException(cause);
                                }
                            }
                            list.add(value);
                            return list;
                        }
                        else 
                            throw new PropertyNotFoundException("Setter method for property \"" + key + "\" in class \""+rvalueType.getName()+"\" not found on bean \""+bean.getClass().getName()+"\".");
                    }
                }
                else {
                    try {
                        setterMethod.invoke(bean, new Object[] {value});
                    }
                    catch(IllegalAccessException exception) {
                        throw new IllegalArgumentException(exception);
                    } 
                    catch(InvocationTargetException exc) {
                
                        Throwable cause = exc.getCause();
                        if (this.isComponent && value instanceof String && cause instanceof NumberFormatException){
                            try {
                                Relative rel = this.setRelative(key,(String)value);
                                if (rel.byPixel()){
                                    this.dropRelative(key);
                                    try {
                                        setterMethod.invoke(bean, new Object[] {rel.floatValue()});
                                    }
                                    catch(IllegalAccessException exc2){
                                    } 
                                    catch(InvocationTargetException exc2){
                                    }
                                }
                            }
                            catch (RuntimeException not){
                            }
                        }
                        throw new IllegalArgumentException(cause);
                    }
                }
            }
            return null;
        }
        else
            throw new IllegalArgumentException();
    }
    public boolean isReadOnly(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        return (getSetterMethod(key, getType(key)) == null);
    }
    public Class<?> getType(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        Method getterMethod = getGetterMethod(key);

        return (getterMethod == null) ? null : getterMethod.getReturnType();
    }
    private Method getGetterMethod(String key) {
        Class<?> type = bean.getClass();

        key = ClassCamel(key);

        Method method = null;
        try {
            method = type.getMethod(GET_PREFIX + key, new Class<?>[] {});
        }
        catch(NoSuchMethodException exception) {
        }

        if (method == null) {
            try {
                method = type.getMethod(IS_PREFIX + key, new Class<?>[] {});
            }
            catch(NoSuchMethodException exception) {
            }
        }

        return method;
    }
    private Method getSetterMethod(String key, Class<?> valueType) {
        Class<?> type = this.bean.getClass();
        Method method = null;

        if (valueType != null) {

            final String methodName = SET_PREFIX + ClassCamel(key);

            try {
                method = type.getMethod(methodName, new Class<?>[] {valueType});
            }
            catch(NoSuchMethodException exception) {

            }

            if (method == null) {
                Class<?> superType = valueType.getSuperclass();
                method = getSetterMethod(key, superType);
            }

            if (method == null) {
                try {
                    Field primitiveTypeField = valueType.getField("TYPE");
                    Class<?> primitiveValueType = (Class<?>)primitiveTypeField.get(this);

                    try {
                        method = type.getMethod(methodName, new Class<?>[] {primitiveValueType});
                    }
                    catch(NoSuchMethodException exception) {
                    }
                }
                catch(NoSuchFieldException exception) {
                }
                catch(IllegalAccessException exception) {
                }
            }

            if (method == null) {

                Class<?>[] interfaces = valueType.getInterfaces();

                int i = 0, n = interfaces.length;
                while (method == null && i < n) {
                    Class<?> interfaceType = interfaces[i++];
                    method = getSetterMethod(key, interfaceType);
                }
            }
        }

        return method;
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }

        return (this.getGetterMethod( (String)key) != null);
    }
    public boolean isEmpty() {
        return false;
    }
    public Iterator<String> iteratorKeys() {
        return new PropertyIteratorKeys();
    }
    public lxl.Set<String> keySet() {
        return new lxl.Set( new PropertyIteratorKeys());
    }
    public Iterator<Object> iteratorValues() {
        return new PropertyIteratorValues();
    }
    public Iterable<String> keys() {
        return new PropertyIteratorKeys();
    }
    public Iterable<Object> values() {
        return new PropertyIteratorValues();
    }

    public void applyAttributes(Resolver io, lxl.Dictionary<String, ?> attributes){
        java.util.Iterator<String> keys = attributes.iteratorKeys();
        while (keys.hasNext()){
            String name = keys.next();

            Class type = this.getType(name);
            if (null != type){
                Object value = attributes.get(name);

                if (value instanceof String){
                    value = io.resolve( (String)value, type);
                }

                if (null != value)
                    try {
                        this.put(name,value);
                    }
                    catch (PropertyNotFoundException exc){
                    }
            }
        }
    }
}
