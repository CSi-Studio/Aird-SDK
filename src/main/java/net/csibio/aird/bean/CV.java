package net.csibio.aird.bean;

import lombok.Data;

/**
 * Controlled Vocabulary
 */
@Data
public class CV {
    /**
     * cv id:cv name
     */
    String cvid;

    /**
     * cv value
     */
    String value;

    /**
     * unit id:unit name
     */
    String units;
}
