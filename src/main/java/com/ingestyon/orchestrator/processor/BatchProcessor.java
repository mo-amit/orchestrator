package com.ingestyon.orchestrator.processor;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.ingestyon.orchestrator.exception.BaseException;
import com.ingestyon.orchestrator.entity.FileBatchLineItemEntity;
import com.ingestyon.orchestrator.entity.ConfigurationEntity;
import com.ingestyon.orchestrator.repository.FileBatchRepository;
import com.ingestyon.orchestrator.repository.ConfigurationRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;



//TODO: Print line remove
public class BatchProcessor extends BaseProcessor {



    @Override
    public void run() {

        ConfigurationRepository configurationRepository = new ConfigurationRepository();
        ConfigurationEntity configurationEntity = (ConfigurationEntity)configurationRepository.findFirst();
        if (configurationEntity == null){
            System.out.println("No configuration found");
            return;
        }
        String root = configurationEntity.getFileLocation();
        File rootDir = new File(root);
        if( !rootDir.isDirectory() || !rootDir.exists() || !rootDir.canRead()) {
            configurationRepository.updateStatus(configurationEntity,FAILURE_PROCESSING_STATUS);
            throw new BaseException("Bad configuration parameter ");
        }

        FileBatchRepository fileBatchRepository = new FileBatchRepository();
        File rootFolder = new File(root);
        FluentIterable<File> iterator = Files.fileTreeTraverser().preOrderTraversal(rootFolder);
        System.out.println("Found " + iterator.size() + " files to process");
        int queueCount= 0;
        HashSet<Integer> filesAlreadyProcessedSet = getFilesAlreadyProcessedSet(fileBatchRepository);
        for ( File file: iterator){
            if(rootFolder.getAbsolutePath().equals(file.getAbsolutePath()))
                continue; // skip root folder
            FileBatchLineItemEntity fileBatchLineItemEntity = new FileBatchLineItemEntity(file,configurationEntity.id);
            if ( filesAlreadyProcessedSet.contains(fileBatchLineItemEntity.fileId)){
                fileBatchLineItemEntity.processing_status = DUPLICATE;
                fileBatchRepository.persist(fileBatchLineItemEntity);
                fileBatchLineItemEntity.inputFile.delete();// delete the file
                continue;
            }
            fileBatchRepository.persist(fileBatchLineItemEntity);
            queueCount++;

        }
        System.out.println("Queued " + queueCount + " files to process");
        configurationRepository.updateStatus(configurationEntity,SUCCESS_PROCESSING_STATUS);


    }

    private HashSet<Integer> getFilesAlreadyProcessedSet(FileBatchRepository inFileBatchRepository) {
        HashSet<Integer> filesAlreadyProcessedSet = new HashSet<>();
        ArrayList<FileBatchLineItemEntity> alreadyProcessedList = inFileBatchRepository.findAll();
        for (FileBatchLineItemEntity entity : alreadyProcessedList) {
            filesAlreadyProcessedSet.add(entity.fileId);
        }
        return filesAlreadyProcessedSet;
    }
}
