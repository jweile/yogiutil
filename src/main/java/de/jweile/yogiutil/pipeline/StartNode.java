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
 *
 * @author jweile
 */
public abstract class StartNode<O> extends Node<Void,O> {

    public static final int QUEUE_LENGTH = 10;
    
    public StartNode(String name) {
        super(name);
    }
    
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
            }
            
            after();
        }
    }
}
