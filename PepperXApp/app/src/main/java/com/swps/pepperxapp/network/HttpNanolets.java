/*
 *  Copyright (C) 2019 SmartLife Robotics, Poland
 *  See COPYING for the license
 */

package com.swps.pepperxapp.network;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.swps.pepperxapp.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;


public class HttpNanolets extends RouterNanoHTTPD {
    public static final int PORT = 4444;
    protected static AssetManager assetManager;
    private static final String TAG = "Robot";

    public HttpNanolets(AssetManager assetManager) throws IOException {
        super(PORT);
        this.assetManager = assetManager;

        addMappings();

    }

    // we override default addMappings completely
    @Override
    public void addMappings() {
        setNotImplementedHandler(NotImplementedHandler.class);
        setNotFoundHandler(Error404UriHandler.class);
        addRoute("/", IndexHandler.class);
        //addRoute("/index.html", IndexHandler.class);

        addRoute("/(.)+", MyStaticPageHandler.class);
        addRoute("/action/:action", ActionHandler.class);
    }

    public static class MyStaticPageHandler extends StaticPageHandler {

        protected BufferedInputStream fileToInputStream(File fileOrdirectory) throws IOException {
            // new FileInputStream(fileOrdirectory)
            InputStream is = assetManager.open("html/index.html");
            return new BufferedInputStream(is);
        }
    }

    public static class ActionHandler extends DefaultHandler {
        @Override
        public String getText() {
            return "UserA, UserB, UserC";
        }

        @Override
        public String getMimeType() {
            return MIME_PLAINTEXT;
        }

        @Override
        public Response.IStatus getStatus() {
            return Response.Status.OK;
        }
    }

    @Override
    public Response serve(IHTTPSession session)  {
        String uri = session.getUri();

        Log.i(TAG, "HttpNanolets::Response(" + session.getUri() + session.getQueryParameterString() + ")");

        try {
            if (uri.equals("/")) {

                InputStream is = assetManager.open("html/index.html");
                return newChunkedResponse(Response.Status.OK, "text/html", is);

                //String response = assetManager.;
                //return newFixedLengthResponse(response);
            }
        }
        catch (IOException exc)
        {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/text", "not found");
        }
        return  null;


    }

/*    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (uri.equals("/hello")) {

            FileInputStream fis = new FileInputStream("");

            return newChunkedResponse(Response.Status.OK, "app/jpg", fis);
        }
        return  null;
    }*/
}