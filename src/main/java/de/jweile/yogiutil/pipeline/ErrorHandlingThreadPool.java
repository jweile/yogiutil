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
 * A thread pool that is able to handle exceptions occurring during execution
 * of any of its members.
 * 
 * @author Jochen Weile <jochenweile@gmail.com>
 */
class ErrorHandlingThreadPool extends ThreadPoolExecutor {
    
    /**
     * A list of the errors that have occurred.
     */
    private volatile List<Throwable> errors = new ArrayList<Throwable>();
    
    /**
     * Constructor.
     * 
     * @param threads 
     * The number of threads in the pool.
     */
    public ErrorHandlingThreadPool(int threads) {
        super(
            threads, // core threads
            threads, // max threads
            Long.MAX_VALUE, // timeout
            TimeUnit.MINUTES, // timeout units
            new LinkedBlockingQueue() // work queue
            );
    }
    
    /**
     * <p>Method invoked upon completion of execution of the given Runnable. 
     * This method is invoked by the thread that executed the task. 
     * If non-null, the Throwable is the uncaught RuntimeException or 
     * Error that caused execution to terminate abruptly.</p>
     * <p>Note: When actions are enclosed in tasks (such as FutureTask) either explicitly 
     * or via methods such as submit, these task objects catch and maintain 
     * computational exceptions, and so they do not cause abrupt termination, 
     * and the internal exceptions are not passed to this method.</p>
     * <p>This implementation does nothing, but may be customized in subclasses. 
     * Note: To properly nest multiple overridings, subclasses should generally 
     * invoke super.afterExecute at the beginning of this method.</p>
     * 
     * @param r
     * The runnable that has completed its execution.
     * 
     * @param t 
     * The throwable that has caused the runnable to terminate, or null if none.
     * 
     * @see java.util.concurrent.ThreadPoolExecutor.afterExecute(Runnable r, Throwable t)
     */
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

    /**
     * throw the first error that occurred in the thread pool into the current thread,
     * wrapped in a runtime exception.
     */
    public void errors() {
        super.terminated();
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.get(0));
        }
    }


}
