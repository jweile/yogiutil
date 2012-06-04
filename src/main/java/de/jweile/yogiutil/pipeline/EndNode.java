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
package de.jweile.yogiutil.pipeline;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Exchanger;

/**
 *
 * @author jweile
 */
public abstract class EndNode<I> extends Node<I,Void> {

    public EndNode(String name) {
        super(name);
    }

    @Override
    public void run() {
        Queue<I> qIn = new LinkedList<I>();
        
        try {
            
            while (qIn != null) {
                
                I in;
                while ((in = qIn.poll()) != null) {
                    process(in);
                }
                
                qIn = inExchanger.exchange(qIn);
                
            }
            
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted "+name);
        }
    }

    
}
