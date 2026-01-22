package mvvm.views;

import contracts.ErrorState;
import core.abstraction.ViewBase;
import core.navigation.Router;
import core.security.AuthContext;
import core.security.UserPrincipal;
import mvvm.viewModels.LoginViewModel;
import mvvm.views.flight.FlightSearchView;
import shared.common.FlexAlignment;
import shared.common.MaterialColors;
import shared.components.Button;
import shared.components.PasswordInput;
import shared.components.TextInput;
import shared.layouts.FlexPanelV;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;


public final class LoginView extends ViewBase {
    //    private final Logger logger;
    private final LoginViewModel vm;

    public LoginView(LoginViewModel viewModel) {
        this.vm = viewModel;
        this.initComponents();
        this.bind();
    }

    @Override
    public void initComponents() {
        this.setLayout(new BorderLayout());
        this.flexPanel = new FlexPanelV(10, FlexAlignment.CENTER);
        JLabel usernameLabel = new JLabel("username:");
        this.usernameInput = new TextInput();
        this.usernameValidationLabel = new JLabel();
        JLabel passwordLabel = new JLabel("password:");
        this.passwordInput = new PasswordInput();
        this.passwordValidationLabel = new JLabel();
        this.errorLabel = new JLabel();
        this.loginButton = new Button("Login", MaterialColors.PURPLE_500, MaterialColors.WHITE, 20, 15);
        this.loginButton.setEnabled(false);
        this.errorLabel.setVisible(false);
        this.errorLabel.setForeground(MaterialColors.RED_600);
        this.usernameValidationLabel.setVisible(false);
        this.usernameValidationLabel.setForeground(MaterialColors.RED_600);
        this.passwordValidationLabel.setVisible(false);
        this.passwordValidationLabel.setForeground(MaterialColors.RED_600);

        this.setBackground(MaterialColors.WHITE);
        this.flexPanel.setBackground(MaterialColors.WHITE);

        this.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        this.flexPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        this.flexPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);

        this.flexPanel.add(usernameLabel);
        this.flexPanel.add(usernameInput);
        this.flexPanel.add(usernameValidationLabel);
        this.flexPanel.add(passwordLabel);
        this.flexPanel.add(passwordInput);
        this.flexPanel.add(passwordValidationLabel);
        this.flexPanel.add(loginButton);
        this.flexPanel.add(errorLabel);
        this.flexPanel.add(new JLabel("OR"));


        this.add(this.flexPanel);
    }

    @Override
    public void bind() {
        this.vm.usernameError().subscribe((oldValue, newValue) -> {
            this.usernameValidationLabel.setVisible((newValue != null));
            this.usernameValidationLabel.setText((String) newValue);
        });

        this.vm.passwordError().subscribe((oldValue, newValue) -> {
            this.passwordValidationLabel.setVisible(newValue != null);
            this.passwordValidationLabel.setText((String) newValue);
        });

        this.usernameInput.getDocument().addDocumentListener(new UsernameInputListener());
        this.passwordInput.getDocument().addDocumentListener(new PasswordInputListener());


        this.vm.getLoginCommand().subscribeChangeListener(() -> {
            this.loginButton.setEnabled(this.vm.getLoginCommand().canExecute(null));
        });
        this.loginButton.addActionListener(this::loginButtonActionHandler);


        this.vm.error().subscribe(((oldValue, newValue) -> {
            this.errorLabel.setVisible(newValue != null);
            assert newValue != null;
            this.errorLabel.setText(((ErrorState) newValue).message());
        }));
    }

    private FlexPanelV flexPanel;

    private TextInput usernameInput;
    private JLabel usernameValidationLabel;
    private PasswordInput passwordInput;
    private JLabel passwordValidationLabel;
    private JLabel errorLabel;
    private Button loginButton;
    private Button signUpButton;

    private void loginButtonActionHandler(ActionEvent ae) {
        this.vm.getLoginCommand().execute(null);
    }


    private class UsernameInputListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            LoginView.this.vm.username().set(LoginView.this.usernameInput.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            LoginView.this.vm.username().set(LoginView.this.usernameInput.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    private class PasswordInputListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            LoginView.this.vm.password().set(new String(LoginView.this.passwordInput.getPassword()));
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            LoginView.this.vm.password().set(new String(LoginView.this.passwordInput.getPassword()));
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }
}