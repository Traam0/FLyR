package mvvm.views.flight;

import core.abstraction.ProtectedView;
import core.abstraction.ViewBase;
import mvvm.models.SeatClass;
import mvvm.models.SeatStatus;
import mvvm.viewModels.FlightDetailViewModel;
import core.navigation.Router;
import shared.common.FlexAlignment;
import shared.common.MaterialColors;
import shared.components.Button;
import shared.components.Label;
import shared.components.TextInput;
import shared.components.TopBar;
import shared.layouts.FlexPanelH;
import shared.layouts.FlexPanelV;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class FlightDetailView extends ProtectedView {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FlightDetailViewModel vm;
    private final Logger logger;
    private FlexPanelV mainPanel;
    private JPanel seatsPanel = new JPanel(new GridLayout(0, 6, 10, 10));

    public FlightDetailView(FlightDetailViewModel viewModel, Logger logger, Router router) {
        super(router);
        this.vm = viewModel;
        this.logger = logger;

        this.initComponents();
        this.bind();

        this.vm.loadData();

    }

    @Override
    public void initComponents() {
        this.setLayout(new BorderLayout());
        this.mainPanel = new FlexPanelV(10, FlexAlignment.TOP, MaterialColors.WHITE);
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.mainPanel.add(TopBar.build(router, logger));
        var informationPanel = this.createInformationPanel();
        this.mainPanel.add(informationPanel);
        var seatsPanel = this.createSeatsPanel();
        var clientPanel = this.createClientPanel();

        var bottomPanel = new FlexPanelH(15, FlexAlignment.LEFT);
        bottomPanel.add(seatsPanel);
        bottomPanel.add(clientPanel);

        this.mainPanel.add(bottomPanel);
        this.add(this.mainPanel);
    }

    @Override
    public void bind() {
        logger.info("Binding FlightDetailView");
        this.vm.flight.subscribe((o, n) -> {
            logger.info("saving data (bound)");
            switch (n.getStatus()) {
                case SUCCESS:
                    var data = n.getData();
                    logger.info("seats in flight.aircraft property " + data.getAirCraft().getSeats().size());

                    this.lFlightNumber.setText(data.getFlightNumber());
                    this.lRoute.setText(String.format("%s → %s", data.getDepartureCity(), data.getDestinationCity()));
                    this.lDepartureDate.setText(this.dateFormatter.format(data.getDepartureDateTime()));
                    this.lAirCraft.setText(String.format("%s (%s)", data.getAirCraft().getModel(), data.getAirCraft().getCode()));
                    this.lCapacity.setText(String.format("Total %d (Business: %d, Economy: %d)", data.getAirCraft().getTotalCapacity(), data.getAirCraft().getBusinessCapacity(), data.getAirCraft().getEconomyCapacity()));

                    this.createSeatButtons();
                    break;
                case LOADING:
                    this.lFlightNumber.setText("still loading");
                    break;
                case ERROR:
                    this.lFlightNumber.setText(n.getMessage());
                    break;
            }
        });

        this.vm.selectedSeatClass.subscribe((o, n) -> {
            this.seatsPanel.removeAll();
            this.createSeatButtons();
        });

        this.vm.selectedSeats.subscribe((o, n) -> {
            this.seatsPanel.removeAll();
            this.createSeatButtons();
            this.lSelectedSeats.setText(String.valueOf(n.size()));
            this.ltotalPrice.setText(String.format("DH %d", n.stream().mapToInt(seat -> seat.getSeatClass() == SeatClass.BUSINESS ? 500 : 300).reduce(0, Integer::sum)));
        });
    }

    private FlexPanelV createInformationPanel() {
        var panel = new FlexPanelV(10, FlexAlignment.TOP, MaterialColors.WHITE);
        var pFlightInfo = new FlexPanelV(5, FlexAlignment.TOP, MaterialColors.PURPLE_50);
        pFlightInfo.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Flight's Information",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLACK
        ), new EmptyBorder(10, 15, 10, 15)));

        var pFlightNumber = new FlexPanelH(10, FlexAlignment.LEFT);
        pFlightNumber.setOpaque(false);
        pFlightNumber.add(new Label("Flight Number: ", MaterialColors.BLACK));
        pFlightNumber.add(this.lFlightNumber);
        pFlightInfo.add(pFlightNumber);
        var pRoute = new FlexPanelH(10, FlexAlignment.LEFT);
        pRoute.setOpaque(false);
        pRoute.add(new Label("Route: ", MaterialColors.BLACK));
        pRoute.add(this.lRoute);
        pFlightInfo.add(pRoute);
        var pDepartureDate = new FlexPanelH(10, FlexAlignment.LEFT);
        pDepartureDate.setOpaque(false);
        pDepartureDate.add(new Label("Departure Date: ", MaterialColors.BLACK));
        pDepartureDate.add(this.lDepartureDate);
        pFlightInfo.add(pDepartureDate);
        var pAirCraft = new FlexPanelH(10, FlexAlignment.LEFT);
        pAirCraft.setOpaque(false);
        pAirCraft.add(new Label("Air Craft: ", MaterialColors.BLACK));
        pAirCraft.add(this.lAirCraft);
        pFlightInfo.add(pAirCraft);
        var pCapacity = new FlexPanelH(10, FlexAlignment.LEFT);
        pCapacity.setOpaque(false);
        pCapacity.add(new Label("Capacity: ", MaterialColors.BLACK));
        pCapacity.add(this.lCapacity);
        pFlightInfo.add(pCapacity);

        panel.add(pFlightInfo);

        var pOverview = new FlexPanelV(10, FlexAlignment.TOP, MaterialColors.DEEP_PURPLE_50);
        pOverview.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Overview",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLACK
        ), new EmptyBorder(10, 15, 10, 15)));
        var pSeatClass = new FlexPanelH(10, FlexAlignment.LEFT);
        pSeatClass.setOpaque(false);
        pSeatClass.add(new Label("Seat Class: ", MaterialColors.BLACK));
        pSeatClass.add(this.iSeatClass);
        this.iSeatClass.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    FlightDetailView.this.vm.selectedSeatClass.set(String.valueOf(e.getItem()));
                }
            }
        });
        pOverview.add(pSeatClass);
        var pSelectedSeats = new FlexPanelH(10, FlexAlignment.LEFT);
        pSelectedSeats.setOpaque(false);
        pSelectedSeats.add(new Label("Selected Seats: ", MaterialColors.BLACK));
        pSelectedSeats.add(this.lSelectedSeats);
        pOverview.add(pSelectedSeats);
        var pTotalPrice = new FlexPanelH(10, FlexAlignment.LEFT);
        pTotalPrice.setOpaque(false);
        pTotalPrice.add(new Label("Total Price: ", MaterialColors.BLACK));
        pTotalPrice.add(this.ltotalPrice);
        pOverview.add(pTotalPrice);

        panel.add(pOverview);
        return panel;
    }

    private FlexPanelV createSeatsPanel() {
        var pSeatsContainer = new FlexPanelV(0, FlexAlignment.TOP, MaterialColors.WHITE);
        pSeatsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pSeatsContainer.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Seat Selection",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLACK
        ), new EmptyBorder(10, 15, 10, 15)));


        var scrollPane = new JScrollPane(this.seatsPanel);
        this.seatsPanel.setBackground(MaterialColors.WHITE);
        scrollPane.setBackground(MaterialColors.WHITE);
        scrollPane.setPreferredSize(new Dimension(650, 400));
        pSeatsContainer.add(scrollPane);
        return pSeatsContainer;
    }

    private FlexPanelV createClientPanel() {
        var panel = new FlexPanelV(15, FlexAlignment.TOP, MaterialColors.WHITE);
        //row1
        var pNaming = new FlexPanelH(10, FlexAlignment.CENTER);
        pNaming.setBackground(MaterialColors.WHITE);

        //  FirstName
        FlexPanelV cFirstName = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel lFirstName = new JLabel("First Name:     ");
        lFirstName.setBackground(MaterialColors.WHITE);
        cFirstName.add(lFirstName);
        cFirstName.add(iFirstName);
        iFirstName.setPreferredSize(new Dimension(150, 30));
        pNaming.add(cFirstName);

        //  LastName
        FlexPanelV cLastName = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel lLastName = new JLabel("Last Name:     ");
        lLastName.setBackground(MaterialColors.WHITE);
        cLastName.add(lLastName);
        cLastName.add(iLastName);
        iLastName.setPreferredSize(new Dimension(150, 30));
        pNaming.add(cLastName);
        panel.add(pNaming);

        //row2
        var pContact = new FlexPanelH(10, FlexAlignment.CENTER);
        pContact.setBackground(MaterialColors.WHITE);

        //  Email
        FlexPanelV cEmail = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel lEmail = new JLabel("Email:     ");
        lEmail.setBackground(MaterialColors.WHITE);
        cEmail.add(lEmail);
        cEmail.add(iEmail);
        iEmail.setPreferredSize(new Dimension(150, 30));
        pContact.add(cEmail);

        //  Phone
        FlexPanelV cPhone = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel lPhone = new JLabel("Phone:     ");
        lPhone.setBackground(MaterialColors.WHITE);
        cPhone.add(lPhone);
        cPhone.add(iPhone);
        iPhone.setPreferredSize(new Dimension(150, 30));
        pContact.add(cPhone);
        panel.add(pContact);

        //row3
        var pIdentity = new FlexPanelH(10, FlexAlignment.CENTER);
        pIdentity.setBackground(MaterialColors.WHITE);

        //  BirthDate
        var cBirthDate = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel lBirthDate = new JLabel("Birth Date:     ");
        lBirthDate.setBackground(MaterialColors.WHITE);
        cBirthDate.add(lBirthDate);
        cBirthDate.add(iBirthDate);
        iBirthDate.setPreferredSize(new Dimension(150, 30));
        pIdentity.add(cBirthDate);

        // Passport
        var cPassport = new FlexPanelV(8, FlexAlignment.TOP);
        JLabel lPassport = new JLabel("Passport:     ");
        lPassport.setBackground(MaterialColors.WHITE);
        cPassport.add(lPassport);
        cPassport.add(iPassport);
        iPassport.setPreferredSize(new Dimension(150, 30));
        pIdentity.add(cPassport);
        panel.add(pIdentity);

        // buttons
        var pButtons = new FlexPanelV(10, FlexAlignment.TOP);
        var clearButton = new Button("Clear", MaterialColors.RED_50, MaterialColors.RED_700_ACCENT, 30, 20);
        pButtons.add(clearButton);
        var saveButton = new Button("Book now", MaterialColors.GREEN_600, MaterialColors.WHITE, 30, 20);
        pButtons.add(saveButton);
        panel.add(pButtons);
        return panel;
    }

    private final Label lFlightNumber = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lRoute = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lDepartureDate = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lAirCraft = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lCapacity = new Label("loading ...", MaterialColors.PURPLE_900);
    private final JComboBox<String> iSeatClass = new JComboBox<>(new String[]{"ALL SEATS", SeatClass.BUSINESS.name(), SeatClass.ECONOMY.name()});
    private final Label lSelectedSeats = new Label("0", MaterialColors.PURPLE_900);
    private final Label ltotalPrice = new Label("0", MaterialColors.PURPLE_900);

    private final TextInput iFirstName = new TextInput();
    private final TextInput iLastName = new TextInput();
    private final TextInput iEmail = new TextInput();
    private final TextInput iPhone = new TextInput();
    private final TextInput iPassport = new TextInput();
    private final TextInput iBirthDate = new TextInput();

    private void createSeatButtons() {
        for (var seat : this.vm.flight.get().getData().getAirCraft().getSeats()) {
            var button = new Button(seat.getSeatNumber(),
                    seat.getSeatClass() == SeatClass.BUSINESS ? MaterialColors.BROWN_500 : MaterialColors.ORANGE_400,
                    MaterialColors.WHITE,
                    35, 20);
            button.setPreferredSize(new Dimension(50, 35));
            if (this.vm.selectedSeats.get().contains(seat)) {
                button.setBackground(MaterialColors.DEEP_PURPLE_400);
            }
            if (seat.getSeatClass().name().equals(this.vm.selectedSeatClass.get()) || this.vm.selectedSeatClass.get().equals("ALL SEATS")) {
                button.addActionListener(e -> {
                    this.vm.selectedSeats.add(seat);
                });
                if (seat.getStatus().equals(SeatStatus.RESERVED)) button.setBackground(MaterialColors.RED_300);
                this.seatsPanel.add(button);
            } else continue;
        }

        this.seatsPanel.revalidate();
        this.seatsPanel.repaint();

    }

}