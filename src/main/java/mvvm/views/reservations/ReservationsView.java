package mvvm.views.reservations;

import core.abstraction.ViewBase;
import core.navigation.Router;
import mvvm.models.*;
import core.mvvm.View;
import mvvm.views.flight.FlightSearchView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationsView extends ViewBase {
    // Mock data
    private List<Reservation> reservations = new ArrayList<>();
    private Reservation selectedReservation = null;

    // Main components
    private JTabbedPane tabbedPane;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;

    // Filter components
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JButton applyFiltersButton;
    private JButton clearFiltersButton;
    private JButton refreshButton;

    // Details components
    private JLabel reservationIdLabel;
    private JLabel flightInfoLabel;
    private JLabel passengerLabel;
    private JLabel seatLabel;
    private JLabel statusLabel;
    private JLabel priceLabel;
    private JLabel createdLabel;
    private JLabel departureLabel;

    // Action buttons
    private JButton cancelButton;
    private JButton checkInButton;
    private JButton viewFlightButton;
    private JButton printButton;
    private JButton backButton;

    // Statistics labels
    private JLabel totalReservationsLabel;
    private JLabel activeReservationsLabel;
    private JLabel totalAmountLabel;
    private JLabel upcomingLabel;
    private JLabel recentLabel;

    // Status labels
    private JLabel errorLabel;
    private JLabel successLabel;
    private JLabel infoLabel;

    // Formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Mock data generators
    private final Random random = new Random();
    private final Router router;

    public ReservationsView(Router router) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.router = router;
        initComponents();
        layoutComponents();

        // Generate and load mock data
        generateMockData();
        loadReservations();
    }

    @Override
    public void initComponents() {
        // Table model
        String[] columns = {"ID", "Flight", "Route", "Departure", "Seat", "Class", "Status", "Price", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only actions column is editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 8) return JButton.class;
                return String.class;
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setRowHeight(40);
        reservationsTable.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        // Configure table columns
        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        reservationsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        reservationsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        reservationsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        reservationsTable.getColumnModel().getColumn(8).setPreferredWidth(200);

        // Status cell renderer for colors
        reservationsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Actions column renderer and editor
        reservationsTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Filter components
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by flight number, route, or seat");

        statusFilter = new JComboBox<>(new String[]{"All Status", "CONFIRMED", "CANCELLED", "PENDING", "CHECKED_IN"});

        startDateField = new JFormattedTextField(dateFormatter.toFormat());
        startDateField.setColumns(10);
        startDateField.setToolTipText("Start date");

        endDateField = new JFormattedTextField(dateFormatter.toFormat());
        endDateField.setColumns(10);
        endDateField.setToolTipText("End date");

        applyFiltersButton = new JButton("Apply Filters");
        applyFiltersButton.addActionListener(this::onApplyFilters);

        clearFiltersButton = new JButton("Clear Filters");
        clearFiltersButton.addActionListener(e -> onClearFilters());

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadReservations());

        // Details components
        reservationIdLabel = createDetailLabel("");
        flightInfoLabel = createDetailLabel("");
        passengerLabel = createDetailLabel("");
        seatLabel = createDetailLabel("");
        statusLabel = createDetailLabel("");
        priceLabel = createDetailLabel("");
        createdLabel = createDetailLabel("");
        departureLabel = createDetailLabel("");

        // Action buttons
        cancelButton = new JButton("Cancel Reservation");
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(this::onCancelReservation);

        checkInButton = new JButton("Check In");
        checkInButton.setBackground(new Color(30, 144, 255));
        checkInButton.setForeground(Color.WHITE);
        checkInButton.addActionListener(this::onCheckIn);

        viewFlightButton = new JButton("View Flight Details");
        viewFlightButton.addActionListener(this::onViewFlightDetails);

        printButton = new JButton("Print Ticket");
        printButton.addActionListener(this::onPrintTicket);

        backButton = new JButton("Back back");
        backButton.addActionListener(e -> {
            // In real app, would navigate back
            this.router.navigateTo(FlightSearchView.class);
        });

        // Statistics labels
        totalReservationsLabel = createStatLabel("0");
        activeReservationsLabel = createStatLabel("0");
        totalAmountLabel = createStatLabel("$0.00");
        upcomingLabel = createStatLabel("0");
        recentLabel = createStatLabel("0");

        // Status labels
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        successLabel = new JLabel();
        successLabel.setForeground(new Color(0, 128, 0));
        successLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoLabel = new JLabel();
        infoLabel.setForeground(new Color(0, 0, 139));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Tabbed pane
        tabbedPane = new JTabbedPane();
    }

    @Override
    public void bind() {

    }

    private void generateMockData() {
        reservations.clear();

        // Create mock flights
        List<Flight> flights = Arrays.asList(
                createMockFlight(1, "AA123", "New York", "Los Angeles",
                        LocalDateTime.now().plusDays(1).withHour(8).withMinute(0)),
                createMockFlight(2, "BA456", "London", "Paris",
                        LocalDateTime.now().plusDays(2).withHour(14).withMinute(30)),
                createMockFlight(3, "DL789", "Atlanta", "Miami",
                        LocalDateTime.now().minusDays(2).withHour(18).withMinute(45)),
                createMockFlight(4, "UA101", "Chicago", "Denver",
                        LocalDateTime.now().plusDays(7).withHour(9).withMinute(15)),
                createMockFlight(5, "EK202", "Dubai", "Singapore",
                        LocalDateTime.now().plusDays(3).withHour(23).withMinute(30))
        );

        // Create mock clients
        List<Client> clients = Arrays.asList(
                createMockClient(1, "John", "Doe", "john.doe@email.com"),
                createMockClient(2, "Jane", "Smith", "jane.smith@email.com"),
                createMockClient(3, "Robert", "Johnson", "robert.j@email.com"),
                createMockClient(4, "Emily", "Williams", "emily.w@email.com")
        );

        // Create mock seats
        List<Seat> seats = Arrays.asList(
                createMockSeat(1, "12A", SeatClass.ECONOMY),
                createMockSeat(2, "15C", SeatClass.ECONOMY),
                createMockSeat(3, "8B", SeatClass.BUSINESS),
                createMockSeat(4, "1A", SeatClass.BUSINESS),
                createMockSeat(5, "22F", SeatClass.ECONOMY)
        );

        // Create mock reservations
        reservations.add(new Reservation(1001, clients.get(0), flights.get(0), seats.get(0),
                ReservationStatus.CONFIRMED, LocalDateTime.now().minusDays(5)));
        reservations.add(new Reservation(1002, clients.get(1), flights.get(1), seats.get(1),
                ReservationStatus.CONFIRMED, LocalDateTime.now().minusDays(3)));
        reservations.add(new Reservation(1003, clients.get(2), flights.get(2), seats.get(2),
                ReservationStatus.CANCELLED, LocalDateTime.now().minusDays(10)));
        reservations.add(new Reservation(1004, clients.get(3), flights.get(3), seats.get(3),
                ReservationStatus.CONFIRMED, LocalDateTime.now().minusDays(1)));
        reservations.add(new Reservation(1005, clients.get(0), flights.get(4), seats.get(4),
                ReservationStatus.CONFIRMED, LocalDateTime.now().minusDays(2)));
    }

    private Flight createMockFlight(int id, String number, String from, String to, LocalDateTime departure) {
        return new Flight(id, number, from, to, departure,
                new AirCraft(1, "Boeing 737", "B737", 170, 120, 50));
    }

    private Client createMockClient(int id, String firstName, String lastName, String email) {
        return new Client(id, firstName, lastName, email,
                "+1-555-123-4567", "P12345678", LocalDate.of(1985, 5, 15), 1);
    }

    private Seat createMockSeat(int id, String seatNumber, SeatClass seatClass) {
        return new Seat(id, seatNumber, seatClass,
                SeatStatus.FREE, new AirCraft(1, "Boeing 737", "B737", 150, 120, 70));
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return label;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(0, 0, 139));
        return label;
    }

    private void layoutComponents() {
        // Main layout with west panel for filters, center for content
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(250);
        mainSplitPane.setResizeWeight(0);

        // Left panel - Filters
        mainSplitPane.setLeftComponent(createFiltersPanel());

        // Center panel - Content
        mainSplitPane.setRightComponent(createContentPanel());

        // Status panel at bottom
        JPanel statusPanel = new JPanel(new BorderLayout());
        JPanel messagePanel = new JPanel(new GridLayout(3, 1));
        messagePanel.add(errorLabel);
        messagePanel.add(successLabel);
        messagePanel.add(infoLabel);
        statusPanel.add(messagePanel, BorderLayout.CENTER);

        // Main layout
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        add(createBottomPanel(), BorderLayout.PAGE_END);
    }

    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Filters & Statistics",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("Search:"), BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Status filter
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(new JLabel("Status:"), BorderLayout.NORTH);
        statusPanel.add(statusFilter, BorderLayout.CENTER);

        // Date range
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDateField);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDateField);

        // Filter buttons
        JPanel filterButtonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        filterButtonsPanel.add(applyFiltersButton);
        filterButtonsPanel.add(clearFiltersButton);

        // Statistics panel
        JPanel statsPanel = createStatisticsPanel();

        // Add all components
        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(filterButtonsPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(statsPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        panel.add(new JLabel("Total Reservations:"));
        panel.add(totalReservationsLabel);
        panel.add(new JLabel("Active Reservations:"));
        panel.add(activeReservationsLabel);
        panel.add(new JLabel("Total Amount:"));
        panel.add(totalAmountLabel);
        panel.add(new JLabel("Upcoming (7 days):"));
        panel.add(upcomingLabel);
        panel.add(new JLabel("Recent (30 days):"));
        panel.add(recentLabel);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabbed pane for different views
        tabbedPane.addTab("All Reservations", createTablePanel());
        tabbedPane.addTab("Upcoming", createUpcomingPanel());
        tabbedPane.addTab("Details", createDetailsPanel());
        tabbedPane.addTab("Grouped by Flight", createGroupedPanel());

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUpcomingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Will be populated dynamically
        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Reservation Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Details rows
        addDetailRow(panel, gbc, "Reservation ID:", reservationIdLabel, 1);
        addDetailRow(panel, gbc, "Flight:", flightInfoLabel, 2);
        addDetailRow(panel, gbc, "Passenger:", passengerLabel, 3);
        addDetailRow(panel, gbc, "Seat:", seatLabel, 4);
        addDetailRow(panel, gbc, "Status:", statusLabel, 5);
        addDetailRow(panel, gbc, "Price:", priceLabel, 6);
        addDetailRow(panel, gbc, "Created:", createdLabel, 7);
        addDetailRow(panel, gbc, "Departure:", departureLabel, 8);

        // Action buttons
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(viewFlightButton);
        buttonPanel.add(printButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, JComponent value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label + " "), gbc);

        gbc.gridx = 1;
        panel.add(value, gbc);
    }

    private JPanel createGroupedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(refreshButton);
        panel.add(backButton);
        return panel;
    }

    private void loadReservations() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate loading delay
                Thread.sleep(500);
                return null;
            }

            @Override
            protected void done() {
                updateReservationsTable();
                updateStatistics();
                updateDetailsPanel();
                updateUpcomingPanel();
                updateGroupedPanel();
                successLabel.setText("Reservations loaded successfully");
            }
        };
        worker.execute();
    }

    private void updateReservationsTable() {
        tableModel.setRowCount(0);

        for (Reservation reservation : reservations) {
            Flight flight = reservation.getFlight();
            Seat seat = reservation.getSeat();
            Client client = reservation.getClient();

            Object[] row = new Object[9];
            row[0] = reservation.getId();
            row[1] = flight != null ? flight.getFlightNumber() : "N/A";
            row[2] = flight != null ?
                    flight.getDepartureCity() + " → " + flight.getDestinationCity() : "N/A";
            row[3] = flight != null ?
                    formatDateTime(flight.getDepartureDateTime()) : "N/A";
            row[4] = seat != null ? seat.getSeatNumber() : "N/A";
            row[5] = seat != null ? seat.getSeatClass() : "N/A";
            row[6] = reservation.getStatus();
            row[7] = String.format("$%.2f", calculateReservationPrice(reservation));
            row[8] = "Actions";

            tableModel.addRow(row);
        }
    }

    private void updateStatistics() {
        long total = reservations.size();
        long active = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        double totalAmount = reservations.stream()
                .mapToDouble(this::calculateReservationPrice)
                .sum();

        long upcoming = reservations.stream()
                .filter(r -> r.getFlight() != null &&
                        r.getFlight().getDepartureDateTime().isAfter(LocalDateTime.now()) &&
                        r.getFlight().getDepartureDateTime().isBefore(LocalDateTime.now().plusDays(7)))
                .count();

        long recent = reservations.stream()
                .filter(r -> r.getCreatedAt() != null &&
                        r.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .count();

        totalReservationsLabel.setText(String.valueOf(total));
        activeReservationsLabel.setText(String.valueOf(active));
        totalAmountLabel.setText(String.format("$%.2f", totalAmount));
        upcomingLabel.setText(String.valueOf(upcoming));
        recentLabel.setText(String.valueOf(recent));
    }

    private void updateDetailsPanel() {
        if (selectedReservation != null) {
            reservationIdLabel.setText(String.valueOf(selectedReservation.getId()));

            if (selectedReservation.getFlight() != null) {
                Flight flight = selectedReservation.getFlight();
                flightInfoLabel.setText(flight.getFlightNumber() + " - " +
                        flight.getDepartureCity() + " to " + flight.getDestinationCity());
                departureLabel.setText(formatDateTime(flight.getDepartureDateTime()));
            } else {
                flightInfoLabel.setText("N/A");
                departureLabel.setText("N/A");
            }

            if (selectedReservation.getClient() != null) {
                passengerLabel.setText(selectedReservation.getClient().getFirstName() + " " +
                        selectedReservation.getClient().getLastName());
            } else {
                passengerLabel.setText("N/A");
            }

            if (selectedReservation.getSeat() != null) {
                seatLabel.setText(selectedReservation.getSeat().getSeatNumber() + " (" +
                        selectedReservation.getSeat().getSeatClass() + ")");
            } else {
                seatLabel.setText("N/A");
            }

            statusLabel.setText(getStatusText(selectedReservation.getStatus()));
            statusLabel.setForeground(getStatusColor(selectedReservation.getStatus()));

            priceLabel.setText(String.format("$%.2f", calculateReservationPrice(selectedReservation)));
            createdLabel.setText(formatDateTime(selectedReservation.getCreatedAt()));

            // Enable/disable action buttons
            cancelButton.setEnabled(canCancelReservation(selectedReservation));
            checkInButton.setEnabled(canCheckInReservation(selectedReservation));
            viewFlightButton.setEnabled(selectedReservation.getFlight() != null);
            printButton.setEnabled(selectedReservation.getStatus() != ReservationStatus.CANCELLED);
        } else {
            // Clear details
            reservationIdLabel.setText("");
            flightInfoLabel.setText("");
            passengerLabel.setText("");
            seatLabel.setText("");
            statusLabel.setText("");
            priceLabel.setText("");
            createdLabel.setText("");
            departureLabel.setText("");

            // Disable action buttons
            cancelButton.setEnabled(false);
            checkInButton.setEnabled(false);
            viewFlightButton.setEnabled(false);
            printButton.setEnabled(false);
        }
    }

    private void updateUpcomingPanel() {
        JPanel panel = (JPanel) tabbedPane.getComponentAt(1);
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        List<Reservation> upcoming = reservations.stream()
                .filter(r -> r.getFlight() != null &&
                        r.getFlight().getDepartureDateTime().isAfter(LocalDateTime.now()))
                .sorted((r1, r2) -> r1.getFlight().getDepartureDateTime()
                        .compareTo(r2.getFlight().getDepartureDateTime()))
                .collect(Collectors.toList());

        if (upcoming.isEmpty()) {
            panel.add(new JLabel("No upcoming reservations."));
        } else {
            for (Reservation reservation : upcoming) {
                panel.add(createReservationCard(reservation));
                panel.add(Box.createVerticalStrut(10));
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private void updateGroupedPanel() {
        JPanel panel = (JPanel) tabbedPane.getComponentAt(3);
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Group reservations by flight
        Map<Flight, List<Reservation>> grouped = reservations.stream()
                .filter(r -> r.getFlight() != null)
                .collect(Collectors.groupingBy(Reservation::getFlight));

        List<ReservationGroup> groups = grouped.entrySet().stream()
                .map(entry -> new ReservationGroup(entry.getKey(), entry.getValue()))
                .sorted((g1, g2) -> g1.getFlight().getDepartureDateTime()
                        .compareTo(g2.getFlight().getDepartureDateTime()))
                .collect(Collectors.toList());

        if (groups.isEmpty()) {
            panel.add(new JLabel("No reservations to group."));
        } else {
            for (ReservationGroup group : groups) {
                panel.add(createFlightGroupCard(group));
                panel.add(Box.createVerticalStrut(15));
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createReservationCard(Reservation reservation) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        Flight flight = reservation.getFlight();
        Seat seat = reservation.getSeat();

        // Left side - Flight info
        JPanel leftPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        leftPanel.setBackground(Color.WHITE);

        leftPanel.add(new JLabel("Flight: " + flight.getFlightNumber()));
        leftPanel.add(new JLabel("Route: " + flight.getDepartureCity() + " → " + flight.getDestinationCity()));
        leftPanel.add(new JLabel("Departure: " + formatDateTime(flight.getDepartureDateTime())));
        leftPanel.add(new JLabel("Seat: " + seat.getSeatNumber() + " (" + seat.getSeatClass() + ")"));

        // Right side - Status and actions
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel(getStatusText(ReservationStatus.valueOf(reservation.getStatus().name())));
//        statusLabel.setForeground(getStatusColor(reservation.getStatus()));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton checkInBtn = new JButton("Check In");
        checkInBtn.setEnabled(canCheckInReservation(reservation));
        checkInBtn.addActionListener(e -> onCheckInReservation(reservation.getId()));
        buttonPanel.add(checkInBtn);

        JButton detailsBtn = new JButton("Details");
        detailsBtn.addActionListener(e -> selectReservation(reservation));
        buttonPanel.add(detailsBtn);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createFlightGroupCard(ReservationGroup group) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        Flight flight = group.getFlight();

        // Flight header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel flightLabel = new JLabel(flight.getFlightNumber() + " - " +
                flight.getDepartureCity() + " to " + flight.getDestinationCity());
        flightLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(flightLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel("Departure: " + formatDateTime(flight.getDepartureDateTime()));
        headerPanel.add(dateLabel, BorderLayout.EAST);

        // Passengers list
        JPanel passengersPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        passengersPanel.setBackground(Color.WHITE);
        passengersPanel.setBorder(BorderFactory.createTitledBorder("Passengers (" + group.getReservations().size() + ")"));

        for (Reservation reservation : group.getReservations()) {
            Seat seat = reservation.getSeat();
            Client client = reservation.getClient();
            String status = getStatusText(reservation.getStatus());

            JLabel passengerLabel = new JLabel(
                    client.getFirstName() + " " + client.getLastName() +
                            " - Seat: " + seat.getSeatNumber() +
                            " (" + seat.getSeatClass() + ") - Status: " + status
            );
            passengerLabel.setForeground(getStatusColor(reservation.getStatus()));
            passengersPanel.add(passengerLabel);
        }

        // Footer - Total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        double totalAmount = group.getReservations().stream()
                .mapToDouble(this::calculateReservationPrice)
                .sum();
        footerPanel.add(new JLabel("Total: $" + String.format("%.2f", totalAmount)));

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(passengersPanel), BorderLayout.CENTER);
        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    private void onRowSelected() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int reservationId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

            selectedReservation = reservations.stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);

            updateDetailsPanel();

            // Switch to details tab
            tabbedPane.setSelectedIndex(2);
        }
    }

    private void selectReservation(Reservation reservation) {
        selectedReservation = reservation;
        updateDetailsPanel();
        tabbedPane.setSelectedIndex(2);
    }

    private void onApplyFilters(ActionEvent e) {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        // Filter reservations
        List<Reservation> filtered = reservations.stream()
                .filter(r -> {
                    // Search filter
                    if (!searchText.isEmpty()) {
                        boolean matches = false;
                        if (r.getFlight() != null) {
                            matches = r.getFlight().getFlightNumber().toLowerCase().contains(searchText) ||
                                    r.getFlight().getDepartureCity().toLowerCase().contains(searchText) ||
                                    r.getFlight().getDestinationCity().toLowerCase().contains(searchText);
                        }
                        if (r.getSeat() != null) {
                            matches = matches || r.getSeat().getSeatNumber().toLowerCase().contains(searchText);
                        }
                        if (!matches) return false;
                    }

                    // Status filter
                    if (!"All Status".equals(selectedStatus)) {
                        return r.getStatus().name().equals(selectedStatus);
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // Update table with filtered results
        tableModel.setRowCount(0);
        for (Reservation reservation : filtered) {
            Flight flight = reservation.getFlight();
            Seat seat = reservation.getSeat();

            Object[] row = new Object[9];
            row[0] = reservation.getId();
            row[1] = flight != null ? flight.getFlightNumber() : "N/A";
            row[2] = flight != null ?
                    flight.getDepartureCity() + " → " + flight.getDestinationCity() : "N/A";
            row[3] = flight != null ?
                    formatDateTime(flight.getDepartureDateTime()) : "N/A";
            row[4] = seat != null ? seat.getSeatNumber() : "N/A";
            row[5] = seat != null ? seat.getSeatClass() : "N/A";
            row[6] = reservation.getStatus();
            row[7] = String.format("$%.2f", calculateReservationPrice(reservation));
            row[8] = "Actions";

            tableModel.addRow(row);
        }

        infoLabel.setText("Found " + filtered.size() + " reservations");
    }

    private void onClearFilters() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        startDateField.setValue(null);
        endDateField.setValue(null);

        loadReservations();
        infoLabel.setText("Filters cleared");
    }

    private void onCancelReservation(ActionEvent e) {
        if (selectedReservation == null) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel reservation #" + selectedReservation.getId() + "?\n" +
                        "Cancellation fees may apply.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
//            selectedReservation.setStatus(ReservationStatus.CANCELLED);
            loadReservations();
            successLabel.setText("Reservation #" + selectedReservation.getId() + " has been cancelled.");
        }
    }

    private void onCheckIn(ActionEvent e) {
        if (selectedReservation == null) return;
        onCheckInReservation(selectedReservation.getId());
    }

    private void onCheckInReservation(int reservationId) {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);

        if (reservation != null) {
//            reservation.setStatus(ReservationStatus.CHECKED_IN);
            loadReservations();
            successLabel.setText("Checked in for reservation #" + reservationId);

            // Show boarding pass
            showBoardingPass(reservationId);
        }
    }

    private void showBoardingPass(int reservationId) {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);

        if (reservation != null) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Boarding Pass", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(this);

            JPanel boardingPass = createBoardingPassPanel(reservation);
            dialog.add(boardingPass, BorderLayout.CENTER);

            JButton printBtn = new JButton("Print");
            printBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(dialog,
                        "Printing boarding pass for reservation #" + reservationId,
                        "Print", JOptionPane.INFORMATION_MESSAGE);
            });

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(printBtn);
            buttonPanel.add(closeBtn);

            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    }

    private JPanel createBoardingPassPanel(Reservation reservation) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel header = new JLabel("BOARDING PASS", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(Color.BLUE);
        panel.add(header, gbc);

        // Flight info
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Flight:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(reservation.getFlight().getFlightNumber()), gbc);

        // Route
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(reservation.getFlight().getDepartureCity() + " → " +
                reservation.getFlight().getDestinationCity()), gbc);

        // Departure
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(formatDateTime(reservation.getFlight().getDepartureDateTime())), gbc);

        // Passenger
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Passenger:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(reservation.getClient().getFirstName() + " " +
                reservation.getClient().getLastName()), gbc);

        // Seat
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Seat:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(reservation.getSeat().getSeatNumber() + " (" +
                reservation.getSeat().getSeatClass() + ")"), gbc);

        // Gate (mock)
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Gate:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("A12"), gbc);

        // Boarding time (mock - 40 min before departure)
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Boarding Time:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(formatDateTime(
                reservation.getFlight().getDepartureDateTime().minusMinutes(40))), gbc);

        // Barcode (mock)
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel barcode = new JLabel("✈️ [BARCODE] ✈️");
        barcode.setFont(new Font("Monospaced", Font.BOLD, 20));
        barcode.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(barcode, gbc);

        return panel;
    }

    private void onViewFlightDetails(ActionEvent e) {
        if (selectedReservation == null || selectedReservation.getFlight() == null) return;

        JOptionPane.showMessageDialog(this,
                "Flight Details:\n" +
                        "Flight: " + selectedReservation.getFlight().getFlightNumber() + "\n" +
                        "Route: " + selectedReservation.getFlight().getDepartureCity() + " → " +
                        selectedReservation.getFlight().getDestinationCity() + "\n" +
                        "Departure: " + formatDateTime(selectedReservation.getFlight().getDepartureDateTime()) + "\n" +
                        "Aircraft: " + selectedReservation.getFlight().getAirCraft().getModel(),
                "Flight Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onPrintTicket(ActionEvent e) {
        if (selectedReservation == null) return;

        JOptionPane.showMessageDialog(this,
                "Printing ticket for reservation #" + selectedReservation.getId() + "\n" +
                        "Flight: " + selectedReservation.getFlight().getFlightNumber() + "\n" +
                        "Passenger: " + selectedReservation.getClient().getFirstName() + " " +
                        selectedReservation.getClient().getLastName() + "\n" +
                        "Seat: " + selectedReservation.getSeat().getSeatNumber(),
                "Print Ticket",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper methods (replacing ViewModel functionality)
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(dateTimeFormatter) : "N/A";
    }

    private double calculateReservationPrice(Reservation reservation) {
        // Mock price calculation
        double basePrice = 299.99;
        SeatClass seatClass = reservation.getSeat().getSeatClass();

        switch (seatClass) {
            case ECONOMY:
                return basePrice * 3.0;
            case BUSINESS:
                return basePrice * 2.0;
            default:
                return basePrice;
        }
    }

    private String getStatusText(ReservationStatus status) {
        switch (status) {
            case CONFIRMED:
                return "Confirmed";
            case CANCELLED:
                return "Cancelled";
            default:
                return status.name();
        }
    }

    private Color getStatusColor(ReservationStatus status) {
        switch (status) {
            case CONFIRMED:
                return new Color(0, 128, 0); // Green
            case CANCELLED:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    private boolean canCancelReservation(Reservation reservation) {
        return reservation.getStatus().equals(ReservationStatus.CONFIRMED) ||
                reservation.getStatus().equals(ReservationStatus.CANCELLED);
    }

    private boolean canCheckInReservation(Reservation reservation) {
        if (reservation.getFlight() == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = reservation.getFlight().getDepartureDateTime();

        return reservation.getStatus().equals(ReservationStatus.CONFIRMED) && departure != null &&
                departure.isAfter(now) &&
                departure.minusDays(1).isBefore(now);
    }

    // Custom cell renderer for status
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            if (value instanceof ReservationStatus) {
                ReservationStatus status = (ReservationStatus) value;
                setText(getStatusText(status));
                setForeground(getStatusColor(status));
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));
            }

            return c;
        }
    }

    // Button renderer for action column
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // Button editor for action column
    private class ButtonEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {
        private JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                onActionButtonClicked(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private void onActionButtonClicked(int row) {
        int reservationId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        Reservation reservation = reservations.stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);

        if (reservation != null) {
            // Show action menu
            JPopupMenu popup = new JPopupMenu();

            JMenuItem viewItem = new JMenuItem("View Details");
            viewItem.addActionListener(e -> selectReservation(reservation));
            popup.add(viewItem);

            if (canCancelReservation(reservation)) {
                JMenuItem cancelItem = new JMenuItem("Cancel Reservation");
                cancelItem.addActionListener(e -> {
                    selectedReservation = reservation;
                    onCancelReservation(null);
                });
                popup.add(cancelItem);
            }

            if (canCheckInReservation(reservation)) {
                JMenuItem checkInItem = new JMenuItem("Check In");
                checkInItem.addActionListener(e -> onCheckInReservation(reservationId));
                popup.add(checkInItem);
            }

            JMenuItem printItem = new JMenuItem("Print Ticket");
            printItem.addActionListener(e -> {
                selectedReservation = reservation;
                onPrintTicket(null);
            });
            popup.add(printItem);

            // Show popup near the button
            Rectangle cellRect = reservationsTable.getCellRect(row, 8, true);
            popup.show(reservationsTable, cellRect.x, cellRect.y + cellRect.height);
        }
    }

    // Helper class for grouping reservations
    private class ReservationGroup {
        private Flight flight;
        private List<Reservation> reservations;

        public ReservationGroup(Flight flight, List<Reservation> reservations) {
            this.flight = flight;
            this.reservations = reservations;
        }

        public Flight getFlight() {
            return flight;
        }

        public List<Reservation> getReservations() {
            return reservations;
        }
    }
}