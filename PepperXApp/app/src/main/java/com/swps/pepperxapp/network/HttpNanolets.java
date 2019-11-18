/*
 *  Copyright (C) 2019 SmartLife Robotics, Poland
 *  See COPYING for the license
 */

package com.swps.pepperxapp.network;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;
import android.util.Log;

import com.swps.pepperxapp.*;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        super.addMappings();

        removeRoute("/");
        removeRoute("/index.html");
        addRoute("/speak", SpeakHandler.class);
        addRoute("/action", ActionHandler.class);
        addRoute("/(.+)", MyStaticPageHandler.class);
        addRoute("/index.html", MyStaticPageHandler.class);
    }

    public static class MyStaticPageHandler extends StaticPageHandler {

        @Override
        public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
            Log.i("Robot", "MyStaticPageHandler::get(" + session.getUri() + ")");

            String realUri = "html/" + normalizeUri(session.getUri());

            try {
                InputStream is = assetManager.open(realUri);

                return newChunkedResponse(getStatus(), getMimeTypeForFile(realUri), is);
            } catch (IOException ioe) {
                return new Error404UriHandler().get(uriResource, urlParams, session);
            }
        }
    }



    public static class ActionHandler extends DefaultHandler {
        @Override
        public Response post(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session){

            String cmd="";

            try {
                JsonReader reader = new JsonReader(new InputStreamReader(session.getInputStream()));
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    String val = reader.nextString();
                    if (name.equals("cmd"))
                        cmd = val;

                }
                reader.endObject();
            }
            catch (IOException e)
            {
                return new Error404UriHandler().get(uriResource, urlParams, session);
            }


            Log.i("Robot", "ActionHandler(" + cmd + ")");
            return newFixedLengthResponse(null);
        }

        // for easier debugging
        @Override
        public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session){
            return get(uriResource, urlParams, session);
        }

        @Override
        public String getText() {
            return "not implemented";
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

    public static class SpeakHandler extends DefaultHandler {
        @Override
        public Response post(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session){

            String text="";

            try {
                JsonReader reader = new JsonReader(new InputStreamReader(session.getInputStream()));
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    String val = reader.nextString();
                    if (name.equals("text"))
                        text = val;

                }
                reader.endObject();
            }
            catch (IOException e)
            {
                return new Error404UriHandler().get(uriResource, urlParams, session);
            }

            Log.i("Robot", "SpeekHandler(" + text + ")");
            return newFixedLengthResponse(null);
        }

        // for easier debugging
        @Override
        public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session){
            return get(uriResource, urlParams, session);
        }


        @Override
        public String getText() {
            return "not implemented";
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

/*    @Override
    public Response serve(IHTTPSession session)  {
        String uri = session.getUri();

        Log.i(TAG, "HttpNanolets::Response(" + session.getUri() + " ? " + session.getQueryParameterString() + ")");

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

    }*/

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