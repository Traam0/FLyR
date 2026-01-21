package mvvm.views.flight;

import core.abstraction.ViewBase;
import mvvm.models.SeatClass;
import mvvm.viewModels.FlightDetailViewModel;
import core.navigation.Router;
import shared.common.FlexAlignment;
import shared.common.MaterialColors;
import shared.components.Label;
import shared.components.TopBar;
import shared.layouts.FlexPanelH;
import shared.layouts.FlexPanelV;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class FlightDetailView extends ViewBase {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FlightDetailViewModel vm;
    private final Logger logger;
    private final Router router;
    private FlexPanelV mainPanel;

    public FlightDetailView(FlightDetailViewModel viewModel, Logger logger, Router router) {
        this.vm = viewModel;
        this.logger = logger;
        this.router = router;

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
        informationPanel.setPreferredSize(new Dimension(mainPanel.getPreferredSize().width, informationPanel.getPreferredSize().height));

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
                    this.lFlightNumber.setText(data.getFlightNumber());
                    this.lRoute.setText(String.format("%s → %s", data.getDepartureCity(), data.getDestinationCity()));
                    this.lDepartureDate.setText(this.dateFormatter.format(data.getDepartureDateTime()));
                    this.lAirCraft.setText(String.format("%s (%s)", data.getAirCraft().getModel(), data.getAirCraft().getCode()));
                    this.lCapacity.setText(String.format("Total %d (Business: %d, Economy: %d)", data.getAirCraft().getTotalCapacity(), data.getAirCraft().getBusinessCapacity(), data.getAirCraft().getEconomyCapacity()));
                    break;
                case LOADING:
                    this.lFlightNumber.setText("still loading");
                    break;
                case ERROR:
                    this.lFlightNumber.setText(n.getMessage());
                    break;
            }
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

    private final Label lFlightNumber = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lRoute = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lDepartureDate = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lAirCraft = new Label("loading ...", MaterialColors.PURPLE_900);
    private final Label lCapacity = new Label("loading ...", MaterialColors.PURPLE_900);
    private final JComboBox<String> iSeatClass = new JComboBox<>(new String[]{"ALL SEATS", SeatClass.BUSINESS.name(), SeatClass.ECONOMY.name()});
    private final Label lSelectedSeats = new Label("0", MaterialColors.PURPLE_900);
    private final Label ltotalPrice = new Label("0", MaterialColors.PURPLE_900);


}