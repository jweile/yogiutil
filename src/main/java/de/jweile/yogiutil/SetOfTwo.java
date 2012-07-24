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

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class SetOfTwo<T> implements Iterable<T> {
    
    private T a, b;

    public SetOfTwo(Comparable<T> a, Comparable<T> b) {
        if (a.compareTo((T)b) <= 0) {
            this.a = (T)a;
            this.b = (T)b;
        } else {
            this.b = (T)a;
            this.a = (T)b;
        }
    }
    
    public SetOfTwo(T a, T b, Comparator<T> comp) {
        if (comp.compare(a, b) <= 0) {
            this.a = a;
            this.b = b;
        } else {
            this.b = a;
            this.a = b;
        }
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SetOfTwo<T> other = (SetOfTwo<T>) obj;
        if (this.a != other.a && (this.a == null || !this.a.equals(other.a))) {
            return false;
        }
        if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.a != null ? this.a.hashCode() : 0);
        hash = 59 * hash + (this.b != null ? this.b.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{")
                .append(a)
                .append(",")
                .append(b)
                .append("}")
                .toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int i = 0;
            
            @Override
            public boolean hasNext() {
                return i < 2;
            }

            @Override
            public T next() {
                switch(i++) {
                    case 0:
                        return a;
                    case 1:
                        return b;
                    default:
                        throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
    
    
    
}
