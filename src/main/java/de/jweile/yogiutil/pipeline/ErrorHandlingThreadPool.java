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
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jweile
 */
class ErrorHandlingThreadPool extends ThreadPoolExecutor {
    
    private volatile List<Throwable> errors = new ArrayList<Throwable>();
    
    public ErrorHandlingThreadPool(int threads) {
        super(
            threads, // core threads
            threads, // max threads
            Long.MAX_VALUE, // timeout
            TimeUnit.MINUTES, // timeout units
            new LinkedBlockingQueue() // work queue
            );
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            errors.add(t);
            shutdownNow();
        }
    }

    public void errors() {
        super.terminated();
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.get(0));
        }
    }


}
