package com.ingestyon.orchestrator.entity;


import java.util.Date;
import lombok.Data;

@Data

public class FileLineItemEntity extends BaseEntity{

    public String process_run_id; // BatchID
    public String command;

    public FileLineItemEntity(String id, String process_run_id, String command, Date created_at , Date updated_at) {
        this.id = id;
        this.process_run_id = process_run_id;
        this.command = command;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
