/*
 *  Copyright (C) 2011 Jochen Weile, M.Sc. <j.weile@ncl.ac.uk>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jweile.yogiutil;

/**
 * A progress bar for the commandline. Usage example:
 * 
 * <code><pre>
 * //say you're iterating over a Collection called xs
 * 
 * CliProgressBar pb = new CliProgressBar(xs.size());
 * for (Object x : xs) {
 *    //do something with x
 *    pb.next();
 * }
 * </pre></code>
 * 
 * @author Jochen Weile
 */
public class CliProgressBar {
    
    private final int max;
    private int last = -1;
    private int curr = 0;
    
    /**
     * Creates the progress bar object.
     * @param max Number of steps you're iterating over.
     */
    public CliProgressBar(int max) {
        this.max = max;
    }
    
    /**
     * Indicate the one step in the iteration has been completed. 
     * This will update the progress bar.
     */
    public void next() {
        
        int bars = curr * 70 / max;
        int percent = curr * 100 / max;
        
        if (percent > last) {
            
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < bars; i++) {
                b.append("=");
            }
            for (int i = bars; i < 70; i++) {
                b.append(" ");
            }
            b.append("| ")
             .append(percent)
             .append("%\r");
            
            System.out.print(b.toString());
            
            last = percent;
        }
        
        
        if (++curr == max) {
            System.out.println("done!");
        }
    }
}
