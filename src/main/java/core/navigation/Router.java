package core.navigation;

import core.mvvm.View;
import core.security.AuthContext;

import javax.swing.*;
import java.util.Map;

public interface Router {
    void navigateTo(Class<? extends View> view);
    void navigateTo(Class<? extends View> view, Map<String, Object> params);
    void goBack();
    JFrame getWindow();
    AuthContext getContext();
    Map<String, Object> getParams();
}
