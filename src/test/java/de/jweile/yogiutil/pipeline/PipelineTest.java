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

import junit.framework.TestCase;

/**
 *
 * @author jweile
 */
public class PipelineTest extends TestCase {
    
    public void testPipeline() {
        
        Pipeline p = new Pipeline();
        
        p.addNode(new StartNode<String>("n1") {

            int i = 0;
            String[] list = {"a","b","c","d","e","f","g"};
            
            @Override
            public String process(Void v) {
                if (i < list.length) {
                    return list[i++];
                } else {
                    return null;
                }
            }
        });
        
        p.addNode(new Node<String,String>("n2") {

            @Override
            public String process(String in) {
                return in;
            }
            
        });
        
        p.addNode(new EndNode<String>("n3") {

            @Override
            public Void process(String in) {
                System.out.println(in);
                return null;
            }
            
        });
        
        p.start();
        
    }
    
    public void testPipelineErrorHandling() {
        
        Pipeline p = new Pipeline();
        
        p.addNode(new StartNode<String>("n1") {

            int i = 0;
            String[] list = {"a","b","c","d","e","f","g"};
            
            @Override
            public String process(Void v) {
                if (i < list.length) {
                    return list[i++];
                } else {
                    return null;
                }
            }
        });
        
        p.addNode(new Node<String,String>("n2") {

            @Override
            public String process(String in) {
                if (in.equals("c")) {
                    throw new RuntimeException("TestError");
                }
                return in;
            }
            
        });
        
        p.addNode(new EndNode<String>("n3") {

            @Override
            public Void process(String in) {
                System.out.println(in);
                return null;
            }
            
        });
        
        RuntimeException rex = null;
        try {
            p.start();
        } catch (RuntimeException e) {
            rex = e;
        }
        
        assertNotNull("Pipeline failed to report error!",rex);
        
    }
    
//    public void testPipelineConstructionError() {
//        
//        Pipeline p = new Pipeline();
//        
//        p.addNode(new StartNode<String>("n1") {
//
//            int i = 0;
//            String[] list = {"a","b","c","d","e","f","g"};
//            
//            @Override
//            public String process(Void v) {
//                if (i < list.length) {
//                    return list[i++];
//                } else {
//                    return null;
//                }
//            }
//        });
//        
//        p.addNode(new Node<String,int[]>("n2") {
//
//            @Override
//            public int[] process(String in) {
//                return new int[0];
//            }
//            
//        });
//        
//        ClassCastException expectedException = null;
//        try {
//            p.addNode(new EndNode<String>("n3") {
//
//                @Override
//                public Void process(String in) {
//                    System.out.println(in);
//                    return null;
//                }
//
//            });
//        } catch (ClassCastException e) {
//            expectedException = e;
//        }
//        
//        assertNotNull("Incompatible node not detected!",expectedException);
//    }
    
    
}
