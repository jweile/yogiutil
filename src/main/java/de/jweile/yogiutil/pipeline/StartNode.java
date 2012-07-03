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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>The first node in the pipeline. Produces output objects of type <code>O</code>. </p>
 * 
 * <p>When implementing the <code>process()</code> method, make sure that when the 
 * pipeline has reached its end, return <b><code>null</code></b> to signal all 
 * subsequent nodes to shut down.</p>
 * 
 * <p>Example:</p>
<code><pre>new StartNode&lt;String&gt;("n1") {

    int i = 0;
    String[] list = {"a","b","c","d","e","f","g"};

    public String process(Void v) {
        if (i &lt; list.length) {
            return list[i++];
        } else {
            return null;
        }
    }
}
</pre></code>
 * 
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public abstract class StartNode<O> extends Node<Void,O> {

    /**
     * The number of object appended to the queue before it is passed on to the 
     * next node.
     */
    public static final int QUEUE_LENGTH = 10;
    
    /**
     * Constructor.
     * @param name The node name.
     */
    public StartNode(String name) {
        super(name);
    }
    
    /**
     * Executed by the pipeline executor.
     */
    @Override
    public void run() {
        
        before();
        
        Queue<O> qOut = new LinkedList<O>();
        boolean more = true;
        
        try {
            
            while (more) {
                
                assert(qOut != null);
                assert(qOut.isEmpty());
                
                for (int i = 0; i < QUEUE_LENGTH && more; i++) {
                    O out = process(null);
                    if (out != null) {
                        qOut.add(out);
                    } else {
                        more = false;
                    }
                }
                
                qOut = outExchanger.exchange(qOut);
                
            }
            
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted "+name);
        } finally {
            
            try {
                //shutdown next node
                Logger.getLogger(StartNode.class.getName())
                        .info(name + " initiating shutdown.");
                outExchanger.exchange(null);
            } catch (InterruptedException ex) {
                Logger.getLogger(StartNode.class.getName())
                        .log(Level.SEVERE, "Shutdown propagation interrupted!", ex);
            } finally {
                after();
            }
        }
    }
}
