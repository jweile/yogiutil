/*
 * Copyright (C) 2012 Department of Molecular Genetics, University of Toronto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jweile.yogiutil;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * A hash map capable of lazy initialization via the getOrCreate() method.
 * 
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class LazyInitMap<K,V> extends HashMap<K,V> {
    
    private Constructor<?> constructor;
    
    public LazyInitMap(Class<?> vClass) {
        try {
            constructor = vClass.getConstructor();
            constructor.newInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException(vClass.getName() + " cannot be instantiated!",ex);
        }
    }
    
    public V getOrCreate(K key) {
        V val = get(key);
        if (val == null) {
            try {
                val = (V) constructor.newInstance();
                put(key, val);
            } catch (Exception e) {
                throw new RuntimeException("Lazy init failed!",e);
            }
        }
        return val;
    }
    
}
