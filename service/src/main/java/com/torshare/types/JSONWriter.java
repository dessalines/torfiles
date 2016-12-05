package com.torshare.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torshare.tools.Tools;

/**
 * Created by tyler on 12/4/16.
 */
public interface JSONWriter {
    default String json(String wrappedName) {
        try {
            String val = Tools.JACKSON.writeValueAsString(this);

            String json = (wrappedName != null) ? "{\"" + wrappedName + "\":" +
                    val +
                    "}" : val;

            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    default String json() {
        return json(null);
    }

}