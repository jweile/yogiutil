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

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class LazyInitMapTest extends TestCase {
    
    public void test() {
        
        String key = "test";
        
        LazyInitMap<String,IntArrayList> map = new LazyInitMap<String, IntArrayList>(IntArrayList.class);
        
        map.getOrCreate(key).add(1);
        
        assertNotNull(map.get(key));
        
        assertFalse(map.get(key).isEmpty());
        
    }
    
    public void testParameterized() {
        
        String key = "test";
        
//        Class<ArrayList<Integer>> clazz = (Class<ArrayList<Integer>>) (new ArrayList<Integer>().getClass());
        LazyInitMap<String,List<Integer>> map = new LazyInitMap<String, List<Integer>>(ArrayList.class);
        
        map.getOrCreate(key).add(1);
        
        assertNotNull(map.get(key));
        
        assertFalse(map.get(key).isEmpty());
    }
    
}
