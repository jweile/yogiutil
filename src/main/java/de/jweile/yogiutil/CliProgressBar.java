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

import java.util.concurrent.TimeUnit;

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
    
    private long startTime;
    
    /**
     * Creates the progress bar object.
     * @param max Number of steps you're iterating over.
     */
    public CliProgressBar(int max) {
        this.max = max;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Indicate that one step in the iteration has been completed. 
     * This will update the progress bar.
     */
    public void next() {
        
        curr++;
        
        int bars = curr * 70 / max;
        int percent = curr * 100 / max;
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        if (percent > last) {
            
            String etaString = "?";
            if (curr > 0) {
                double perUnitOfWork = (double)elapsed / (double)curr;
                long eta = (long)((double)(max - curr) * perUnitOfWork);
                etaString = time(eta);
            }
            
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < bars; i++) {
                b.append("=");
            }
            for (int i = bars; i < 70; i++) {
                b.append(" ");
            }
            b.append("| ")
             .append(percent)
             .append("% ETA: ")
             .append(etaString)
             .append("                 \r");
            
            System.out.print(b.toString());
            
            last = percent;
        }
        
        
        if (curr == max) {
            System.out.println("\nDone! Elapsed: "+time(elapsed));
        }
    }
    
    /**
     * Express the given number of milliseconds as a string that looks like:
     * "1d, 2h, 12m, 3s"
     * @param t a number of milliseconds
     * @return the string as described above.
     */
    private String time(long t) {
        StringBuilder b = new StringBuilder();
        
        for (Unit unit : Unit.getUnits()) {
            long uValue = t / unit.inMillis();
            if (uValue > 0) {
                b.append(uValue).append(unit.getLabel()).append(", ");
            }
            t = t % unit.inMillis();
        }
        
        //remove last comma
        if (b.length() >= 2) {
            b.delete(b.length()-2, b.length());
        }
        
        if (b.length() == 0) {
            b.append("due");
        }
        
        return b.toString();
    }
    
    /**
     * Time units
     */
    private static enum Unit {
        
        MILLI(1,"ms"), 
        SEC(1000*MILLI.ms,"s"), 
        MIN(60*SEC.ms,"m"), 
        HOUR(60*MIN.ms,"h"), 
        DAY(24*HOUR.ms,"d"), 
        WEEK(7*DAY.ms,"wk");
        
        private long ms;
        private String label;

        private Unit(long conversion, String label) {
            this.ms = conversion;
            this.label = label;
        }
        
        /**
         * returns all time units.
         * @return all time units.
         */
        public static Unit[] getUnits() {
            return new Unit[]{WEEK, DAY, HOUR, MIN, SEC};
        }

        /**
         * the duration of this unit in milliseconds
         * @return duration of this unit in milliseconds
         */
        private long inMillis() {
            return ms;
        }

        /**
         * The common label for this unit. (e.g. "s" for second)
         * @return 
         */
        public String getLabel() {
            return label;
        }
        
        
    }
}
