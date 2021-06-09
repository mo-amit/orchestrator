package com.ingestyon.orchestrator.entity;


import java.util.Date;
import lombok.Data;

@Data

public class ConfigurationEntity extends BaseEntity{


    private String  processing_status;
    private String process; // type of process ingest
    private final String FILE_LOCATION = "/ingestion";



    public ConfigurationEntity(String id, String processing_status, String process, Date created_at, Date updated_at) {
        this.id = id;
        this.processing_status  = processing_status;
        this.process = process;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }


    public String getFileLocation() {
            return FILE_LOCATION;
    }









}
