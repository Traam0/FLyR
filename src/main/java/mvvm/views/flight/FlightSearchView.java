package mvvm.views.flight;

import contracts.FlightData;
import contracts.wrappers.Resource;
import core.abstraction.ViewBase;
import mvvm.views.LoginView;
import shared.common.MScrollBar;
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
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;


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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setBackground(MaterialColors.WHITE);
        mainPanel.add(TopBar.build(router, logger));
        createSearchForm();
        createStatusIndicators();
        this.add(mainPanel, BorderLayout.CENTER);
        this.createResultsArea();
        if (this.vm.flights.get().getStatus() == Resource.Status.SUCCESS)
            for (var f : this.vm.flights.get().getData())
                this.resultsPanel.add(this.createFlightCard(f));

    }

    @Override
    @SuppressWarnings("unchecked")
    public void bind() {
        this.vm.flights.subscribe((o, n) -> {
            var data = (Resource<FlightData[]>) n;
            switch (data.getStatus()) {
                case LOADING:
                    this.loadingLabel.setVisible(true);
                    break;
                case ERROR:
                    this.loadingLabel.setVisible(false);
                    this.errorLabel.setVisible(true);
                    this.errorLabel.setText(data.getMessage());
                    break;
                case SUCCESS:
                    this.loadingLabel.setVisible(false);
                    this.resultsPanel.removeAll();
                    if (data.getData().length == 0)
                        this.noResultsLabel.setVisible(true);
                    for (var f : data.getData())
                        this.resultsPanel.add(this.createFlightCard(f));
                    resultsScrollPane.revalidate();
            }
        });
        this.vm.departureCity.subscribe((o, n) -> SwingUtilities.invokeLater(() -> this.departureInput.setText((String) n)));
        this.vm.arrivalCity.subscribe((o, n) -> SwingUtilities.invokeLater(() -> this.arrivalInput.setText((String) n)));
        this.vm.departureDate.subscribe((o, n) -> SwingUtilities.invokeLater(() -> this.departureDateInput.setText((String) n)));
        this.vm.arrivalDate.subscribe((o, n) -> SwingUtilities.invokeLater(() -> this.returnDateInput.setText((String) n)));
        this.vm.passengerCount.subscribe((o, n) -> SwingUtilities.invokeLater(() -> this.passengersSpinner.setValue(n)));
        this.vm.isRoundTrip.subscribe((o, n) -> SwingUtilities.invokeLater(() -> this.roundTripCheckbox.setSelected((boolean) n)));
    }

    private void createSearchForm() {
        formPanel = new FlexPanelV(10, FlexAlignment.EVEN);
        formPanel.setBackground(MaterialColors.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Search Flights",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLUE_700
        ));

        //row1
        FlexPanelH travelInformationPanel = new FlexPanelH(10, FlexAlignment.EVEN);
        travelInformationPanel.setBackground(MaterialColors.WHITE);

        //      Departure City
        FlexPanelV departurePanel = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel departureLabel = new JLabel("Departure City:     ");
        departureLabel.setBackground(MaterialColors.WHITE);
        this.departureInput = new TextInput();
        this.departureErrorLabel = createErrorLabel();

        departurePanel.add(departureLabel);
        departurePanel.add(this.departureInput);
        departurePanel.add(this.departureErrorLabel);
        departurePanel.setBackground(MaterialColors.WHITE);
        travelInformationPanel.add(departurePanel);


        //      Arrival city
        FlexPanelV arrivalPanel = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel arrivalLabel = new JLabel("Arrival City:     ");
        arrivalLabel.setBackground(MaterialColors.WHITE);
        arrivalInput = new TextInput();
        arrivalErrorLabel = createErrorLabel();

        arrivalPanel.add(arrivalLabel);
        arrivalPanel.add(arrivalInput);
        arrivalPanel.add(arrivalErrorLabel);
        arrivalPanel.setBackground(MaterialColors.WHITE);
        travelInformationPanel.add(arrivalPanel);

        //      Departure Date
        FlexPanelV departureDatePanel = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel departureDateLabel = new JLabel("Departure Date: dd/mm/YYYY");
        departureDateLabel.setBackground(MaterialColors.WHITE);
        this.departureDateInput = new TextInput();
        this.departureDateErrorLabel = createErrorLabel();

        departureDatePanel.add(departureDateLabel);
        departureDatePanel.add(departureDateInput);
        departureDatePanel.add(departureDateErrorLabel);
        departureDatePanel.setBackground(MaterialColors.WHITE);
        travelInformationPanel.add(departureDatePanel);

        //      Return Date
        FlexPanelV returnDatePanel = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel returnDateLabel = new JLabel("Return Date: dd/mm/YYYY");
        returnDateLabel.setBackground(MaterialColors.WHITE);
        this.returnDateInput = new TextInput();
        this.returnDateErrorLabel = createErrorLabel();

        returnDatePanel.add(returnDateLabel);
        returnDatePanel.add(returnDateInput);
        returnDatePanel.add(returnDateErrorLabel);
        returnDatePanel.setBackground(MaterialColors.WHITE);
        travelInformationPanel.add(returnDatePanel);

        this.formPanel.add(travelInformationPanel);

        // row2
        FlexPanelH optionalInformationPanel = new FlexPanelH(10, FlexAlignment.EVEN);
        optionalInformationPanel.setBackground(MaterialColors.WHITE);

        //      Passengers
        FlexPanelV passengersPanel = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel passengersLabel = new JLabel("Passengers:");
        passengersLabel.setBackground(MaterialColors.WHITE);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        this.passengersSpinner = new JSpinner(spinnerModel);


        passengersPanel.add(passengersLabel);
        passengersPanel.add(passengersSpinner);
        passengersPanel.setBackground(MaterialColors.WHITE);
        optionalInformationPanel.add(passengersPanel);

        //      Round trip
        FlexPanelV roundTripPanel = new FlexPanelV(8, FlexAlignment.TOP);
        FlexPanelH optionsPanel = new FlexPanelH(20, FlexAlignment.LEFT);
        roundTripCheckbox = new JCheckBox("Round Trip");
        optionsPanel.setBackground(MaterialColors.WHITE);

        optionsPanel.add(roundTripCheckbox);
        roundTripPanel.add(optionsPanel);
        roundTripPanel.setBackground(MaterialColors.WHITE);
        optionalInformationPanel.add(roundTripCheckbox);

        //      buttons
        buttonPanel = new FlexPanelH(15, FlexAlignment.CENTER);
        buttonPanel.setBackground(MaterialColors.WHITE);

        this.searchButton = new Button("Search Flights", MaterialColors.BLUE_400_ACCENT, MaterialColors.WHITE, 20, 10);
        this.searchButton.addActionListener(this::searchButtonHandler);
        this.clearButton = new Button("Clear", MaterialColors.RED_100_ACCENT, MaterialColors.WHITE, 20, 10);
        this.clearButton.addActionListener(this::clearFromHandler);

        FlexPanelH buttonsPanel = new FlexPanelH(15, FlexAlignment.CENTER);
        buttonsPanel.add(this.searchButton);
        buttonsPanel.add(this.clearButton);
        buttonsPanel.setBackground(MaterialColors.WHITE);
        optionalInformationPanel.add(buttonsPanel);

        this.formPanel.add(optionalInformationPanel);


        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);
    }

    private void createResultsArea() {
        resultsContainer = new FlexPanelV(10, FlexAlignment.TOP);

        resultsPanel = new FlexPanelV(8, FlexAlignment.EVEN);
        resultsPanel.setBackground(MaterialColors.WHITE);

        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultsScrollPane.setBackground(MaterialColors.WHITE);
        resultsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        resultsScrollPane.getVerticalScrollBar().setUI(new MScrollBar());

        noResultsLabel = new JLabel("No flights found. Try a different search.");
        noResultsLabel.setForeground(MaterialColors.GREY_600);
        noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noResultsLabel.setVisible(false);

        resultsContainer.add(resultsScrollPane);
        resultsContainer.add(noResultsLabel);

        mainPanel.add(resultsContainer);
        resultsScrollPane.setPreferredSize(new Dimension(800, router.getWindow().getHeight() - formPanel.getPreferredSize().height));

    }

    private void createStatusIndicators() {
        loadingLabel = new JLabel("Searching for flights...");
        loadingLabel.setForeground(MaterialColors.BLUE_600);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setVisible(this.vm.flights.get().getStatus() == Resource.Status.LOADING);

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

    private JPanel createFlightCard(FlightData flight) {
        // Main card container
        FlexPanelH card = new FlexPanelH(15, FlexAlignment.LEFT);
        card.setBackground(MaterialColors.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MaterialColors.BLUE_300, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Left side: Flight information
        FlexPanelV flightInfoPanel = new FlexPanelV(8, FlexAlignment.TOP);
        flightInfoPanel.setBackground(MaterialColors.WHITE);

        // Flight header with number
        FlexPanelH headerPanel = new FlexPanelH(10, FlexAlignment.LEFT);
        headerPanel.setBackground(MaterialColors.WHITE);

        JLabel flightNumberLabel = new JLabel(flight.flightNumber());
        flightNumberLabel.setFont(flightNumberLabel.getFont().deriveFont(Font.BOLD, 16));
        flightNumberLabel.setForeground(MaterialColors.BLUE_700);

        // Add airline icon or identifier
        JLabel airlineBadge = createAirlineBadge(flight.flightNumber());

        headerPanel.add(flightNumberLabel);
        headerPanel.add(airlineBadge);
        flightInfoPanel.add(headerPanel);

        // Route information
        flightInfoPanel.add(createRoutePanel(flight));

        // Departure time with icon
        flightInfoPanel.add(createDepartureTimePanel(flight));

        FlexPanelH buttonPanel = new FlexPanelH(10, FlexAlignment.CENTER);
        buttonPanel.setBackground(MaterialColors.WHITE);

        // View details button
        Button detailsButton = new Button("View Details",
                MaterialColors.GREY_200, MaterialColors.GREY_800, 12, 6);
        detailsButton.setFont(detailsButton.getFont().deriveFont(11f));
//        detailsButton.addActionListener(e -> showFlightDetails(flight));
        detailsButton.addActionListener(e -> {
            router.navigateTo(LoginView.class, Map.of("id", flight.id()));
        });
        // Quick book button
        Button quickBookButton = new Button("Quick Book",
                MaterialColors.GREEN_500, MaterialColors.WHITE, 12, 6);
        quickBookButton.setFont(quickBookButton.getFont().deriveFont(11f));
//        quickBookButton.addActionListener(e -> quickBookFlight(flight));

        buttonPanel.add(detailsButton);
        buttonPanel.add(quickBookButton);

        // Duration estimate
        flightInfoPanel.add(buttonPanel);

        card.add(flightInfoPanel);
        JPanel tag = new JPanel();
        tag.setPreferredSize(new Dimension(2, 10)); // 1px width, height will auto adjust to match parent
        tag.setBackground(MaterialColors.PURPLE_700); // or any other color you want for the separator

        card.add(tag);
        card.add(flightInfoPanel);
        tag.setPreferredSize(new Dimension(2, card.getPreferredSize().height));
        return card;
    }

    private JPanel createRoutePanel(FlightData flight) {
        FlexPanelH routePanel = new FlexPanelH(8, FlexAlignment.LEFT);
        routePanel.setBackground(MaterialColors.WHITE);

        // Departure city
        JLabel departureLabel = new JLabel(flight.departureCity());
        departureLabel.setFont(departureLabel.getFont().deriveFont(Font.BOLD, 14));
        departureLabel.setForeground(MaterialColors.BLUE_800);

        // Arrow icon
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(arrowLabel.getFont().deriveFont(Font.BOLD, 16));
        arrowLabel.setForeground(MaterialColors.GREY_600);

        // Destination city
        JLabel destinationLabel = new JLabel(flight.destinationCity());
        destinationLabel.setFont(destinationLabel.getFont().deriveFont(Font.BOLD, 14));
        destinationLabel.setForeground(MaterialColors.GREEN_800);

        routePanel.add(departureLabel);
        routePanel.add(arrowLabel);
        routePanel.add(destinationLabel);

        return routePanel;
    }


    private JPanel createDepartureTimePanel(FlightData flight) {
        FlexPanelH timePanel = new FlexPanelH(5, FlexAlignment.LEFT);
        timePanel.setBackground(MaterialColors.WHITE);

        // Calendar icon
        ImageIcon calendarIcon = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/icons/ic_calendar_days.png"))
        );

        JLabel calendar = new JLabel("🕑");
        calendar.setFont(calendar.getFont().deriveFont(12f));

        // Formatted date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        JLabel dateLabel = new JLabel(flight.departureDateTime().format(dateFormatter));
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD, 13));
        dateLabel.setForeground(MaterialColors.GREY_800);

        JLabel timeLabel = new JLabel(flight.departureDateTime().format(timeFormatter));
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD, 13));
        timeLabel.setForeground(MaterialColors.BLUE_600);

        timeLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        timePanel.add(calendar);
        timePanel.add(dateLabel);
        timePanel.add(new JLabel("at"));
        timePanel.add(timeLabel);

        return timePanel;
    }

    private String extractAirlineFromNumber(String flightNumber) {
        // Extract airline code from flight number (first 2-3 characters)
        if (flightNumber.length() >= 2) {
            String code = flightNumber.substring(0, 2);
            return switch (code) {
                case "AA" -> "American";
                case "DL" -> "Delta";
                case "UA" -> "United";
                case "LH" -> "Lufthansa";
                case "BA" -> "British Airways";
                default -> "Airline";
            };
        }
        return "Airline";
    }

    private JLabel createAirlineBadge(String flightNumber) {
        String airline = extractAirlineFromNumber(flightNumber);
        JLabel badge = new JLabel(airline);
        badge.setFont(badge.getFont().deriveFont(Font.PLAIN, 11));
        badge.setForeground(MaterialColors.WHITE);
        badge.setOpaque(true);
        badge.setBackground(MaterialColors.PURPLE_400);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        return badge;
    }

    private void clearFromHandler(ActionEvent e) {
        this.vm.getClearFormCommand().execute(null);
    }

    private void searchButtonHandler(ActionEvent e) {
        this.vm.departureCity.set(this.departureInput.getText());
        this.vm.arrivalCity.set(this.arrivalInput.getText());
        this.vm.departureDate.set(this.departureDateInput.getText());
        this.vm.arrivalDate.set(this.returnDateInput.getText());
        this.vm.passengerCount.set((int) this.passengersSpinner.getValue());
        this.vm.getSearchCommand().execute(null);
    }
}