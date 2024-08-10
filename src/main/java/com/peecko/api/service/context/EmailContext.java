package com.peecko.api.service.context;

import lombok.Data;

@Data
public class EmailContext {
    String from;
    String to;
    String subject;
    String text;
    String attachmentPath;
}
