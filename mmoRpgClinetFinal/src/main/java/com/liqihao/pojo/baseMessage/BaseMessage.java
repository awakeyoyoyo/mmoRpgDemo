package com.liqihao.pojo.baseMessage;

import java.util.List;

public class BaseMessage {
    List<SceneMessage> sceneMessages;
    List<NPCMessage> npcMessages;
    public List<NPCMessage> getNpcMessages() {
        return npcMessages;
    }

    public void setNpcMessages(List<NPCMessage> npcMessages) {
        this.npcMessages = npcMessages;
    }

    public List<SceneMessage> getSceneMessages() {
        return sceneMessages;
    }

    public void setSceneMessages(List<SceneMessage> sceneMessages) {
        this.sceneMessages = sceneMessages;
    }
}
