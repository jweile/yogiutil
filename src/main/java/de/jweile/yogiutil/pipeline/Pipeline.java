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
 * <p>A pipeline that consists of a chain nodes. Al nodes run in parallel. Each node 
 * receives chunks of data from its predecessor, processes them and passes them on
 * to its successor. Simply add nodes to the pipeline and call the start method.
 * Each pipeline must start with a <code>StartNode</code> and end with an 
 * <code>EndNode</code>.</p>
 * 
 * <p>How to use the pipeline API:</p>
 * <code><pre>Pipeline p = new Pipeline();
        
p.addNode(new StartNode&lt;String&gt;("n1") {

    int i = 0;
    String[] list = {"a","b","c","d","e","f","g"};

    public String process(Void v) {
        if (i &lt; list.length) {
            return list[i++];
        } else {
            return null;
        }
    }
});

p.addNode(new Node&lt;String,String&gt;("n2") {

    public String process(String in) {
        return in;
    }

});

p.addNode(new EndNode&lt;String&gt;("n3") {

    public Void process(String in) {
        System.out.println(in);
        return null;
    }

});

p.start();</pre></code>
 * 
 * 
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class Pipeline {
    
    /**
     * the list of nodes
     */
    private List<Node> nodeList = new ArrayList<Node>();
    
    /**
     * The exchanger exposed by the last node that was added.
     */
    private Exchanger<Queue<?>> lastEx = null;
    
    /**
     * Adds a node to the pipeline.
     * 
     * @param <I>
     * The input data type.
     * 
     * @param <O>
     * The output data type.
     * 
     * @param n 
     * The node to add.
     */
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
    
    /**
     * Activate the pipeline.
     */
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
