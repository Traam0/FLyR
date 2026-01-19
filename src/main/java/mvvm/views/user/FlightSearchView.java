package mvvm.views.user;

import core.abstraction.ViewBase;
import shared.common.MaterialColors;
import core.navigation.Router;
import shared.layouts.FlexPanelV;
import shared.layouts.FlexPanelH;
import shared.common.FlexAlignment;
import shared.components.Button;
import shared.components.TopBar;
import shared.components.TextInput;
import mvvm.viewModels.FlightSearchViewModel;

import java.awt.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


public class FlightSearchView extends ViewBase {
    private final FlightSearchViewModel vm;
    private final Router router;
    private final Logger logger;

    private TextInput departureInput;
    private JLabel departureErrorLabel;
    private TextInput arrivalInput;
    private JLabel arrivalErrorLabel;
    private TextInput departureDateInput;
    private JLabel departureDateErrorLabel;
    private TextInput returnDateInput;
    private JLabel returnDateErrorLabel;
    private JSpinner passengersSpinner;
    private JCheckBox roundTripCheckbox;
    private Button searchButton;
    private Button clearButton;
    private JPanel resultsPanel;
    private JScrollPane resultsScrollPane;
    private JLabel loadingLabel;
    private JLabel errorLabel;
    private JLabel noResultsLabel;
    private FlexPanelV mainPanel;
    private FlexPanelV formPanel;
    private FlexPanelH buttonPanel;
    private FlexPanelV resultsContainer;


    public FlightSearchView(Router router, Logger logger, FlightSearchViewModel viewModel) {
        this.vm = viewModel;
        this.router = router;
        this.logger = logger;
        this.initComponents();
        this.bind();

    }

    @Override
    public void initComponents() {
        this.setLayout(new BorderLayout());
        mainPanel = new FlexPanelV(5, FlexAlignment.TOP);
        mainPanel.setBackground(MaterialColors.WHITE);
        mainPanel.add(TopBar.build(router, logger));
        createStatusIndicators();
//        createSearchForm();
//        createResultsArea();

        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void createSearchForm() {
        formPanel = new FlexPanelV(10, FlexAlignment.TOP);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Search Flights",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLUE_700
        ));

        // Departure city
        JLabel departureLabel = new JLabel("Departure City:");
        departureLabel.setBackground(MaterialColors.WHITE);
        this.departureInput = new TextInput();
        this.departureErrorLabel = createErrorLabel();

        // Arrival city
        JLabel arrivalLabel = new JLabel("Arrival City:");
        arrivalLabel.setBackground(MaterialColors.WHITE);
        arrivalInput = new TextInput();
        arrivalErrorLabel = createErrorLabel();

        // Dates
        JLabel departureDateLabel = new JLabel("Departure Date:");
        departureDateLabel.setBackground(MaterialColors.WHITE);
        this.departureDateInput = new TextInput();
        this.departureDateErrorLabel = createErrorLabel();

        JLabel returnDateLabel = new JLabel("Return Date:");
        returnDateLabel.setBackground(MaterialColors.WHITE);
        this.returnDateInput = new TextInput();
        this.returnDateErrorLabel = createErrorLabel();

        // Passengers
        JLabel passengersLabel = new JLabel("Passengers:");
        passengersLabel.setBackground(MaterialColors.WHITE);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        passengersSpinner = new JSpinner(spinnerModel);

        // Round trip checkbox
        roundTripCheckbox = new JCheckBox("Round Trip");

        // Add components to form panel
        formPanel.add(departureLabel);
        formPanel.add(departureInput);
        formPanel.add(departureErrorLabel);

        formPanel.add(arrivalLabel);
        formPanel.add(arrivalInput);
        formPanel.add(arrivalErrorLabel);

        // Date row panel
        FlexPanelH datePanel = new FlexPanelH(10, FlexAlignment.LEFT);
        datePanel.setBackground(MaterialColors.WHITE);

