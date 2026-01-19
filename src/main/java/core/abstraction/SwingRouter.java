package core.abstraction;


import core.dependencyInjection.ServiceProvider;
import core.mvvm.View;
import core.navigation.Router;
import core.security.AuthContext;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

public class SwingRouter implements Router {
    private final JFrame window;
    private final AuthContext context;
    private final ServiceProvider serviceProvider;
    private final Stack<Class<? extends View>> history = new Stack<>();
    private final Map<String, Object> params;

    public SwingRouter(Logger logger, ServiceProvider serviceProvider) {
        this.window = new JFrame();
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.serviceProvider = serviceProvider;
        this.context = serviceProvider.getRequiredService(AuthContext.class);
        this.params = new HashMap<>();
    }

    @Override
    public void navigateTo(Class<? extends View> view) {
        this.params.clear();
        this.push(view);
    }

    @Override
    public void navigateTo(Class<? extends View> view, Map<String, Object> params) {
        this.params.putAll(params);
        this.push(view);
    }

    @Override
    public void goBack() {
        this.params.clear();
        if (this.history.size() <= 1) return;
        this.history.pop();
        SwingUtilities.invokeLater(() -> {
            this.window.setTitle("FLyR");
            this.window.getContentPane().removeAll();
            this.window.getContentPane().add((Component) this.serviceProvider.getRequiredService(history.peek()));
            this.window.revalidate();
            this.window.repaint();
        });
    }

    public JFrame getWindow() {
        return this.window;
    }

    public AuthContext getContext() {
        return this.context;
    }

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(this.params);
    }

    private void push(Class<? extends View> view) {
        SwingUtilities.invokeLater(() -> {
            var viewName = view.getSimpleName();
            this.window.setTitle(viewName.substring(0,  viewName.length() - "view".length()));
            this.window.getContentPane().removeAll();
            this.window.getContentPane().add((Component) this.serviceProvider.getRequiredService(view));
            this.window.revalidate();
            this.window.repaint();
            this.history.add(view);
        });
    }
}
