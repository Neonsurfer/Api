package com.simple.api.error;

public class TokenExpiredException extends RemoteServiceException {
    public TokenExpiredException() {
        super("A felhasználói token lejárt nem értelmezhető, vagy nem kapcsolódik a kártyához", 10051);
    }
}
