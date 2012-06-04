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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jweile
 */
public abstract class Node<I,O> implements Runnable {
    
    protected String name;
    
    protected Exchanger<Queue<I>> inExchanger;
    protected Exchanger<Queue<O>> outExchanger;

    public Node(String name) {
        this.name = name;
    }
    
    void setExchangers(Exchanger<Queue<I>> inExchanger, Exchanger<Queue<O>> outExchanger) {
        this.inExchanger = inExchanger;
        this.outExchanger = outExchanger;
    }
    
    protected void before() {
        //for overriding
    }
    
    protected void after() {
        //for overriding
    }

    @Override
    public void run() {
        
        before();
        
        Queue<I> qIn = new LinkedList<I>();
        Queue<O> qOut = new LinkedList<O>();
        
        try {
            
            while (qIn != null) {
                
                assert(qOut != null);
                assert(qOut.isEmpty());
                
                I in;
                while ((in = qIn.poll()) != null) {
                    O out = process(in);
                    qOut.add(out);
                }
                
                qIn = inExchanger.exchange(qIn);
                qOut = outExchanger.exchange(qOut);
                
            }
            
        } catch (InterruptedException e) {
            
            throw new RuntimeException("Interrupted "+name);
            
        } finally {
            
            after();
            
            try {
                outExchanger.exchange(null);//shutdown next node;
            } catch (InterruptedException ex) {
                Logger.getLogger(Node.class.getName())
                        .log(Level.SEVERE, "Shutdown propagation interrupted!", ex);
            }
        }
    }

    public abstract O process(I in);
    
}
