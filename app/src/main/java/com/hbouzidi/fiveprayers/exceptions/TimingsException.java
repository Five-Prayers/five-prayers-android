package com.hbouzidi.fiveprayers.exceptions;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class TimingsException extends Throwable {

    public TimingsException(String message) {
        super(message);
    }

    public TimingsException(String message, Throwable cause) {
        super(message, cause);
    }
}
