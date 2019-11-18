/*
 *  Copyright (C) 2019 SmartLife Robotics, Poland
 *  See COPYING for the license
 */
package com.swps.pepperxapp.robot;

import com.aldebaran.qi.sdk.object.conversation.Phrase;

import java.util.List;

public interface UiNotifier {
    void setText(String text);

    void updateQiChatSuggestions(List<Phrase> recommendation);
}
