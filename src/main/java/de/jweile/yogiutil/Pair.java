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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A pair of two things of type T.
 * 
 * @author jweile
 */
public class Pair<T> implements Iterable<T> {
    private T a,b;

    public Pair(T a, T b) {
        this.a = a;
        this.b = b;
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
        final Pair<T> other = (Pair<T>) obj;
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
        hash = 61 * hash + (this.a != null ? this.a.hashCode() : 0);
        hash = 61 * hash + (this.b != null ? this.b.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder()
                .append("(")
                .append(a.toString())
                .append(",")
                .append(b.toString())
                .append(")");
        return out.toString();
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
