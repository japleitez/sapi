package com.peecko.api.domain.context;

import lombok.Data;

@Data
public class EmailContext {
    String from;
    String to;
    String subject;
    String text;
    String attachmentPath;
}
