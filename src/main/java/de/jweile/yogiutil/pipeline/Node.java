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
 * A node in the pipeline. Receives input objects of type <code>I</code> and processes them
 * to produce output objects of type <code>O</code>. 
 * 
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public abstract class Node<I,O> implements Runnable {
    
    /**
     * The node's name.
     */
    protected String name;
    
    /**
     * The incoming data exchanger
     */
    protected Exchanger<Queue<I>> inExchanger;
    
    /**
     * The outgoing data exchanger.
     */
    protected Exchanger<Queue<O>> outExchanger;

    /**
     * Constructor
     * @param name The name of the node.
     */
    public Node(String name) {
        this.name = name;
    }
    
    /**
     * internal method for setting the input/output exchangers
     * @param inExchanger the input
     * @param outExchanger the output
     */
    void setExchangers(Exchanger<Queue<I>> inExchanger, Exchanger<Queue<O>> outExchanger) {
        this.inExchanger = inExchanger;
        this.outExchanger = outExchanger;
    }
    
    /**
     * Can be overridden, but doesn't have to be. Will be executed before anything
     * else happens in the thread.
     */
    protected void before() {
        //for overriding
    }
    
    /**
     * Can be overridden, but doesn't have to be. Will be executed when the node
     * shuts down, even if an error has occurred earlier.
     */
    protected void after() {
        //for overriding
    }

    /**
     * Runs the thread.
     */
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
            
            try {
                Logger.getLogger(Node.class.getName())
                        .info(name+" shutting down and propagating.");
                outExchanger.exchange(null);//shutdown next node;
            } catch (InterruptedException ex) {
                Logger.getLogger(Node.class.getName())
                        .log(Level.SEVERE, "Shutdown propagation interrupted!", ex);
            } finally {
                after();
            }
        }
    }

    /**
     * The actual processing method. Receives input of type I and produces output
     * of type O.
     * @param in
     * An input object.
     * @return 
     * An output object.
     */
    public abstract O process(I in);
    
}
