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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class Counts<T> {
    
    private Map<T,Counter> counters = new HashMap<T,Counter>();
    
    public void resetAll() {
        for (Counter c : counters.values()) {
            c.reset();
        }
    }
    
    public void count(T item) {
        retrieveOrCreate(item).inc();
    }
    
    public void reset(T item) {
        retrieveOrCreate(item).reset();
    }
    
    public Integer getCount(T key) {
        Counter c = counters.get(key);
        if (c != null) {
            return c.getVal();
        } else {
            return null;
        }
    }
    
    public Set<T> getKeys() {
        return Collections.unmodifiableSet(counters.keySet());
    }

    private Counter retrieveOrCreate(T key) {
        Counter c = counters.get(key);
        if (c == null) {
            c = new Counter();
            counters.put(key,c);
        }
        return c;
    }
    
     /**
     * A simple class for counting occurrences.
     */
    private static class Counter {
        int val = 0;
        
        /**
         * increase counter value.
         */
        public void inc() {
            val++;
        }
        
        /**
         * Reset counter to 0.
         */
        public void reset() {
            val = 0;
        }

        /**
         * @return current counter value.
         */
        public int getVal() {
            return val;
        }
        
        
    }
}
