/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.obs.marveleditor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

import com.github.tcking.giraffecompressor.GiraffeCompressor;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class OptiCommonMethods {

    private static String tagName = OptiCommonMethods.class.getSimpleName();

    //write intent data into file
    public static File writeIntoFile(Context context, Intent data, File file) {

        AssetFileDescriptor videoAsset = null;
        try {
            videoAsset = context.getContentResolver().openAssetFileDescriptor(data.getData(), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream in;
        try {
            in = videoAsset.createInputStream();

            OutputStream out = null;
            out = new FileOutputStream(file);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    //copy file from one source file to destination file
    public static void copyFile(File sourceFile, File destFile, Context context) throws IOException {
        compressFile(sourceFile, destFile, context);
      /*  if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }*/
    }

    //get video duration in seconds
    public static long convertDurationInSec(long duration) {
        return (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    //get video duration in minutes
    public static long convertDurationInMin(long duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        Log.v(tagName, "min: " + minutes);

        if (minutes > 0) {
            return minutes;
        } else {
            return 0;
        }
    }

    //get video duration in minutes & seconds
    public static String convertDuration(long duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        Log.v(tagName, "min: " + minutes);

        if (minutes > 0) {
            return minutes + "";
        } else {
            return "00:" + (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
    }

    //get video duration based on uri
    public static int getMediaDuration(Context context, Uri uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(context, uriOfFile);
        return mp.getDuration();
    }

    //get file extension based on file path
    public static String getFileExtension(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf("."));
        Log.v(tagName, "extension: " + extension);
        return extension;
    }

    public static void compressFile(File inputFile, File outputFile, Context context){
        GiraffeCompressor.create() //two implementations: mediacodec and ffmpeg,default is mediacodec
                .input(inputFile) //set video to be compressed
                .output(outputFile) //set compressed video output
                .bitRate(2073600)//set bitrate 码率
                .resizeFactor(1.0f)//set video resize factor 分辨率缩放,默认保持原分辨率
                .ready()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GiraffeCompressor.Result>() {
                    @Override
                    public void onCompleted() {
                        Log.e("1","start compress");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e("2","start compress");
                        Log.e("3","error:"+e.getMessage());
                    }

                    @Override
                    public void onNext(GiraffeCompressor.Result s) {
                        String msg = String.format("compress completed \ntake time:%s \nout put file:%s", s.getCostTime(), s.getOutput());

                        msg = msg + "\ninput file size:"+ Formatter.formatFileSize(context,inputFile.length());
                        msg = msg + "\nout file size:"+ Formatter.formatFileSize(context,new File(s.getOutput()).length());
                        System.out.println(msg);
                        Log.e("4",msg);
                    }
                });
    }
}
