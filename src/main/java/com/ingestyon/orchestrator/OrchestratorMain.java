package com.ingestyon.orchestrator;

import com.ingestyon.orchestrator.processor.BatchProcessor;
import com.ingestyon.orchestrator.processor.FileProcessor;
import com.ingestyon.orchestrator.scheduler.BatchScheduler;

/**
 * Possible Improvements:
 * 1. Add Logger instead of System.out
 * 2. Scale the threads with number of files to be processed
 * 3. Keep track of overall progress and expose for querying or publish to Kubernates if thats an option
 * 4. Restart Scenario can be more robust
 *      Skip reprocessing lines of the files which have already been processed
 *      Some more testing required to make sure re-queing logic for the half-processed files work
 * 5. USe OR Mapping libraray of some sort like Morphia or springData
 *     Current model of manually persisting is too error prone and very **Frustrating***
 *     The current implementation will only work with MongoDB. Swithich will require rewriting some of persistence logic
 * 6. Rethink dual ID situation. Currently we are using two IDs per entity
 * 7. "command" getting persisted in DB are not trimming the fields. We can do that just not sure
 * 8. DB initialization needs some more thought. I don't like the way its being done right now based on AtomicBoolean.
 *    Works for MongoDB because its idempodent but may not work for other mysql DBs
 * 9. This code will need a bunch of work to be compatible with distributed setup where more than one copy of the code is working against the same database
 * 10. More fine grained exception propagation and handling
 *
 *
 */

public class OrchestratorMain {

    public static void main(String[] args){

       BatchScheduler batchProcessScheduler = new BatchScheduler();
       batchProcessScheduler.schedule(new BatchProcessor(),2*60);

        BatchScheduler fileProcessorScheduler = new BatchScheduler();
        fileProcessorScheduler.schedule(new FileProcessor(),60,2); //TODO: Scale number of threads up and down based on load



    }
}
