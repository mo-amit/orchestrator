package com.ingestyon.orchestrator.processor;

import com.ingestyon.orchestrator.entity.FileBatchLineItemEntity;
import com.ingestyon.orchestrator.entity.FileLineItemEntity;
import com.ingestyon.orchestrator.repository.FileBatchRepository;
import com.ingestyon.orchestrator.repository.FileRepository;
import com.ingestyon.orchestrator.utils.Utils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;


import java.io.*;
import java.util.Date;
import java.util.Iterator;

public class FileProcessor extends BaseProcessor{



    public static final char FILE_SEPARATOR = '|';
    public static final int  NUM_RECORD_LINE=3;



    @Override
    public void run() {

        FileBatchRepository fileBatchRepository = new FileBatchRepository();
        FileBatchLineItemEntity fileBatchLineItemEntity = fileBatchRepository.pickOne();
        if(fileBatchLineItemEntity == null){
            System.out.println("Nothing to process..");
            return;
        }
        System.out.println("Processing file" + fileBatchLineItemEntity.inputFile.getAbsolutePath());
        fileBatchRepository.updateStatus(fileBatchLineItemEntity,IN_PROGRESS_PROCESSING_STATUS);
        String processingStatus = processFile(fileBatchLineItemEntity,fileBatchLineItemEntity.id);
        fileBatchRepository.updateStatus(fileBatchLineItemEntity,processingStatus);
    }

    private  String processFile(FileBatchLineItemEntity inFileBatchLineItemEntity, String inPprocess_run_id)  {
        File localFile = inFileBatchLineItemEntity.inputFile;
        FileRepository fileRepository = new FileRepository();
        String idPrefix = new StringBuilder().append(localFile.getAbsolutePath().hashCode()).toString().intern();
        CSVParser parser = new CSVParserBuilder().withSeparator(FILE_SEPARATOR).build();
        FileReader fileReader = null;
        try{
                    fileReader = new FileReader(localFile);
        } catch (FileNotFoundException e) {

                    return FAILURE_PROCESSING_STATUS;
        }
        CSVReader reader = new CSVReaderBuilder(fileReader).withSkipLines(1).withCSVParser(parser).build();
        String [] nextLine;
        Iterator<String[]> iter = reader.iterator();
        String perLineStatus = null;
        String command = null;
        int errorCount = 0;

        for ( int lineCount = 0; iter.hasNext(); lineCount++) {
            nextLine = iter.next();
            perLineStatus = "";
            if( skipLine(inFileBatchLineItemEntity,lineCount)){ // placeholder for now
                continue;
            }
            if (nextLine.length != NUM_RECORD_LINE) {
                perLineStatus += String.format("InValid recordCount {0}",nextLine.length);
                errorCount++;
            } else {
                if (!Utils.isURLValid(nextLine[0])) {
                    perLineStatus += "InValid col1 ";
                    errorCount++;
                }
                if (!Utils.isDateValid(nextLine[1])) {
                    perLineStatus += "InValid col2 ";
                    errorCount++;
                }
                if (!Utils.isRestMethodValid(nextLine[2])) {
                    perLineStatus += "InValid col3 ";
                    errorCount++;
                }
            }

            if (perLineStatus.length() == 0)
                command = String.format("Insert \"%s\" , \"%s\" , \"%s\" ", nextLine[0], nextLine[1], nextLine[2]);
            else
                command = perLineStatus;

            FileLineItemEntity entity = new FileLineItemEntity(idPrefix +"_"+lineCount, inPprocess_run_id, command, new Date(), new Date());
            fileRepository.persist(entity);
        }

        localFile.delete(); // Delete the file when done
        if ( errorCount != 0 )
            return COMPLETED_WITH_ERROR;

        return SUCCESS_PROCESSING_STATUS;

    }

    boolean skipLine(FileBatchLineItemEntity inFileBatchLineItemEntity, int lineCount){
        // TODO: Check DB if the file has been processed based on special ID and skip processing this line
        return false;
    }

//    public static void main(String [] args){
//        new FileProcessor().processFile(new FileBatchLineItemEntity(new File("/tmp/orchestrator/good_ingestion.txt"),null),null);
//    }


}
