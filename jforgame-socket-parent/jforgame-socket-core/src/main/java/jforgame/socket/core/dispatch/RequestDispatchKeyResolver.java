package jforgame.socket.core.dispatch;

/**
 * Request dispatch key resolver.
 * Used to extract routing keys required by the thread model from session and request context.
 * @since 4.0.0
 */
@FunctionalInterface
public interface RequestDispatchKeyResolver {

    /**
     * Resolve dispatch key
     *
     * @param context request context
     * @return dispatch key
     */
    long resolve(RequestContext context);
}
