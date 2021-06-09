package com.ingestyon.orchestrator;

import com.ingestyon.orchestrator.processor.BatchProcessor;
import com.ingestyon.orchestrator.processor.FileProcessor;
import com.ingestyon.orchestrator.scheduler.BatchScheduler;

/**
 * Following may not be obvious
 *
 * 1. Please refer to "resources/sample_config.json" for ideas about some of the other operations we can support include
 *    A. Join
 *    B. Aggegations
 *    C. Computed columns ( col3 = col1+col2)
 *
 *    Full Disclaimer: I copied sample_config.json from one of our old projects which allowed configurable and extensible
 *                     data pipelines over hadoop. Its a bit old but the ideas are very relevant to any data pipeline
 *
 * 2. The system is designed to scale with workload by add more processing threads for processing files.
 *
 * 3. The code follows more of a server model vs Utility model, i.e.
 *    A. A thread scans the directory every few minutes and processes the files as they come in.
 *       Handover is managed via Database
 *    B. I have added some simple recovery code to deal with the scenario when the system dies in the middle of processing a file
 *        a. Files are re-queued for processing
 *        b. There is an reproducible ( file.absolutepath.hashcode+_+line_number) which uniuely identifies a line
 *           and hence allows for the possiblity that those lines can be skipped when the files are reprocessed
 *
 *
 *
 * Known Improvement possibilities  :
 * 1. Add Logger instead of System.out
 * 2. Scale the threads with number of files to be processed
 * 3. Keep track of overall progress and expose for querying or publish to Kubernates if thats an option
 * 4. Restart Scenario can be more robust
 *      Skip reprocessing lines of the files which have already been processed
 *      Some more testing required to make sure re-queuing logic for the half-processed files work
 * 5. We should definitely use O-R Mapping library of some sort. With Mongo there are many options like Morphia or springData etc
 *     a. Current model of manually persisting is too error prone and very **Frustrating***
 *     b. Current implementation will only work with MongoDB. Switching will require rewriting some of persistence logic
 * 6. Rethink DUAL ID situation. Currently we are using two IDs per entity. Our own ID and Mongo's recordID.
 *     a. I didn't want to rely on Mongo Record ID because
 *         1. We have special logic associated with file line id.
 *         2. Mongo IDs are stored as special object type and might have some unforeseen consequences.
 * 7. "command" getting persisted in DB ( process_run_insert) are not trimming the fields. Its easy to do just not done.
 * 8. DB initialization needs some more thought. I don't like the way its being done right now based on AtomicBoolean.
 *    Works for MongoDB because its idempotent but may not work for other  DBs
 * 9. This code will need a bunch of work to be compatible with distributed setup where more than one copy of the code is working against the same database
 * 10. A bit more fine grained exception propagation and handling needs to be implemented
 * 12  We should make the file parsing logic pluggable so it would be possible to change it based on file type or some other heuristic
 *
 * Production Considerations:
 *
 * 1. We definitely need a better system to keep track of status / error per line. The solution will likely involve
 *    keeping tack of error information vs status of every line in file which got processed correctly.
 * 2. We should not delete the processed files
 *
 *
 * Crazy Idea
 *
 * Not sure how close the files provided are to the real production files but if the production files are like the samples included
 * I could not help but notice the repetitive nature of text in the files. I mean there can be 100K unique URLs, 10 Method types and timestamp
 * which is by definition unique but only the  minute part. If we get 100B events a day thats 69Million entries per minute. This data distribution
 * closely resembles Google style web-index. A long tail of data points ( web pages for Google) associated with relatively fewer Keywords.
 * Depending on the use cases we should explore the idea of in-memory reverse keyword index to enable some of the use cases related to
 * responding to user actions. There are many off-the-shelf options available which integrate with modern data stack if we do decide to explore
 *
 * Like I said Crazy Idea :-)
 *
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