        FlexPanelV departureDatePanel = new FlexPanelV(2, FlexAlignment.TOP);
        departureDatePanel.add(departureDateLabel);
        departureDatePanel.add(departureDateInput);
        departureDatePanel.add(departureDateErrorLabel);

        FlexPanelV returnDatePanel = new FlexPanelV(2, FlexAlignment.TOP);
        returnDatePanel.add(returnDateLabel);
        returnDatePanel.add(returnDateInput);
        returnDatePanel.add(returnDateErrorLabel);

        datePanel.add(departureDatePanel);
        datePanel.add(returnDatePanel);
        formPanel.add(datePanel);

        // Options row panel
        FlexPanelH optionsPanel = new FlexPanelH(20, FlexAlignment.LEFT);
        optionsPanel.setBackground(MaterialColors.WHITE);

        FlexPanelV passengersPanel = new FlexPanelV(2, FlexAlignment.TOP);
        passengersPanel.add(passengersLabel);
        passengersPanel.add(passengersSpinner);

        optionsPanel.add(passengersPanel);
        optionsPanel.add(roundTripCheckbox);
        formPanel.add(optionsPanel);

        // Create button panel
        createButtonPanel();
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);
    }

    private void createButtonPanel() {
        buttonPanel = new FlexPanelH(15, FlexAlignment.CENTER);
        buttonPanel.setBackground(MaterialColors.WHITE);

        searchButton = new Button("Search Flights",
                MaterialColors.BLUE_400_ACCENT, MaterialColors.WHITE, 20, 10);
        searchButton.setEnabled(false);

        clearButton = new Button("Clear",
                MaterialColors.GREY_500, MaterialColors.WHITE, 20, 10);

        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
    }

    private void createResultsArea() {
        resultsContainer = new FlexPanelV(10, FlexAlignment.TOP);
        resultsContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Available Flights",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.GREEN_700
        ));

        resultsPanel = new FlexPanelV(8, FlexAlignment.TOP);
        resultsPanel.setBackground(MaterialColors.WHITE);

        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setPreferredSize(new Dimension(800, 300));
        resultsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        noResultsLabel = new JLabel("No flights found. Try a different search.");
        noResultsLabel.setForeground(MaterialColors.GREY_600);
        noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noResultsLabel.setVisible(false);

        resultsContainer.add(resultsScrollPane);
        resultsContainer.add(noResultsLabel);

        mainPanel.add(resultsContainer);
    }

    private void createStatusIndicators() {
        loadingLabel = new JLabel("Searching for flights...");
        loadingLabel.setForeground(MaterialColors.BLUE_600);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setVisible(this.vm.isLoading.get());

        errorLabel = new JLabel();
        errorLabel.setForeground(MaterialColors.RED_600);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setVisible(false);

        mainPanel.add(loadingLabel);
        mainPanel.add(errorLabel);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setForeground(MaterialColors.RED_600);
        label.setFont(label.getFont().deriveFont(11f));
        label.setVisible(false);
        return label;
    }

    @Override
    public void bind() {
        logger.info("bindinf start");

        this.vm.isLoading.subscribe((o, n) -> {
            this.loadingLabel.setVisible((Boolean) n);
            logger.info(String.valueOf(n));
        });
        logger.info("bindinf start");
        // Bind input fields to ViewModel properties
//        bindInputFields();
//
//        // Bind error labels
//        bindErrorLabels();
//
//        // Bind commands
//        bindCommands();
//
//        // Bind results
//        bindResults();
//
//        // Bind loading and error states
//        bindStatusIndicators();
    }

    //    private void bindInputFields() {
