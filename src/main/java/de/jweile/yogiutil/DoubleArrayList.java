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
public class DoubleArrayList extends AbstractList<Double> implements List<Double> {
    
    private double[] array;
    
    private int size = 0;

    public DoubleArrayList() {
        this(32);
    }
    
    public DoubleArrayList(int initSize) {
        array = new double[initSize];
    }
    
    public DoubleArrayList(Collection<Double> c) {
        addAll(c);
    }

    @Override
    public Double get(int i) {
        checkBounds(i);
        return array[i];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Double set(int i, Double e) {
        
        checkNull(e);
        checkBounds(i);
        
        double old = array[i];
        array[i] = e;
        
        return old;
    }

    @Override
    public void add(int i, Double e) {
        
        checkNull(e);
        
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException(i+"");
        }
        
        //extend array if necessary
        if (size == array.length) {
            double[] newArray = Arrays.copyOf(array, array.length+32);
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

    @Override
    public Double remove(int i) {
        checkBounds(i);
        
        double old = array[i];
        
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
    
    private void checkNull(Double e) {
        if (e == null) {
            throw new NullPointerException("Cannot add null to list!");
        }
    }
}
