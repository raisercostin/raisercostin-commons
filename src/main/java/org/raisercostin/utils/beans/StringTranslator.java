package org.raisercostin.utils.beans;

import java.io.UnsupportedEncodingException;

public interface StringTranslator {

    /**
     * Translates the initial string by applying some character mappings
     * @param initial
     * @return
     */
    String translateString(String initial);

    /**
     * Truncates the initial string
     * @param initial
     * @return
     */
    String truncateString(String initial, int maxLength) throws UnsupportedEncodingException;
}
