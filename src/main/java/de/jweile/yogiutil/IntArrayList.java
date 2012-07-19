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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class IntArrayList extends AbstractList<Integer> implements List<Integer> {

    private int[] array;
    
    private int size = 0;

    public IntArrayList() {
        this(32);
    }
    
    public IntArrayList(int initSize) {
        array = new int[initSize];
    }
    
    public IntArrayList(Collection<Integer> c) {
        this();
        addAll(c);
    }
    
    public IntArrayList(int[] init) {
        this();
        addAll(init);
    }

    @Override
    public Integer get(int i) {
        checkBounds(i);
        return array[i];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Integer set(int i, Integer e) {
        
        checkNull(e);
        checkBounds(i);
        
        int old = array[i];
        array[i] = e;
        
        return old;
    }

    @Override
    public void add(int i, Integer e) {
        
        checkNull(e);
        
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException(i+"");
        }
        
        //extend array if necessary
        if (size == array.length) {
            int[] newArray = Arrays.copyOf(array, array.length+32);
            array = newArray;
        }
        
        //shift elements to the right
        for (int j = array.length-1; j > i; j--) {
            array[j] = array[j-1];
        }
        
        //replace element i
        array[i] = e;
        size++;
        
    }
    
    public void addAll(int[] ints) {
        for (int i : ints) {
            add(i);
        }
    }

    @Override
    public Integer remove(int i) {
        checkBounds(i);
        
        int old = array[i];
        
        //shift elements to the left
        for (int j = i; j < array.length - 1; j++) {
            array[j] = array[j+1];
        }
        size--;
        
        return old;
    }


    private void checkBounds(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException(i+"");
        }
    }
    
    private void checkNull(Integer e) {
        if (e == null) {
            throw new NullPointerException("Cannot add null to list!");
        }
    }

}
