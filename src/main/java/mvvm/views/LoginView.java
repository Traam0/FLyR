package mvvm.views;

import core.abstraction.ViewBase;
import core.navigation.Router;
import mvvm.views.flight.FlightSearchView;
import shared.common.FlexAlignment;
import shared.common.MaterialColors;
import shared.components.Button;
import shared.layouts.FlexPanelV;

import javax.swing.*;
import java.awt.*;

public final class LoginView extends ViewBase {


    private final Router router;

    public LoginView(Router router) {
        this.router = router;
        this.initComponents();
    }

    @Override
    public void initComponents() {
        this.setLayout(new BorderLayout());
        FlexPanelV flexPanel = new FlexPanelV(10, FlexAlignment.CENTER);
        flexPanel.add(new JLabel("Continue AS"));
        Button defaultScreenBtn = new Button("USER", MaterialColors.PURPLE_500, MaterialColors.WHITE, 20, 15);
        defaultScreenBtn.addActionListener(e -> {
           router.navigateTo(FlightSearchView.class);
        });
        this.setBackground(MaterialColors.WHITE);
        flexPanel.setBackground(MaterialColors.WHITE);

        this.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        flexPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        flexPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);

        flexPanel.add(defaultScreenBtn);
        flexPanel.add(new JLabel("OR"));
        Button adminScreenBtn = new Button("ADMIN", MaterialColors.CYAN_700, MaterialColors.WHITE, 20, 15);
        adminScreenBtn.addActionListener(e -> {
            throw new RuntimeException("NOT YET IMPLEMENTED");
        });
        flexPanel.add(adminScreenBtn);


        this.add(flexPanel);
    }

    @Override
    public void bind() {
//        this.vm.usernameError().subscribe((oldValue, newValue) -> {
//            this.usernameValidationLabel.setVisible((newValue != null));
//            this.usernameValidationLabel.setText((String) newValue);
//        });
//
//        this.vm.passwordError().subscribe((oldValue, newValue) -> {
//            this.passwordValidationLabel.setVisible(newValue != null);
//            this.passwordValidationLabel.setText((String) newValue);
//        });
//
//        this.usernameInput.getDocument().addDocumentListener(new UsernameInputListener());
//        this.passwordInput.getDocument().addDocumentListener(new PasswordInputListener());
//
//
//        this.vm.getLoginCommand().subscribeChangeListener(() -> {
//            this.loginButton.setEnabled(this.vm.getLoginCommand().canExecute(null));
//        });
//        this.loginButton.addActionListener(this::loginButtonActionHandler);
//
//        this.signUpButton.addActionListener(this::signUpButtonActionHandler);
//
//        this.vm.error().subscribe(((oldValue, newValue) -> {
//            this.errorLabel.setVisible(newValue != null);
//            assert newValue != null;
//            this.errorLabel.setText(((ErrorState) newValue).message());
//        }));
    }

}

