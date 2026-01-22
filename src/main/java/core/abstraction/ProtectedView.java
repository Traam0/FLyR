package core.abstraction;

import core.navigation.Router;

public abstract class ProtectedView extends ViewBase {
    protected final Router router;

    public ProtectedView(Router router) {
        super();
        if (!router.getContext().isAuthenticated()) {
            router.authenticate();
        }
        this.router = router;
    }
}
