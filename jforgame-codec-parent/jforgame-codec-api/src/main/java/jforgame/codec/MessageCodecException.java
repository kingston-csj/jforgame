package jforgame.codec;

/**
 * Indicates that a message could not be fully encoded or decoded.
 * <p>
 * A codec should propagate this exception to the caller instead of returning
 * {@code null} or a partial result. The caller must discard any partial output
 * from the failed operation.
 *
 * @see MessageCodec
 */
public class MessageCodecException extends RuntimeException {
    /**
     * Creates an exception describing a codec failure.
     *
     * @param message a description of the failure
     */
    public MessageCodecException(String message) {
        super(message);
    }

    /**
     * Creates an exception describing a codec failure and preserving its cause.
     *
     * @param message a description of the failure
     * @param cause the underlying cause of the failure
     */
    public MessageCodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
