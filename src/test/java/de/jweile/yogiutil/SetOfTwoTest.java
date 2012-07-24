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

import junit.framework.TestCase;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class SetOfTwoTest extends TestCase {
    
    public void testIterator() {
        SetOfTwo<String> set = new SetOfTwo<String>("foo", "bar");
        int i = 0;
        for(String s : set) {
            System.out.println(s);
            i++;
        }
        assert(i==2);
    }
    
    public void testEquals() {
        
        SetOfTwo<String> set1 = new SetOfTwo<String>("foo", "bar");
        SetOfTwo<String> set2 = new SetOfTwo<String>("bar", "foo");
        assert(set1.equals(set2));
        
    }
}
