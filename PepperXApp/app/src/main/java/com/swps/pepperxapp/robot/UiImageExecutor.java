package com.swps.pepperxapp.robot;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;

import java.util.List;

public class UiImageExecutor extends BaseQiChatExecutor {
    private UiNotifier _uiNotifier;

    public UiImageExecutor(QiContext context, UiNotifier uiNotifier)
    {
        super(context);
        this._uiNotifier = uiNotifier;
    }


    @Override
    public void runWith(List<String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        String name = params.get(0);
    }

    @Override
    public void stop() {

    }
}
