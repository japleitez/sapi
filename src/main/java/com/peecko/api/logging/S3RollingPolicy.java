package com.peecko.api.logging;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S3RollingPolicy<E> extends TimeBasedRollingPolicy<E> {

    private String s3BucketName;
    private String s3KeyPrefix = "";
    private String awsRegion = "eu-central-1";

    private S3Client s3Client;
    private final ExecutorService uploadExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "log-s3-uploader");
        t.setDaemon(true);
        return t;
    });

    @Override
    public void start() {
        super.start();
        if (s3BucketName != null) {
            s3Client = S3Client.builder().region(Region.of(awsRegion)).build();
        }
    }

    @Override
    public void rollover() throws RolloverFailure {
        String archivedFile = getTimeBasedFileNamingAndTriggeringPolicy()
            .getCurrentPeriodsFileNameWithoutCompressionSuffix();
        super.rollover();
        if (archivedFile != null && s3Client != null) {
            uploadExecutor.submit(() -> upload(archivedFile));
        }
    }

    private void upload(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            addWarn("Archived log file not found for S3 upload: " + filePath);
            return;
        }
        String key = s3KeyPrefix + file.getName();
        try {
            s3Client.putObject(
                PutObjectRequest.builder().bucket(s3BucketName).key(key).build(),
                RequestBody.fromFile(file)
            );
            addInfo("Uploaded log to s3://" + s3BucketName + "/" + key);
        } catch (Exception e) {
            addError("Failed to upload log file to S3: " + filePath, e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (s3Client != null) s3Client.close();
        uploadExecutor.shutdown();
    }

    public void setS3BucketName(String s3BucketName) { this.s3BucketName = s3BucketName; }
    public void setS3KeyPrefix(String s3KeyPrefix) { this.s3KeyPrefix = s3KeyPrefix; }
    public void setAwsRegion(String awsRegion) { this.awsRegion = awsRegion; }
}
