package mvvm.views.flight;

import core.abstraction.ViewBase;
import mvvm.models.SeatClass;
import mvvm.viewModels.FlightDetailViewModel;
import core.navigation.Router;
import shared.common.FlexAlignment;
import shared.common.MaterialColors;
import shared.components.TopBar;
import shared.layouts.FlexPanelH;
import shared.layouts.FlexPanelV;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.logging.Logger;

public class FlightDetailView extends ViewBase {
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
        this.vm.flight.subscribe((o, n) -> {
            this.lFlightNumber.setText(n.getData().getFlightNumber());
        });
    }

    private FlexPanelV createInformationPanel() {
        var panel = new FlexPanelV(10, FlexAlignment.TOP, MaterialColors.WHITE);
        var pFlightInfo = new FlexPanelV(5, FlexAlignment.TOP, MaterialColors.WHITE);
        pFlightInfo.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Flight's Information",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLACK
        ), new EmptyBorder(10, 15, 10, 15)));

        var pFlightNumber = new FlexPanelH(10, FlexAlignment.LEFT);
        pFlightNumber.add(new JLabel("Flight Number: "));
        pFlightNumber.add(this.lFlightNumber);
        pFlightInfo.add(pFlightNumber);
        var pRoute = new FlexPanelH(10, FlexAlignment.LEFT);
        pRoute.add(new JLabel("Route: "));
        pRoute.add(this.lRoute);
        pFlightInfo.add(pRoute);
        var pDepartureDate = new FlexPanelH(10, FlexAlignment.LEFT);
        pDepartureDate.add(new JLabel("Departure Date: "));
        pDepartureDate.add(this.lDepartureDate);
        pFlightInfo.add(pDepartureDate);
        var pAirCraft = new FlexPanelH(10, FlexAlignment.LEFT);
        pAirCraft.add(new JLabel("Air Craft: "));
        pAirCraft.add(this.lAirCraft);
        pFlightInfo.add(pAirCraft);
        var pCapacity = new FlexPanelH(10, FlexAlignment.LEFT);
        pCapacity.add(new JLabel("Capacity: "));
        pCapacity.add(this.lCapacity);
        pFlightInfo.add(pCapacity);

        panel.add(pFlightInfo);

        var pOverview = new FlexPanelV(10, FlexAlignment.TOP, MaterialColors.WHITE);
        pOverview.setBorder(new CompoundBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MaterialColors.GREY_300),
                "Overview",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                MaterialColors.BLACK
        ), new EmptyBorder(10, 15, 10, 15)));
        var pSeatClass = new FlexPanelH(10, FlexAlignment.LEFT);
        pSeatClass.add(new JLabel("Seat Class: "));
        pSeatClass.add(this.iSeatClass);
        pOverview.add(pSeatClass);
        var pSelectedSeats = new FlexPanelH(10, FlexAlignment.LEFT);
        pSelectedSeats.add(new JLabel("Selected Seats: "));
        pSelectedSeats.add(this.lSelectedSeats);
        pOverview.add(pSelectedSeats);
        var pTotalPrice = new FlexPanelH(10, FlexAlignment.LEFT);
        pTotalPrice.add(new JLabel("Total Price: "));
        pTotalPrice.add(this.ltotalPrice);
        pOverview.add(pTotalPrice);

        panel.add(pOverview);
        return panel;
    }

    private final JLabel lFlightNumber = new JLabel("loading ...");
    private final JLabel lRoute = new JLabel("loading ...");
    private final JLabel lDepartureDate = new JLabel("loading ...");
    private final JLabel lAirCraft = new JLabel("loading ...");
    private final JLabel lCapacity = new JLabel("loading ...");
    private final JComboBox<String> iSeatClass = new JComboBox<>(new String[]{"ALL SEATS", SeatClass.BUSINESS.name(), SeatClass.ECONOMY.name()});
    private final JLabel lSelectedSeats = new JLabel("0");
    private final JLabel ltotalPrice = new JLabel("0");

}