//        // Departure city
//        departureInput.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                vm.departureCity().set(departureInput.getText());
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                vm.departureCity().set(departureInput.getText());
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {}
//        });
//
//        // Arrival city
//        arrivalInput.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                vm.arrivalCity().set(arrivalInput.getText());
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                vm.arrivalCity().set(arrivalInput.getText());
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {}
//        });
//
//        // Departure date
//        departureDateInput.addPropertyChangeListener("value", evt -> {
//            vm.departureDate().set((Date) departureDateInput.getValue());
//        });
//
//        // Return date
//        returnDateInput.addPropertyChangeListener("value", evt -> {
//            vm.returnDate().set((Date) returnDateInput.getValue());
//        });
//
//        // Passengers
//        passengersSpinner.addChangeListener(e -> {
//            vm.passengers().set((Integer) passengersSpinner.getValue());
//        });
//
//        // Round trip checkbox
//        roundTripCheckbox.addItemListener(e -> {
//            boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;
//            vm.isRoundTrip().set(isSelected);
//            returnDateInput.setEnabled(isSelected);
//
//            if (!isSelected) {
//                returnDateInput.setValue(null);
//            }
//        });
//    }
//
//    private void bindErrorLabels() {
//        vm.departureCityError().subscribe((oldVal, newVal) -> {
//            departureErrorLabel.setVisible(newVal != null);
//            departureErrorLabel.setText((String) newVal);
//        });
//
//        vm.arrivalCityError().subscribe((oldVal, newVal) -> {
//            arrivalErrorLabel.setVisible(newVal != null);
//            arrivalErrorLabel.setText((String) newVal);
//        });
//
//        vm.departureDateError().subscribe((oldVal, newVal) -> {
//            departureDateErrorLabel.setVisible(newVal != null);
//            departureDateErrorLabel.setText((String) newVal);
//        });
//
//        vm.returnDateError().subscribe((oldVal, newVal) -> {
//            returnDateErrorLabel.setVisible(newVal != null);
//            returnDateErrorLabel.setText((String) newVal);
//        });
//    }
//
//    private void bindCommands() {
//        // Bind search command
//        vm.getSearchCommand().subscribeChangeListener(() -> {
//            searchButton.setEnabled(vm.getSearchCommand().canExecute(null));
//        });
//
//        searchButton.addActionListener((ActionEvent e) -> {
//            vm.getSearchCommand().execute(null);
//        });
//
//        // Bind clear command
//        clearButton.addActionListener((ActionEvent e) -> {
//            vm.getClearCommand().execute(null);
//        });
//    }
//
//    private void bindResults() {
//        vm.flights().subscribe((oldVal, newVal) -> {
//            resultsPanel.removeAll();
//
//            if (newVal == null || newVal.isEmpty()) {
//                noResultsLabel.setVisible(true);
//            } else {
//                noResultsLabel.setVisible(false);
//                for (Flight flight : newVal) {
//                    resultsPanel.add(createFlightCard(flight));
//                }
//            }
//
//            resultsPanel.revalidate();
//            resultsPanel.repaint();
//        });
//    }
//
//    private JPanel createFlightCard(Flight flight) {
//        FlexPanelH card = new FlexPanelH(15, FlexAlignment.LEFT);
//        card.setBackground(MaterialColors.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(MaterialColors.GREY_200),
//                BorderFactory.createEmptyBorder(10, 15, 10, 15)
//        ));
//
//        // Flight info
//        FlexPanelV infoPanel = new FlexPanelV(5, FlexAlignment.TOP);
//        infoPanel.setBackground(MaterialColors.WHITE);
//
//        // Flight number and airline
//        FlexPanelH headerPanel = new FlexPanelH(20, FlexAlignment.LEFT);
//        JLabel flightNumberLabel = new JLabel(flight.getFlightNumber());
//        flightNumberLabel.setFont(flightNumberLabel.getFont().deriveFont(Font.BOLD, 14));
//        flightNumberLabel.setForeground(MaterialColors.BLUE_700);
//
//        JLabel airlineLabel = new JLabel(flight.getAirline());
//        airlineLabel.setForeground(MaterialColors.GREY_700);
//
//        headerPanel.add(flightNumberLabel);
//        headerPanel.add(airlineLabel);
//        infoPanel.add(headerPanel);
//
//        // Route and times
//        FlexPanelH routePanel = new FlexPanelH(10, FlexAlignment.LEFT);
//
//        FlexPanelV departurePanel = new FlexPanelV(2, FlexAlignment.TOP);
//        JLabel departureTimeLabel = new JLabel(formatTime(flight.getDepartureTime()));
//        departureTimeLabel.setFont(departureTimeLabel.getFont().deriveFont(Font.BOLD, 16));
//        JLabel departureAirportLabel = new JLabel(flight.getDepartureAirport());
//        departureAirportLabel.setForeground(MaterialColors.GREY_600);
//        departurePanel.add(departureTimeLabel);
//        departurePanel.add(departureAirportLabel);
//
//        FlexPanelV durationPanel = new FlexPanelV(2, FlexAlignment.CENTER);
//        JLabel durationLabel = new JLabel(flight.getDuration());
//        durationLabel.setForeground(MaterialColors.GREY_500);
//        durationPanel.add(new JLabel("→"));
//        durationPanel.add(durationLabel);
//
//        FlexPanelV arrivalPanel = new FlexPanelV(2, FlexAlignment.TOP);
//        JLabel arrivalTimeLabel = new JLabel(formatTime(flight.getArrivalTime()));
//        arrivalTimeLabel.setFont(arrivalTimeLabel.getFont().deriveFont(Font.BOLD, 16));
//        JLabel arrivalAirportLabel = new JLabel(flight.getArrivalAirport());
//        arrivalAirportLabel.setForeground(MaterialColors.GREY_600);
//        arrivalPanel.add(arrivalTimeLabel);
//        arrivalPanel.add(arrivalAirportLabel);
//
//        routePanel.add(departurePanel);
//        routePanel.add(durationPanel);
//        routePanel.add(arrivalPanel);
//        infoPanel.add(routePanel);
//
//        // Price and select button
//        FlexPanelV actionPanel = new FlexPanelV(5, FlexAlignment.CENTER);
//        actionPanel.setBackground(MaterialColors.WHITE);
//
//        JLabel priceLabel = new JLabel(String.format("$%.2f", flight.getPrice()));
//        priceLabel.setFont(priceLabel.getFont().deriveFont(Font.BOLD, 18));
//        priceLabel.setForeground(MaterialColors.GREEN_700);
//
//        Button selectButton = new Button("Select",
//                MaterialColors.BLUE_500, MaterialColors.WHITE, 15, 8);
//        selectButton.addActionListener(e -> onFlightSelected(flight));
//
//        actionPanel.add(priceLabel);
//        actionPanel.add(selectButton);
//
//        card.add(infoPanel);
//        card.add(actionPanel);
//
//        return card;
//    }
//
//    private void bindStatusIndicators() {
//        vm.isLoading().subscribe((oldVal, newVal) -> {
//            loadingLabel.setVisible(Boolean.TRUE.equals(newVal));
//            searchButton.setEnabled(!Boolean.TRUE.equals(newVal));
//        });
//
//        vm.error().subscribe((oldVal, newVal) -> {
//            errorLabel.setVisible(newVal != null);
//            if (newVal != null) {
//                errorLabel.setText(((ErrorState) newVal).message());
//            }
//        });
//    }
//
//    private String formatTime(Date date) {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//        return date != null ? timeFormat.format(date) : "";
//    }
//
//    private void onFlightSelected(Flight flight) {
//        // Implement flight selection logic
//        JOptionPane.showMessageDialog(this,
//                String.format("Selected flight %s from %s to %s",
//                        flight.getFlightNumber(),
//                        flight.getDepartureAirport(),
//                        flight.getArrivalAirport()),
//                "Flight Selected",
//                JOptionPane.INFORMATION_MESSAGE);
//    }
}