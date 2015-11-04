package com.example.sym.protocole_applicatif;

import java.util.EventListener;

/**
 * Created by Domingues on 14.10.2015.
 */
public interface CommunicationEventListener extends EventListener {
    boolean handleServerResponse(String response);
}
