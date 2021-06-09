package com.ingestyon.orchestrator.entity;

import com.ingestyon.orchestrator.utils.Utils;

import java.io.File;
import java.util.Date;
import lombok.Data;


@Data
public class FileBatchLineItemEntity extends BaseEntity {

    public File inputFile;
    public int fileId;
    public String file_path;
    public String configurationID;
    public String processing_status;


    public  FileBatchLineItemEntity(File inputFile, String configurationID) {
        this(Utils.getUniqueID(),
                inputFile.getAbsolutePath().hashCode(),
                inputFile.getAbsolutePath(),
                configurationID,
                null,
                new Date(),
                new Date());

    }

    public FileBatchLineItemEntity (String id, int fileId, String filePath, String configurationID, String processing_status, Date created_at , Date update_at){
        this.id = id;
        this.fileId = fileId;
        this.file_path = filePath;
        this.configurationID = configurationID;
        this.processing_status = processing_status;
        this.created_at = created_at;
        this.updated_at = update_at;
        this.inputFile = new File(filePath);

    }


}
