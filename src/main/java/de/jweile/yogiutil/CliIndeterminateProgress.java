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
package de.jweile.yogiutil;

/**
 *
 * @author Jochen Weile <jochenweile@gmail.com>
 */
public class CliIndeterminateProgress {
    
    private int lastLength = 0;
    
    int processed = 0;
    
    private long startTime = System.currentTimeMillis();
    
    private long lastTime = System.currentTimeMillis();
    
    public void next(String msg) {
        
        processed++;
        
        if (System.currentTimeMillis() - lastTime > 1000) {
            double secondsElapsed = (double)(System.currentTimeMillis() - startTime) / 1000.0;
            double rate = (double)processed / secondsElapsed;

            int overwrite = lastLength-msg.length()+2;
            if (overwrite < 0) {
                overwrite = 0;
            }

            System.out.print("\r");
            System.out.print(msg);
            System.out.print(String.format(" - Rate: %.2f Hz",rate));

            for (int j = 0; j < overwrite; j++) {
                System.out.print(" ");
            }
            lastLength = msg.length();
            lastTime = System.currentTimeMillis();
        }
    }
    
    public void done() {
        System.out.println();
    }
}
