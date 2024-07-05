package mg.itu.prom16.etu2564;
import jakarta.servlet.http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Mysession {

    private HttpSession session;

    public Mysession() {

    }

    public Mysession(HttpSession session) {
        this.session = session;
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void add(String key, Object object) {
        if (session != null) {
            session.setAttribute(key, object);
        } else {
            throw new IllegalStateException("HttpSession is null. Make sure it is properly initialized.");
        }
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }


}
