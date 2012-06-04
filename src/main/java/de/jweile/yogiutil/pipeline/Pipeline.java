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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jweile
 */
public class Pipeline {
    
    private List<Node> nodeList = new ArrayList<Node>();
    
    private Exchanger<Queue<?>> lastEx = null;
    
    public <I,O> void addNode(Node<I,O> n) {
        if (nodeList.isEmpty()) {
            if (!(n instanceof StartNode)) {
                throw new RuntimeException("Pipeline must start with StartNode!");
            }
        }
        
        //FIXME: Should check that I is the same as the previous O.
        //But I don't currently see a way due to type erasure.
               
        Exchanger<Queue<O>> outEx = (n instanceof EndNode) ? null : new Exchanger<Queue<O>>();
        n.setExchangers((Exchanger<Queue<I>>)(Object)lastEx, outEx);
        lastEx = (Exchanger<Queue<?>>)(Object)outEx;
        nodeList.add(n);
    }
    
    public void start() {
        
        if (nodeList.isEmpty()) {
            throw new RuntimeException("Pipeline is empty!");
        }
        if (!(nodeList.get(nodeList.size() -1) instanceof EndNode)) {
            throw new RuntimeException("Pipeline has no EndNode!");
        }
        
        ErrorHandlingThreadPool pool = new ErrorHandlingThreadPool(nodeList.size());
        for (Node<?,?> node : nodeList) {
            pool.submit(node);
        }
        pool.shutdown();
        
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Pipeline.class.getName())
                    .log(Level.SEVERE, "Interrupted!", ex);
        }
        pool.errors();
    }
    
}
