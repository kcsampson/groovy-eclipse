/*
 * Copyright 2011 SpringSource, a division of VMware, Inc
 * 
 * andrew - Initial API and implementation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.eclipse.compiler;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.batch.Main;

/**
 * Simple progress monitor to keep track of number of files compiled.
 * Also, this is the launcher for the JDT compiler
 * 
 * @author Andrew Eisenberg
 * @created Aug 13, 2010
 */
class InternalCompiler extends CompilationProgress {
    
    static class Result {
        final boolean success;
        final int globalErrorsCount;
        final int globalWarningsCount;
        public Result(boolean success, int globalErrorCount, int globalWarningCount) {
            super();
            this.success = success;
            this.globalErrorsCount = globalErrorCount;
            this.globalWarningsCount = globalWarningCount;
        }
    }

    private Logger logger;
    private boolean verbose;

    InternalCompiler(Logger logger,boolean verbose) {
        this.logger = logger;
        this.verbose = verbose;
    }

    private int count = 0;

    public void begin(int remainingWork) {}

    public void done() {
        if (verbose) {
            logger.info("Compilation complete.  Compiled " + count + " files.");
        }
    }

    public boolean isCanceled() {
        return false;
    }

    public void setTaskName(String newTaskName) {}

    public void worked(int workIncrement, int remainingWork) {
        if (verbose) {
            String file = remainingWork == 1 ? "file" : "files";
            logger.info(remainingWork + " " + file + " left.");
            count++;
        }
    }

    static Result doCompile(String[] args, StringWriter out, Logger logger, boolean verbose) {
        InternalCompiler progress = new InternalCompiler(logger, verbose);
        Main main = new Main(new PrintWriter(out), new PrintWriter(out), false/* systemExit */,
                null/* options */, progress);
        boolean result = main.compile(args);
        return new Result(result, main.globalErrorsCount, main.globalWarningsCount);
    }
}